package com.auth0.client.myorganization.auth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * SDK telemetry constants and the {@code Auth0-Client} header builder.
 *
 */
public final class Auth0ClientTelemetry {

    public static final String SDK_NAME = "myorganization-java";
    public static final String SDK_VERSION = loadVersion();

    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final String AUTH0_CLIENT_HEADER = buildHeaderValue();
    private static final String USER_AGENT = SDK_NAME + "/" + SDK_VERSION;

    private Auth0ClientTelemetry() {}

    /**
     * Returns the pre-computed {@code Auth0-Client} header value (Base64 URL-encoded JSON).
     */
    public static String auth0ClientHeader() {
        return AUTH0_CLIENT_HEADER;
    }

    /**
     * Returns the {@code User-Agent} header value.
     */
    public static String userAgent() {
        return USER_AGENT;
    }

    private static String buildHeaderValue() {
        String json = "{\"name\":\"" + SDK_NAME + "\""
                + ",\"version\":\"" + SDK_VERSION + "\""
                + ",\"env\":{\"java\":\"" + JAVA_VERSION + "\"}"
                + "}";
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    private static String loadVersion() {
        try (InputStream is = Auth0ClientTelemetry.class.getResourceAsStream("/.version")) {
            if (is != null) {
                String raw = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .readLine()
                        .trim();
                return raw.startsWith("v") ? raw.substring(1) : raw;
            }
        } catch (Exception ignored) {
        }
        return "0.0.0";
    }
}
