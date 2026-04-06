package com.auth0.client.myorganization.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A {@link TokenProvider} that uses Private Key JWT authentication (client_assertion)
 * to obtain access tokens from the Auth0 token endpoint.
 *
 * <p>Creates a signed JWT assertion on each token fetch and exchanges it via the
 * {@code client_credentials} grant with {@code client_assertion} parameters.
 *
 * <p>Tokens are cached and reused until 60 seconds before expiry. Thread-safe.
 *
 * <p>Supported signing algorithms: RS256, RS384, RS512, ES256, ES384, ES512.
 *
 */
public final class PrivateKeyJwtTokenProvider implements TokenProvider {

    private static final long EXPIRY_BUFFER_MS = 60_000;
    private static final long ASSERTION_LIFETIME_MS = 120_000; // 2 minutes
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Map<String, String> SUPPORTED_ALGORITHMS;

    static {
        Map<String, String> algs = new HashMap<>();
        algs.put("RS256", "RSA");
        algs.put("RS384", "RSA");
        algs.put("RS512", "RSA");
        algs.put("ES256", "EC");
        algs.put("ES384", "EC");
        algs.put("ES512", "EC");
        SUPPORTED_ALGORITHMS = Collections.unmodifiableMap(algs);
    }

    private final OkHttpClient httpClient;
    private final String tokenURL;
    private final String clientId;
    private final String audience;
    private final String organization;
    private final String signingAlgorithm;
    private final java.security.PrivateKey privateKey;

    private volatile Token cachedToken;

    public PrivateKeyJwtTokenProvider(
            OkHttpClient httpClient,
            String tokenURL,
            String clientId,
            String privateKeyPEM,
            String signingAlgorithm,
            String audience,
            String organization) {
        this.httpClient = httpClient;
        this.tokenURL = tokenURL;
        this.clientId = clientId;
        this.audience = audience;
        this.organization = organization;
        this.signingAlgorithm = signingAlgorithm.toUpperCase();

        String keyType = SUPPORTED_ALGORITHMS.get(this.signingAlgorithm);
        if (keyType == null) {
            throw new IllegalArgumentException("auth0: unsupported signing algorithm \"" + signingAlgorithm
                    + "\"; supported: RS256, RS384, RS512, ES256, ES384, ES512");
        }

        this.privateKey = parsePrivateKey(privateKeyPEM, keyType);
    }

    @Override
    public Token getToken() throws IOException {
        Token current = cachedToken;
        if (current != null && !current.isExpired(EXPIRY_BUFFER_MS)) {
            return current;
        }
        synchronized (this) {
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
        String assertion = createClientAssertion();

        Map<String, String> requestData = new HashMap<>();
        requestData.put("grant_type", "client_credentials");
        requestData.put("client_id", clientId);
        requestData.put("client_assertion", assertion);
        requestData.put("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
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
                        "auth0: failed to fetch private key JWT token: HTTP " + response.code() + " " + errBody);
            }

            JsonNode json = MAPPER.readTree(response.body().string());
            String accessToken = json.get("access_token").asText();
            long expiresIn = json.has("expires_in") ? json.get("expires_in").asLong() : 3600;
            long expiresAtMs = System.currentTimeMillis() + (expiresIn * 1000);

            return Token.of(accessToken, expiresAtMs);
        }
    }

    private String createClientAssertion() {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ASSERTION_LIFETIME_MS);
        String aud = audienceFromTokenURL(tokenURL);

        Algorithm algorithm = resolveAlgorithm(signingAlgorithm, privateKey);

        return JWT.create()
                .withIssuer(clientId)
                .withSubject(clientId)
                .withAudience(aud)
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(now)
                .withNotBefore(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    private static Algorithm resolveAlgorithm(String alg, java.security.PrivateKey key) {
        switch (alg) {
            case "RS256":
                return Algorithm.RSA256(null, (RSAPrivateKey) key);
            case "RS384":
                return Algorithm.RSA384(null, (RSAPrivateKey) key);
            case "RS512":
                return Algorithm.RSA512(null, (RSAPrivateKey) key);
            case "ES256":
                return Algorithm.ECDSA256(null, (ECPrivateKey) key);
            case "ES384":
                return Algorithm.ECDSA384(null, (ECPrivateKey) key);
            case "ES512":
                return Algorithm.ECDSA512(null, (ECPrivateKey) key);
            default:
                throw new IllegalArgumentException("auth0: unsupported algorithm: " + alg);
        }
    }

    private static java.security.PrivateKey parsePrivateKey(String pem, String keyType) {
        try {
            String stripped = pem.replaceAll("-----BEGIN .*-----", "")
                    .replaceAll("-----END .*-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(stripped);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance(keyType);
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("auth0: failed to parse private key: " + e.getMessage(), e);
        }
    }

    static String audienceFromTokenURL(String tokenURL) {
        try {
            URI uri = URI.create(tokenURL);
            return uri.getScheme() + "://" + uri.getHost() + "/";
        } catch (Exception e) {
            return tokenURL;
        }
    }
}
