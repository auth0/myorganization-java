package com.auth0.client.myorganization.auth;

import com.auth0.client.myorganization.MyOrganizationApi;
import com.auth0.client.myorganization.MyOrganizationApiBuilder;
import com.auth0.client.myorganization.core.ClientOptions;
import com.auth0.client.myorganization.core.Environment;
import com.auth0.client.myorganization.core.LogConfig;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import okhttp3.OkHttpClient;

/**
 * Auth0 My Organization API client with automatic token management.
 *
 * <p>Wraps the Fern-generated {@link MyOrganizationApi} with dynamic authentication,
 * Auth0 telemetry headers, and domain-based URL derivation.
 *
 * <p>Example — client credentials:
 * <pre>{@code
 * MyOrganizationClient client = MyOrganizationClient.builder()
 *     .domain("acme.auth0.com")
 *     .clientCredentials("clientId", "clientSecret")
 *     .organization("org_...")
 *     .build();
 *
 * OrgDetails details = client.organizationDetails().get();
 * }</pre>
 *
 * <p>Example — custom token provider:
 * <pre>{@code
 * MyOrganizationClient client = MyOrganizationClient.builder()
 *     .domain("acme.auth0.com")
 *     .tokenProvider(myCustomProvider)
 *     .build();
 * }</pre>
 */
public final class MyOrganizationClient extends MyOrganizationApi {

    private final TokenProvider tokenProvider;

    private MyOrganizationClient(ClientOptions clientOptions, TokenProvider tokenProvider) {
        super(clientOptions);
        this.tokenProvider = tokenProvider;
    }

    /**
     * Creates a new builder for {@link MyOrganizationClient}.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends MyOrganizationApiBuilder {

        private String domain;
        private String audience;
        private String organization;
        private TokenProvider customTokenProvider;
        private String clientId;
        private String clientSecret;
        private String privateKeyPEM;
        private String signingAlgorithm;
        private String staticTokenValue;
        private OkHttpClient auth0HttpClient;
        private final Map<String, String> auth0Headers = new HashMap<>();

        private TokenProvider resolvedTokenProvider;

        /**
         * The Auth0 tenant domain (e.g., "acme.auth0.com").
         */
        public Builder domain(String domain) {
            this.domain = Objects.requireNonNull(domain, "domain must not be null");
            return this;
        }

        /**
         * Use OAuth2 client credentials for authentication.
         */
        public Builder clientCredentials(String clientId, String clientSecret) {
            this.clientId = Objects.requireNonNull(clientId, "clientId must not be null");
            this.clientSecret = Objects.requireNonNull(clientSecret, "clientSecret must not be null");
            return this;
        }

        /**
         * Use Private Key JWT (client_assertion) for authentication.
         *
         * @param clientId         the OAuth client ID
         * @param privateKeyPEM    PEM-encoded private key
         * @param signingAlgorithm algorithm (RS256, RS384, RS512, ES256, ES384, ES512)
         */
        public Builder privateKeyJwt(String clientId, String privateKeyPEM, String signingAlgorithm) {
            this.clientId = Objects.requireNonNull(clientId, "clientId must not be null");
            this.privateKeyPEM = Objects.requireNonNull(privateKeyPEM, "privateKeyPEM must not be null");
            this.signingAlgorithm = Objects.requireNonNull(signingAlgorithm, "signingAlgorithm must not be null");
            return this;
        }

        /**
         * Use a static bearer token for authentication.
         */
        public Builder staticToken(String token) {
            this.staticTokenValue = Objects.requireNonNull(token, "token must not be null");
            return this;
        }

        /**
         * Use a pre-built {@link TokenProvider} for authentication.
         */
        public Builder tokenProvider(TokenProvider tokenProvider) {
            this.customTokenProvider = Objects.requireNonNull(tokenProvider, "tokenProvider must not be null");
            return this;
        }

        /**
         * Custom API audience. Defaults to {@code https://{domain}/my-org/}.
         */
        public Builder audience(String audience) {
            this.audience = audience;
            return this;
        }

        /**
         * Auth0 organization name or ID.
         */
        public Builder organization(String organization) {
            this.organization = organization;
            return this;
        }

        // ---- Covariant overrides for method chaining ----

        @Override
        public Builder timeout(int timeout) {
            super.timeout(timeout);
            return this;
        }

        @Override
        public Builder maxRetries(int maxRetries) {
            super.maxRetries(maxRetries);
            return this;
        }

        @Override
        public Builder httpClient(OkHttpClient httpClient) {
            this.auth0HttpClient = httpClient;
            super.httpClient(httpClient);
            return this;
        }

        @Override
        public Builder logging(LogConfig logging) {
            super.logging(logging);
            return this;
        }

        @Override
        public Builder addHeader(String name, String value) {
            this.auth0Headers.put(name, value);
            return this;
        }

        /**
         * Sets a static bearer token. Delegates to {@link #staticToken(String)}.
         */
        @Override
        public Builder token(String token) {
            return staticToken(token);
        }

        /**
         * Sets the tenant domain. Delegates to {@link #domain(String)}.
         */
        @Override
        public Builder tenantDomain(String tenantDomain) {
            return domain(tenantDomain);
        }

        // ---- Hook overrides ----

        @Override
        protected void setEnvironment(ClientOptions.Builder builder) {
            String sanitized = Auth0Domain.sanitize(domain);
            builder.environment(Environment.custom(Auth0Domain.deriveBaseURL(sanitized)));
        }

        @Override
        protected void setAuthentication(ClientOptions.Builder builder) {
            builder.addHeader("Authorization", () -> {
                try {
                    return "Bearer " + resolvedTokenProvider.getToken().getValue();
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to obtain access token", e);
                }
            });
        }

        @Override
        protected void setAdditional(ClientOptions.Builder builder) {
            builder.addHeader("Auth0-Client", Auth0ClientTelemetry.auth0ClientHeader());
            builder.addHeader("User-Agent", Auth0ClientTelemetry.userAgent());

            for (Map.Entry<String, String> entry : auth0Headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        @Override
        protected void validateConfiguration() {
            if (domain == null || domain.trim().isEmpty()) {
                throw new IllegalArgumentException("domain is required");
            }
            Auth0OptionsValidator.validate(
                    domain,
                    clientId,
                    clientSecret,
                    privateKeyPEM,
                    signingAlgorithm,
                    staticTokenValue,
                    customTokenProvider);
        }

        @Override
        public MyOrganizationClient build() {
            validateConfiguration();

            String sanitized = Auth0Domain.sanitize(domain);
            String tokenURL = Auth0Domain.deriveTokenURL(sanitized);
            String resolvedAudience = audience != null ? audience : Auth0Domain.deriveAudience(sanitized);

            this.resolvedTokenProvider = resolveTokenProvider(tokenURL, resolvedAudience);

            ClientOptions clientOptions = buildClientOptions();
            return new MyOrganizationClient(clientOptions, resolvedTokenProvider);
        }

        private TokenProvider resolveTokenProvider(String tokenURL, String audience) {
            if (customTokenProvider != null) {
                return new DelegateTokenProvider(customTokenProvider);
            }
            if (clientSecret != null) {
                OkHttpClient http = auth0HttpClient != null ? auth0HttpClient : new OkHttpClient();
                return new ClientCredentialsTokenProvider(
                        http, tokenURL, clientId, clientSecret, audience, organization);
            }
            if (privateKeyPEM != null) {
                OkHttpClient http = auth0HttpClient != null ? auth0HttpClient : new OkHttpClient();
                return new PrivateKeyJwtTokenProvider(
                        http, tokenURL, clientId, privateKeyPEM, signingAlgorithm, audience, organization);
            }
            if (staticTokenValue != null) {
                return new StaticTokenProvider(staticTokenValue);
            }
            throw new IllegalStateException("No authentication method configured");
        }
    }
}
