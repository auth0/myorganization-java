package com.auth0.client.myorganization.auth;

import java.io.IOException;

/**
 * Provides access tokens for authenticating API requests.
 *
 * <p>Implementations handle token acquisition, caching, and refresh. The SDK calls
 * {@link #getToken()} before each HTTP request to obtain a valid token.
 *
 * <p>This is a functional interface, so lambdas can be used for simple cases:
 * <pre>{@code
 * TokenProvider provider = () -> Token.of(myStaticToken);
 * }</pre>
 */
@FunctionalInterface
public interface TokenProvider {

    /**
     * Returns a valid access token.
     *
     * @return a non-null {@link Token}
     * @throws IOException if the token cannot be obtained (e.g., network error)
     */
    Token getToken() throws IOException;
}
