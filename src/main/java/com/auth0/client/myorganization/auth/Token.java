package com.auth0.client.myorganization.auth;

/**
 * Represents an access token with optional expiration information.
 */
public final class Token {

    private final String value;
    private final long expiresAtMs;

    private Token(String value, long expiresAtMs) {
        this.value = value;
        this.expiresAtMs = expiresAtMs;
    }

    /**
     * Creates a token that never expires (e.g., a static token).
     */
    public static Token of(String value) {
        return new Token(value, Long.MAX_VALUE);
    }

    /**
     * Creates a token with an expiration time.
     *
     * @param value       the token string
     * @param expiresAtMs expiration time in milliseconds since epoch
     */
    public static Token of(String value, long expiresAtMs) {
        return new Token(value, expiresAtMs);
    }

    public String getValue() {
        return value;
    }

    public long getExpiresAtMs() {
        return expiresAtMs;
    }

    /**
     * Returns true if the token is expired or will expire within the given buffer.
     *
     * @param bufferMs buffer time in milliseconds before actual expiry
     */
    public boolean isExpired(long bufferMs) {
        return System.currentTimeMillis() >= (expiresAtMs - bufferMs);
    }
}
