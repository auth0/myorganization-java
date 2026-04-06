package com.auth0.client.myorganization;

import com.auth0.client.myorganization.core.ObjectMappers;
import com.auth0.client.myorganization.types.CreateIdPProvisioningConfigResponseContent;
import com.auth0.client.myorganization.types.GetIdPProvisioningConfigResponseContent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrganizationIdentityProvidersProvisioningWireTest {
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
    public void testGet() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"identity_provider_id\":\"con_2CZPv6IY0gWzDaQJ\",\"identity_provider_name\":\"EC-org-gaZPTTOS42pReSzs-id-ready2\",\"strategy\":\"okta\",\"method\":\"scim\",\"attributes\":[{\"user_attribute\":\"preferred_username\",\"description\":\"Preferred Username\",\"label\":\"Preferred username\",\"is_required\":true,\"is_extra\":false,\"is_missing\":false,\"provisioning_field\":\"userName\"},{\"user_attribute\":\"blocked\",\"description\":\"description\",\"label\":\"label\",\"is_required\":true,\"is_extra\":false,\"is_missing\":false,\"provisioning_field\":\"active\"}],\"user_id_attribute\":\"externalId\",\"created_at\":\"2025-05-15T23:32:52Z\",\"updated_on\":\"2025-05-15T23:32:52Z\"}"));
        GetIdPProvisioningConfigResponseContent response =
                client.organization().identityProviders().provisioning().get("idp_id");
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"identity_provider_id\": \"con_2CZPv6IY0gWzDaQJ\",\n"
                + "  \"identity_provider_name\": \"EC-org-gaZPTTOS42pReSzs-id-ready2\",\n"
                + "  \"strategy\": \"okta\",\n"
                + "  \"method\": \"scim\",\n"
                + "  \"attributes\": [\n"
                + "    {\n"
                + "      \"user_attribute\": \"preferred_username\",\n"
                + "      \"description\": \"Preferred Username\",\n"
                + "      \"label\": \"Preferred username\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": false,\n"
                + "      \"is_missing\": false,\n"
                + "      \"provisioning_field\": \"userName\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"user_attribute\": \"blocked\",\n"
                + "      \"description\": \"description\",\n"
                + "      \"label\": \"label\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": false,\n"
                + "      \"is_missing\": false,\n"
                + "      \"provisioning_field\": \"active\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"user_id_attribute\": \"externalId\",\n"
                + "  \"created_at\": \"2025-05-15T23:32:52Z\",\n"
                + "  \"updated_on\": \"2025-05-15T23:32:52Z\"\n"
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

    @Test
    public void testCreate() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"identity_provider_id\":\"con_2CZPv6IY0gWzDaQJ\",\"identity_provider_name\":\"EC-org-gaZPTTOS42pReSzs-id-ready2\",\"strategy\":\"okta\",\"method\":\"scim\",\"attributes\":[{\"user_attribute\":\"preferred_username\",\"description\":\"Preferred Username\",\"label\":\"Preferred username\",\"is_required\":true,\"is_extra\":false,\"is_missing\":false,\"provisioning_field\":\"userName\"},{\"user_attribute\":\"external_id\",\"description\":\"description\",\"label\":\"label\",\"is_required\":true,\"is_extra\":true,\"is_missing\":false,\"provisioning_field\":\"externalId\"}],\"user_id_attribute\":\"externalId\",\"created_at\":\"2025-05-15T23:32:52Z\",\"updated_on\":\"2025-05-15T23:32:52Z\"}"));
        CreateIdPProvisioningConfigResponseContent response =
                client.organization().identityProviders().provisioning().create("idp_id");
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"identity_provider_id\": \"con_2CZPv6IY0gWzDaQJ\",\n"
                + "  \"identity_provider_name\": \"EC-org-gaZPTTOS42pReSzs-id-ready2\",\n"
                + "  \"strategy\": \"okta\",\n"
                + "  \"method\": \"scim\",\n"
                + "  \"attributes\": [\n"
                + "    {\n"
                + "      \"user_attribute\": \"preferred_username\",\n"
                + "      \"description\": \"Preferred Username\",\n"
                + "      \"label\": \"Preferred username\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": false,\n"
                + "      \"is_missing\": false,\n"
                + "      \"provisioning_field\": \"userName\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"user_attribute\": \"external_id\",\n"
                + "      \"description\": \"description\",\n"
                + "      \"label\": \"label\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": true,\n"
                + "      \"is_missing\": false,\n"
                + "      \"provisioning_field\": \"externalId\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"user_id_attribute\": \"externalId\",\n"
                + "  \"created_at\": \"2025-05-15T23:32:52Z\",\n"
                + "  \"updated_on\": \"2025-05-15T23:32:52Z\"\n"
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

    @Test
    public void testDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.organization().identityProviders().provisioning().delete("idp_id");
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("DELETE", request.getMethod());
    }

    @Test
    public void testUpdateAttributes() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"identity_provider_id\":\"con_2CZPv6IY0gWzDaQJ\",\"identity_provider_name\":\"EC-org-gaZPTTOS42pReSzs-id-ready2\",\"strategy\":\"okta\",\"method\":\"scim\",\"attributes\":[{\"user_attribute\":\"preferred_username\",\"description\":\"Preferred Username\",\"label\":\"Preferred username\",\"is_required\":true,\"is_extra\":false,\"is_missing\":false,\"provisioning_field\":\"userName\"},{\"user_attribute\":\"blocked\",\"description\":\"description\",\"label\":\"label\",\"is_required\":true,\"is_extra\":false,\"is_missing\":false,\"provisioning_field\":\"active\"}],\"user_id_attribute\":\"externalId\",\"created_at\":\"2025-05-15T23:32:52Z\",\"updated_on\":\"2025-05-15T23:32:52Z\"}"));
        GetIdPProvisioningConfigResponseContent response = client.organization()
                .identityProviders()
                .provisioning()
                .updateAttributes("idp_id", new HashMap<String, Object>() {
                    {
                        put("key", "value");
                    }
                });
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("PUT", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = "" + "{\n" + "  \"key\": \"value\"\n" + "}";
        JsonNode actualJson = objectMapper.readTree(actualRequestBody);
        JsonNode expectedJson = objectMapper.readTree(expectedRequestBody);
        Assertions.assertTrue(jsonEquals(expectedJson, actualJson), "Request body structure does not match expected");
        if (actualJson.has("type") || actualJson.has("_type") || actualJson.has("kind")) {
            String discriminator = null;
            if (actualJson.has("type")) discriminator = actualJson.get("type").asText();
            else if (actualJson.has("_type"))
                discriminator = actualJson.get("_type").asText();
            else if (actualJson.has("kind"))
                discriminator = actualJson.get("kind").asText();
            Assertions.assertNotNull(discriminator, "Union type should have a discriminator field");
            Assertions.assertFalse(discriminator.isEmpty(), "Union discriminator should not be empty");
        }

        if (!actualJson.isNull()) {
            Assertions.assertTrue(
                    actualJson.isObject() || actualJson.isArray() || actualJson.isValueNode(),
                    "request should be a valid JSON value");
        }

        if (actualJson.isArray()) {
            Assertions.assertTrue(actualJson.size() >= 0, "Array should have valid size");
        }
        if (actualJson.isObject()) {
            Assertions.assertTrue(actualJson.size() >= 0, "Object should have valid field count");
        }

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"identity_provider_id\": \"con_2CZPv6IY0gWzDaQJ\",\n"
                + "  \"identity_provider_name\": \"EC-org-gaZPTTOS42pReSzs-id-ready2\",\n"
                + "  \"strategy\": \"okta\",\n"
                + "  \"method\": \"scim\",\n"
                + "  \"attributes\": [\n"
                + "    {\n"
                + "      \"user_attribute\": \"preferred_username\",\n"
                + "      \"description\": \"Preferred Username\",\n"
                + "      \"label\": \"Preferred username\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": false,\n"
                + "      \"is_missing\": false,\n"
                + "      \"provisioning_field\": \"userName\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"user_attribute\": \"blocked\",\n"
                + "      \"description\": \"description\",\n"
                + "      \"label\": \"label\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": false,\n"
                + "      \"is_missing\": false,\n"
                + "      \"provisioning_field\": \"active\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"user_id_attribute\": \"externalId\",\n"
                + "  \"created_at\": \"2025-05-15T23:32:52Z\",\n"
                + "  \"updated_on\": \"2025-05-15T23:32:52Z\"\n"
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
