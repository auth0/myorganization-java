package com.auth0.client.myorganization.auth;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrivateKeyJwtTokenProviderTest {

    private MockWebServer server;
    private OkHttpClient httpClient;
    private String rsaPrivateKeyPEM;

    @BeforeEach
    public void setup() throws Exception {
        server = new MockWebServer();
        server.start();
        httpClient = new OkHttpClient();
        rsaPrivateKeyPEM = generateRSAPrivateKeyPEM();
    }

    @AfterEach
    public void teardown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testTokenRequestIncludesTelemetryHeaders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"jwt-token\",\"expires_in\":3600,\"token_type\":\"Bearer\"}"));

        PrivateKeyJwtTokenProvider provider = new PrivateKeyJwtTokenProvider(
                httpClient,
                server.url("/oauth/token").toString(),
                "test-client-id",
                rsaPrivateKeyPEM,
                "RS256",
                "https://example.com/api/",
                null);

        Token token = provider.getToken();
        Assertions.assertNotNull(token);
        Assertions.assertEquals("jwt-token", token.getValue());

        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());

        String auth0Client = request.getHeader("Auth0-Client");
        Assertions.assertNotNull(auth0Client, "Auth0-Client header must be present on token request");
        Assertions.assertEquals(Auth0ClientTelemetry.auth0ClientHeader(), auth0Client);

        String userAgent = request.getHeader("User-Agent");
        Assertions.assertNotNull(userAgent, "User-Agent header must be present on token request");
        Assertions.assertTrue(
                userAgent.contains(Auth0ClientTelemetry.userAgent()), "User-Agent should contain SDK telemetry value");
    }

    @Test
    public void testTokenRequestIncludesClientAssertion() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"jwt-token\",\"expires_in\":3600,\"token_type\":\"Bearer\"}"));

        PrivateKeyJwtTokenProvider provider = new PrivateKeyJwtTokenProvider(
                httpClient,
                server.url("/oauth/token").toString(),
                "test-client-id",
                rsaPrivateKeyPEM,
                "RS256",
                "https://example.com/api/",
                null);

        provider.getToken();

        RecordedRequest request = server.takeRequest();
        String body = request.getBody().readUtf8();
        Assertions.assertTrue(body.contains("\"client_assertion\""), "Request body should include client_assertion");
        Assertions.assertTrue(
                body.contains("\"client_assertion_type\""), "Request body should include client_assertion_type");
        Assertions.assertTrue(
                body.contains("urn:ietf:params:oauth:client-assertion-type:jwt-bearer"),
                "client_assertion_type should be jwt-bearer");
    }

    @Test
    public void testTokenCachingReturnsCachedToken() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"cached-jwt\",\"expires_in\":3600,\"token_type\":\"Bearer\"}"));

        PrivateKeyJwtTokenProvider provider = new PrivateKeyJwtTokenProvider(
                httpClient,
                server.url("/oauth/token").toString(),
                "test-client-id",
                rsaPrivateKeyPEM,
                "RS256",
                "https://example.com/api/",
                null);

        Token first = provider.getToken();
        Token second = provider.getToken();

        Assertions.assertSame(first, second, "Second call should return the cached token");
        Assertions.assertEquals(1, server.getRequestCount(), "Only one HTTP request should be made");
    }

    private static String generateRSAPrivateKeyPEM() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        String base64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" + base64 + "\n-----END PRIVATE KEY-----";
    }
}
