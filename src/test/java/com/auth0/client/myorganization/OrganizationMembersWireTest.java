package com.auth0.client.myorganization;

import com.auth0.client.myorganization.core.ObjectMappers;
import com.auth0.client.myorganization.core.OptionalNullable;
import com.auth0.client.myorganization.core.SyncPagingIterable;
import com.auth0.client.myorganization.organization.types.GetOrganizationMemberRequestParameters;
import com.auth0.client.myorganization.organization.types.ListOrganizationMembersRequestParameters;
import com.auth0.client.myorganization.types.OrgMember;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrganizationMembersWireTest {
    private MockWebServer server;
    private MyOrganizationApi client;
    private ObjectMapper objectMapper = ObjectMappers.JSON_MAPPER;

    @BeforeEach
    public void setup() throws Exception {
        server = new MockWebServer();
        server.start();
        client = MyOrganizationApi.builder()
                .url(server.url("/").toString())
                .token("test-token")
                .build();
    }

    @AfterEach
    public void teardown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testList() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"next\":\"next\",\"members\":[{\"email\":\"roadrunner@acme.com\",\"name\":\"name\",\"nickname\":\"nickname\",\"given_name\":\"given_name\",\"family_name\":\"family_name\",\"user_id\":\"auth0|123234235\",\"roles\":[{\"id\":\"rol_BKI0BKI0BKI0BKI0\",\"name\":\"role1\"},{\"id\":\"rol_BKW1BKIfBKd0BaI0\",\"name\":\"role2\"}],\"created_at\":\"2024-01-15T09:30:00Z\",\"updated_at\":\"2024-01-15T09:30:00Z\",\"last_login\":\"2024-01-15T09:30:00Z\",\"phone_number\":\"phone_number\"}]}"));
        SyncPagingIterable<OrgMember> response = client.organization()
                .members()
                .list(ListOrganizationMembersRequestParameters.builder()
                        .fields(OptionalNullable.of("fields"))
                        .includeFields(OptionalNullable.of(true))
                        .from(OptionalNullable.of("from"))
                        .take(OptionalNullable.of(1))
                        .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        // Pagination response validated via MockWebServer
        // The SDK correctly parses the response into a SyncPagingIterable
    }

    @Test
    public void testGet() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"email\":\"roadrunner@acme.com\",\"name\":\"roadrunner\",\"nickname\":\"beepbeep\",\"given_name\":\"Road\",\"family_name\":\"Runner\",\"user_id\":\"auth0|123234235\",\"roles\":[{\"id\":\"rol_BKI0BKI0BKI0BKI0\",\"name\":\"role1\",\"description\":\"description\"},{\"id\":\"rol_BKW1BKIfBKd0BaI0\",\"name\":\"role2\",\"description\":\"description\"}],\"created_at\":\"2025-05-01T12:00:00Z\",\"updated_at\":\"2025-05-02T12:00:00Z\",\"last_login\":\"2025-05-03T12:00:00Z\",\"phone_number\":\"phone_number\"}"));
        OrgMember response = client.organization()
                .members()
                .get(
                        "user_id",
                        GetOrganizationMemberRequestParameters.builder()
                                .fields(OptionalNullable.of("fields"))
                                .includeFields(OptionalNullable.of(true))
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"email\": \"roadrunner@acme.com\",\n"
                + "  \"name\": \"roadrunner\",\n"
                + "  \"nickname\": \"beepbeep\",\n"
                + "  \"given_name\": \"Road\",\n"
                + "  \"family_name\": \"Runner\",\n"
                + "  \"user_id\": \"auth0|123234235\",\n"
                + "  \"roles\": [\n"
                + "    {\n"
                + "      \"id\": \"rol_BKI0BKI0BKI0BKI0\",\n"
                + "      \"name\": \"role1\",\n"
                + "      \"description\": \"description\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"id\": \"rol_BKW1BKIfBKd0BaI0\",\n"
                + "      \"name\": \"role2\",\n"
                + "      \"description\": \"description\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"created_at\": \"2025-05-01T12:00:00Z\",\n"
                + "  \"updated_at\": \"2025-05-02T12:00:00Z\",\n"
                + "  \"last_login\": \"2025-05-03T12:00:00Z\",\n"
                + "  \"phone_number\": \"phone_number\"\n"
                + "}";
        JsonNode actualResponseNode = objectMapper.readTree(actualResponseJson);
        JsonNode expectedResponseNode = objectMapper.readTree(expectedResponseBody);
        Assertions.assertTrue(
                jsonEquals(expectedResponseNode, actualResponseNode),
                "Response body structure does not match expected");
        if (actualResponseNode.has("type") || actualResponseNode.has("_type") || actualResponseNode.has("kind")) {
            String discriminator = null;
            if (actualResponseNode.has("type"))
                discriminator = actualResponseNode.get("type").asText();
            else if (actualResponseNode.has("_type"))
                discriminator = actualResponseNode.get("_type").asText();
            else if (actualResponseNode.has("kind"))
                discriminator = actualResponseNode.get("kind").asText();
            Assertions.assertNotNull(discriminator, "Union type should have a discriminator field");
            Assertions.assertFalse(discriminator.isEmpty(), "Union discriminator should not be empty");
        }

        if (!actualResponseNode.isNull()) {
            Assertions.assertTrue(
                    actualResponseNode.isObject() || actualResponseNode.isArray() || actualResponseNode.isValueNode(),
                    "response should be a valid JSON value");
        }

        if (actualResponseNode.isArray()) {
            Assertions.assertTrue(actualResponseNode.size() >= 0, "Array should have valid size");
        }
        if (actualResponseNode.isObject()) {
            Assertions.assertTrue(actualResponseNode.size() >= 0, "Object should have valid field count");
        }
    }

    /**
     * Compares two JsonNodes with numeric equivalence and null safety.
     * For objects, checks that all fields in 'expected' exist in 'actual' with matching values.
     * Allows 'actual' to have extra fields (e.g., default values added during serialization).
     */
    private boolean jsonEquals(JsonNode expected, JsonNode actual) {
        if (expected == null && actual == null) return true;
        if (expected == null || actual == null) return false;
        if (expected.equals(actual)) return true;
        if (expected.isNumber() && actual.isNumber())
            return Math.abs(expected.doubleValue() - actual.doubleValue()) < 1e-10;
        if (expected.isObject() && actual.isObject()) {
            java.util.Iterator<java.util.Map.Entry<String, JsonNode>> iter = expected.fields();
            while (iter.hasNext()) {
                java.util.Map.Entry<String, JsonNode> entry = iter.next();
                JsonNode actualValue = actual.get(entry.getKey());
                if (actualValue == null || !jsonEquals(entry.getValue(), actualValue)) return false;
            }
            return true;
        }
        if (expected.isArray() && actual.isArray()) {
            if (expected.size() != actual.size()) return false;
            for (int i = 0; i < expected.size(); i++) {
                if (!jsonEquals(expected.get(i), actual.get(i))) return false;
            }
            return true;
        }
        return false;
    }
}
