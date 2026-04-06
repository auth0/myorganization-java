package com.auth0.client.myorganization.auth;

/**
 * Utilities for Auth0 domain sanitization and URL derivation.
 *
 * <p>Mirrors the Go SDK's {@code internal/auth0/auth0.go}.
 */
public final class Auth0Domain {

    private Auth0Domain() {}

    /**
     * Strips protocol prefixes ({@code https://}, {@code http://}) and trailing slashes from the domain.
     * Loops to handle edge cases like {@code "http://http://example.com"}.
     *
     * @param domain the raw domain string
     * @return the sanitized domain (e.g., {@code "acme.auth0.com"})
     */
    public static String sanitize(String domain) {
        String d = domain;
        while (true) {
            String trimmed = d;
            if (trimmed.startsWith("https://")) {
                trimmed = trimmed.substring("https://".length());
            }
            if (trimmed.startsWith("http://")) {
                trimmed = trimmed.substring("http://".length());
            }
            if (trimmed.equals(d)) {
                break;
            }
            d = trimmed;
        }
        while (d.endsWith("/")) {
            d = d.substring(0, d.length() - 1);
        }
        return d;
    }

    /**
     * Derives the base URL from a sanitized domain.
     *
     * @return {@code https://{domain}/my-org}
     */
    public static String deriveBaseURL(String sanitizedDomain) {
        return "https://" + sanitizedDomain + "/my-org";
    }

    /**
     * Derives the OAuth2 token endpoint URL from a sanitized domain.
     *
     * @return {@code https://{domain}/oauth/token}
     */
    public static String deriveTokenURL(String sanitizedDomain) {
        return "https://" + sanitizedDomain + "/oauth/token";
    }

    /**
     * Derives the default API audience from a sanitized domain.
     *
     * @return {@code https://{domain}/my-org/}
     */
    public static String deriveAudience(String sanitizedDomain) {
        return "https://" + sanitizedDomain + "/my-org/";
    }
}
