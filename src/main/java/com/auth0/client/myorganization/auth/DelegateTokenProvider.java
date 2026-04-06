package com.auth0.client.myorganization.auth;

import java.io.IOException;
import java.util.Objects;

/**
 * A {@link TokenProvider} backed by a user-supplied delegate.
 *
 * <p>Use this to retrieve tokens from an external source such as a secret manager,
 * cache, or any custom logic. The delegate is invoked on every call; no caching
 * is performed by this class.
 *
 * <pre>{@code
 * TokenProvider provider = new DelegateTokenProvider(() -> Token.of(fetchFromVault()));
 * }</pre>
 */
public final class DelegateTokenProvider implements TokenProvider {

    private final TokenProvider delegate;

    /**
     * @param delegate the underlying provider to invoke on each call
     * @throws NullPointerException if delegate is null
     */
    public DelegateTokenProvider(TokenProvider delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
    }

    @Override
    public Token getToken() throws IOException {
        Token token = delegate.getToken();
        if (token == null || token.getValue() == null || token.getValue().isEmpty()) {
            throw new IOException("Token provider returned a null or empty token");
        }
        return token;
    }
}
