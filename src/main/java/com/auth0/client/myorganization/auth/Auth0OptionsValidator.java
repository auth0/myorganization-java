package com.auth0.client.myorganization.auth;

/**
 * Validates Auth0 client configuration before building the client.
 *
 */
public final class Auth0OptionsValidator {

    private Auth0OptionsValidator() {}

    /**
     * Validates that exactly one authentication mode is configured and all required fields are present.
     *
     * @param domain          the Auth0 tenant domain
     * @param clientId        client ID (used by client credentials and private key JWT)
     * @param clientSecret    client secret (used by client credentials)
     * @param privateKeyPEM   PEM-encoded private key (used by private key JWT)
     * @param signingAlgorithm signing algorithm (used by private key JWT)
     * @param staticToken     static bearer token
     * @param tokenProvider   custom token provider
     * @throws IllegalArgumentException if the configuration is invalid
     */
    public static void validate(
            String domain,
            String clientId,
            String clientSecret,
            String privateKeyPEM,
            String signingAlgorithm,
            String staticToken,
            TokenProvider tokenProvider) {

        if (domain == null || domain.trim().isEmpty()) {
            throw new IllegalArgumentException("auth0: domain must not be empty");
        }

        boolean hasClientCredentials = clientSecret != null && !clientSecret.isEmpty();
        boolean hasPrivateKeyJwt = privateKeyPEM != null && !privateKeyPEM.isEmpty();
        boolean hasStaticToken = staticToken != null && !staticToken.isEmpty();
        boolean hasTokenProvider = tokenProvider != null;

        // ClientID alone (without ClientSecret or PrivateKeyPEM) is incomplete client credentials.
        boolean hasOrphanClientId =
                clientId != null && !clientId.isEmpty() && !hasClientCredentials && !hasPrivateKeyJwt;

        int count = 0;
        if (hasClientCredentials || hasOrphanClientId) count++;
        if (hasPrivateKeyJwt) count++;
        if (hasStaticToken) count++;
        if (hasTokenProvider) count++;

        if (count > 1) {
            throw new IllegalArgumentException("auth0: only one authentication mode may be used "
                    + "(client credentials, private key JWT, token provider, or static token)");
        }
        if (count == 0) {
            throw new IllegalArgumentException("auth0: must provide either client credentials (withClientCredentials), "
                    + "private key JWT (withPrivateKeyJWT), a token provider (withTokenProvider), "
                    + "or a static token (withStaticToken)");
        }

        if (hasClientCredentials || hasOrphanClientId) {
            if (clientId == null || clientId.isEmpty()) {
                throw new IllegalArgumentException("auth0: client credentials: client ID must not be empty");
            }
            if (clientSecret == null || clientSecret.isEmpty()) {
                throw new IllegalArgumentException("auth0: client credentials: client secret must not be empty");
            }
        }

        if (hasPrivateKeyJwt) {
            if (clientId == null || clientId.isEmpty()) {
                throw new IllegalArgumentException("auth0: private key JWT: client ID must not be empty");
            }
            if (signingAlgorithm == null || signingAlgorithm.isEmpty()) {
                throw new IllegalArgumentException("auth0: private key JWT: signing algorithm must not be empty");
            }
        }
    }
}
