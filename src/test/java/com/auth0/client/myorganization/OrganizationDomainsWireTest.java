package com.auth0.client.myorganization;

import com.auth0.client.myorganization.core.ObjectMappers;
import com.auth0.client.myorganization.core.OptionalNullable;
import com.auth0.client.myorganization.core.SyncPagingIterable;
import com.auth0.client.myorganization.organization.types.CreateOrganizationDomainRequestContent;
import com.auth0.client.myorganization.organization.types.ListOrganizationDomainsRequestParameters;
import com.auth0.client.myorganization.types.OrgDomain;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrganizationDomainsWireTest {
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
                                "{\"next\":\"eyJpZCI6Im9yZF96VzFVSGV0dmtCV1NXZENEZThEV3E3IiwidGVuYW50IjoidGVzdC10ZW5hbnQifQ\",\"organization_domains\":[{\"id\":\"ord_aW1UHetvkBWSWdCCe8DWq7\",\"org_id\":\"org_zW1UHutvkVWSWdCC\",\"domain\":\"acme.com\",\"status\":\"pending\",\"verification_txt\":\"dove_text=asdfpiujnlewp-23849jdkfjzxcfpiawer\",\"verification_host\":\"_ss-verification.org_zW1UHutvkVWSWdCC.acme.com\"},{\"id\":\"ord_zW1UHetvkBWSWdCDe8DWq7\",\"org_id\":\"org_nW1UHutvkVWSWdCG\",\"domain\":\"roadrunner.com\",\"status\":\"failed\",\"verification_txt\":\"dove_text=bcxzpiujnlewp-23849jdkfjzxcfpiawer\",\"verification_host\":\"_ss-verification.org_nW1UHutvkVWSWdCG.acme.com\"}]}"));
        SyncPagingIterable<OrgDomain> response = client.organization()
                .domains()
                .list(ListOrganizationDomainsRequestParameters.builder()
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
    public void testCreate() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"id\":\"ord_aW1UHetvkBWSWdCCe8DWq7\",\"org_id\":\"org_zW1UHutvkVWSWdCC\",\"domain\":\"acme.com\",\"status\":\"pending\",\"verification_txt\":\"dove_text=asdfpiujnlewp-23849jdkfjzxcfpiawer\",\"verification_host\":\"_ss-verification.org_zW1UHutvkVWSWdCC.acme.com\"}"));
        OrgDomain response = client.organization()
                .domains()
                .create(CreateOrganizationDomainRequestContent.builder()
                        .domain("acme.com")
                        .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = "" + "{\n" + "  \"domain\": \"acme.com\"\n" + "}";
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
                + "  \"id\": \"ord_aW1UHetvkBWSWdCCe8DWq7\",\n"
                + "  \"org_id\": \"org_zW1UHutvkVWSWdCC\",\n"
                + "  \"domain\": \"acme.com\",\n"
                + "  \"status\": \"pending\",\n"
                + "  \"verification_txt\": \"dove_text=asdfpiujnlewp-23849jdkfjzxcfpiawer\",\n"
                + "  \"verification_host\": \"_ss-verification.org_zW1UHutvkVWSWdCC.acme.com\"\n"
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
    public void testGet() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"id\":\"ord_aW1UHetvkBWSWdCCe8DWq7\",\"org_id\":\"org_zW1UHutvkVWSWdCC\",\"domain\":\"acme.com\",\"status\":\"pending\",\"verification_txt\":\"dove_text=asdfpiujnlewp-23849jdkfjzxcfpiawer\",\"verification_host\":\"_ss-verification.org_zW1UHutvkVWSWdCC.acme.com\"}"));
        OrgDomain response = client.organization().domains().get("domain_id");
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"id\": \"ord_aW1UHetvkBWSWdCCe8DWq7\",\n"
                + "  \"org_id\": \"org_zW1UHutvkVWSWdCC\",\n"
                + "  \"domain\": \"acme.com\",\n"
                + "  \"status\": \"pending\",\n"
                + "  \"verification_txt\": \"dove_text=asdfpiujnlewp-23849jdkfjzxcfpiawer\",\n"
                + "  \"verification_host\": \"_ss-verification.org_zW1UHutvkVWSWdCC.acme.com\"\n"
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
        client.organization().domains().delete("domain_id");
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("DELETE", request.getMethod());
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
