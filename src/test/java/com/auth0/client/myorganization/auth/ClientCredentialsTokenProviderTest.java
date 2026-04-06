package com.auth0.client.myorganization.auth;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClientCredentialsTokenProviderTest {

    private MockWebServer server;
    private OkHttpClient httpClient;

    @BeforeEach
    public void setup() throws Exception {
        server = new MockWebServer();
        server.start();
        httpClient = new OkHttpClient();
    }

    @AfterEach
    public void teardown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testTokenRequestIncludesTelemetryHeaders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"test-token\",\"expires_in\":3600,\"token_type\":\"Bearer\"}"));

        ClientCredentialsTokenProvider provider = new ClientCredentialsTokenProvider(
                httpClient,
                server.url("/oauth/token").toString(),
                "test-client-id",
                "test-client-secret",
                "https://example.com/api/",
                null);

        Token token = provider.getToken();
        Assertions.assertNotNull(token);
        Assertions.assertEquals("test-token", token.getValue());

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
    public void testTokenRequestIncludesOrganization() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"org-token\",\"expires_in\":3600,\"token_type\":\"Bearer\"}"));

        ClientCredentialsTokenProvider provider = new ClientCredentialsTokenProvider(
                httpClient,
                server.url("/oauth/token").toString(),
                "test-client-id",
                "test-client-secret",
                "https://example.com/api/",
                "org_abc123");

        Token token = provider.getToken();
        Assertions.assertEquals("org-token", token.getValue());

        RecordedRequest request = server.takeRequest();
        String body = request.getBody().readUtf8();
        Assertions.assertTrue(body.contains("\"organization\""), "Request body should include organization");
        Assertions.assertTrue(body.contains("org_abc123"), "Request body should include organization value");
    }

    @Test
    public void testTokenCachingReturnsCachedToken() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"cached-token\",\"expires_in\":3600,\"token_type\":\"Bearer\"}"));

        ClientCredentialsTokenProvider provider = new ClientCredentialsTokenProvider(
                httpClient,
                server.url("/oauth/token").toString(),
                "test-client-id",
                "test-client-secret",
                "https://example.com/api/",
                null);

        Token first = provider.getToken();
        Token second = provider.getToken();

        Assertions.assertSame(first, second, "Second call should return the cached token");
        Assertions.assertEquals(1, server.getRequestCount(), "Only one HTTP request should be made");
    }
}
