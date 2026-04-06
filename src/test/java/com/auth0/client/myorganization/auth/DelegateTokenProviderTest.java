package com.auth0.client.myorganization.auth;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DelegateTokenProviderTest {

    @Test
    public void testValidTokenIsAccepted() throws Exception {
        TokenProvider delegate = () -> Token.of("valid-token");
        DelegateTokenProvider provider = new DelegateTokenProvider(delegate);

        Token token = provider.getToken();
        Assertions.assertNotNull(token);
        Assertions.assertEquals("valid-token", token.getValue());
    }

    @Test
    public void testNullTokenThrowsIOException() {
        TokenProvider delegate = () -> null;
        DelegateTokenProvider provider = new DelegateTokenProvider(delegate);

        IOException ex = Assertions.assertThrows(IOException.class, provider::getToken);
        Assertions.assertTrue(
                ex.getMessage().contains("null or empty"), "Exception message should mention null or empty token");
    }

    @Test
    public void testEmptyTokenValueThrowsIOException() {
        TokenProvider delegate = () -> Token.of("");
        DelegateTokenProvider provider = new DelegateTokenProvider(delegate);

        IOException ex = Assertions.assertThrows(IOException.class, provider::getToken);
        Assertions.assertTrue(
                ex.getMessage().contains("null or empty"), "Exception message should mention null or empty token");
    }

    @Test
    public void testNullTokenValueThrowsIOException() {
        TokenProvider delegate = () -> Token.of(null);
        DelegateTokenProvider provider = new DelegateTokenProvider(delegate);

        IOException ex = Assertions.assertThrows(IOException.class, provider::getToken);
        Assertions.assertTrue(
                ex.getMessage().contains("null or empty"), "Exception message should mention null or empty token");
    }

    @Test
    public void testNullDelegateThrowsNPE() {
        Assertions.assertThrows(NullPointerException.class, () -> new DelegateTokenProvider(null));
    }

    @Test
    public void testDelegateExceptionPropagates() {
        TokenProvider delegate = () -> {
            throw new IOException("upstream failure");
        };
        DelegateTokenProvider provider = new DelegateTokenProvider(delegate);

        IOException ex = Assertions.assertThrows(IOException.class, provider::getToken);
        Assertions.assertEquals("upstream failure", ex.getMessage());
    }
}
