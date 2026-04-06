package com.auth0.client.myorganization.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A {@link TokenProvider} that uses the OAuth2 client_credentials grant to obtain
 * access tokens from the Auth0 token endpoint.
 *
 * <p>Tokens are cached and reused until 60 seconds before expiry. Thread-safe.
 *
 */
public final class ClientCredentialsTokenProvider implements TokenProvider {

    private static final long EXPIRY_BUFFER_MS = 60_000;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final OkHttpClient httpClient;
    private final String tokenURL;
    private final String clientId;
    private final String clientSecret;
    private final String audience;
    private final String organization;

    private volatile Token cachedToken;

    public ClientCredentialsTokenProvider(
            OkHttpClient httpClient,
            String tokenURL,
            String clientId,
            String clientSecret,
            String audience,
            String organization) {
        this.httpClient = httpClient;
        this.tokenURL = tokenURL;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.audience = audience;
        this.organization = organization;
    }

    @Override
    public Token getToken() throws IOException {
        Token current = cachedToken;
        if (current != null && !current.isExpired(EXPIRY_BUFFER_MS)) {
            return current;
        }
        synchronized (this) {
            // Double-check after acquiring lock.
            current = cachedToken;
            if (current != null && !current.isExpired(EXPIRY_BUFFER_MS)) {
                return current;
            }
            Token fresh = fetchToken();
            cachedToken = fresh;
            return fresh;
        }
    }

    private Token fetchToken() throws IOException {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("grant_type", "client_credentials");
        requestData.put("client_id", clientId);
        requestData.put("client_secret", clientSecret);
        requestData.put("audience", audience);

        if (organization != null && !organization.isEmpty()) {
            requestData.put("organization", organization);
        }

        RequestBody body = RequestBody.create(MAPPER.writeValueAsBytes(requestData), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(tokenURL)
                .post(body)
                .addHeader("Auth0-Client", Auth0ClientTelemetry.auth0ClientHeader())
                .addHeader("User-Agent", Auth0ClientTelemetry.userAgent())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                String errBody = response.body() != null ? response.body().string() : "";
                throw new IOException(
                        "auth0: failed to fetch client credentials token: HTTP " + response.code() + " " + errBody);
            }

            JsonNode json = MAPPER.readTree(response.body().string());
            String accessToken = json.get("access_token").asText();
            long expiresIn = json.has("expires_in") ? json.get("expires_in").asLong() : 3600;
            long expiresAtMs = System.currentTimeMillis() + (expiresIn * 1000);

            return Token.of(accessToken, expiresAtMs);
        }
    }
}
