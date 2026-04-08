package com.auth0.client.myorganization;

import com.auth0.client.myorganization.core.ObjectMappers;
import com.auth0.client.myorganization.types.FedMetadataXml;
import com.auth0.client.myorganization.types.IdpAdfsOptionsRequest;
import com.auth0.client.myorganization.types.IdpAdfsUpdateRequest;
import com.auth0.client.myorganization.types.IdpKnownRequest;
import com.auth0.client.myorganization.types.IdpKnownResponse;
import com.auth0.client.myorganization.types.IdpOidcOptionsRequest;
import com.auth0.client.myorganization.types.IdpOidcOptionsTypeEnum;
import com.auth0.client.myorganization.types.IdpOidcRequest;
import com.auth0.client.myorganization.types.IdpOidcRequestStrategy;
import com.auth0.client.myorganization.types.IdpUpdateKnownRequest;
import com.auth0.client.myorganization.types.IdpUpdateKnownResponse;
import com.auth0.client.myorganization.types.ListIdentityProvidersResponseContent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrganizationIdentityProvidersWireTest {
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
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/OrganizationIdentityProvidersWireTest_testList_response.json")));
        ListIdentityProvidersResponseContent response =
                client.organization().identityProviders().list();
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody =
                TestResources.loadResource("/wire-tests/OrganizationIdentityProvidersWireTest_testList_response.json");
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
                                "{\"id\":\"con_zW1UHutvkVWSWdCC\",\"name\":\"oidcIdp\",\"strategy\":\"oidc\",\"domains\":[\"mydomain.com\"],\"display_name\":\"OIDC IdP\",\"show_as_button\":true,\"assign_membership_on_login\":false,\"is_enabled\":true,\"access_level\":\"full\",\"options\":{\"type\":\"front_channel\",\"client_id\":\"client_a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7did\",\"discovery_url\":\"https://{yourDomain}/.well-known/openid-configuration\"},\"attributes\":[{\"user_attribute\":\"preferred_username\",\"description\":\"Preferred Username\",\"label\":\"Preferred username\",\"is_required\":true,\"is_extra\":false,\"is_missing\":false,\"sso_field\":[\"userName\"]},{\"user_attribute\":\"external_id\",\"description\":\"description\",\"label\":\"label\",\"is_required\":true,\"is_extra\":true,\"is_missing\":false,\"sso_field\":[\"externalId\"]}]}"));
        IdpKnownResponse response = client.organization()
                .identityProviders()
                .create(IdpKnownRequest.of(IdpOidcRequest.builder()
                        .strategy(IdpOidcRequestStrategy.OIDC)
                        .options(IdpOidcOptionsRequest.builder()
                                .type(IdpOidcOptionsTypeEnum.FRONT_CHANNEL)
                                .clientId("a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7d")
                                .discoveryUrl("https://{yourDomain}/.well-known/openid-configuration")
                                .clientSecret(Optional.of("KzQp2sVxR8nTgMjFhYcEWuLoIbDvUoC6A9B1zX7yWqFjHkGrP5sQdLmNp"))
                                .build())
                        .name("oidcIdp")
                        .domains(Optional.of(Arrays.asList("mydomain.com")))
                        .displayName(Optional.of("OIDC IdP"))
                        .showAsButton(Optional.of(true))
                        .assignMembershipOnLogin(Optional.of(false))
                        .isEnabled(Optional.of(true))
                        .build()));
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "{\n"
                + "  \"name\": \"oidcIdp\",\n"
                + "  \"strategy\": \"oidc\",\n"
                + "  \"domains\": [\n"
                + "    \"mydomain.com\"\n"
                + "  ],\n"
                + "  \"display_name\": \"OIDC IdP\",\n"
                + "  \"show_as_button\": true,\n"
                + "  \"assign_membership_on_login\": false,\n"
                + "  \"is_enabled\": true,\n"
                + "  \"options\": {\n"
                + "    \"type\": \"front_channel\",\n"
                + "    \"client_id\": \"a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7d\",\n"
                + "    \"client_secret\": \"KzQp2sVxR8nTgMjFhYcEWuLoIbDvUoC6A9B1zX7yWqFjHkGrP5sQdLmNp\",\n"
                + "    \"discovery_url\": \"https://{yourDomain}/.well-known/openid-configuration\"\n"
                + "  }\n"
                + "}";
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
                + "  \"id\": \"con_zW1UHutvkVWSWdCC\",\n"
                + "  \"name\": \"oidcIdp\",\n"
                + "  \"strategy\": \"oidc\",\n"
                + "  \"domains\": [\n"
                + "    \"mydomain.com\"\n"
                + "  ],\n"
                + "  \"display_name\": \"OIDC IdP\",\n"
                + "  \"show_as_button\": true,\n"
                + "  \"assign_membership_on_login\": false,\n"
                + "  \"is_enabled\": true,\n"
                + "  \"access_level\": \"full\",\n"
                + "  \"options\": {\n"
                + "    \"type\": \"front_channel\",\n"
                + "    \"client_id\": \"client_a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7did\",\n"
                + "    \"discovery_url\": \"https://{yourDomain}/.well-known/openid-configuration\"\n"
                + "  },\n"
                + "  \"attributes\": [\n"
                + "    {\n"
                + "      \"user_attribute\": \"preferred_username\",\n"
                + "      \"description\": \"Preferred Username\",\n"
                + "      \"label\": \"Preferred username\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": false,\n"
                + "      \"is_missing\": false,\n"
                + "      \"sso_field\": [\n"
                + "        \"userName\"\n"
                + "      ]\n"
                + "    },\n"
                + "    {\n"
                + "      \"user_attribute\": \"external_id\",\n"
                + "      \"description\": \"description\",\n"
                + "      \"label\": \"label\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": true,\n"
                + "      \"is_missing\": false,\n"
                + "      \"sso_field\": [\n"
                + "        \"externalId\"\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
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
                                "{\"id\":\"con_zW1UHutvkVWSWdCC\",\"name\":\"oidcIdp\",\"strategy\":\"oidc\",\"domains\":[\"mydomain.com\"],\"display_name\":\"OIDC IdP\",\"show_as_button\":true,\"assign_membership_on_login\":false,\"is_enabled\":true,\"access_level\":\"readonly\",\"options\":{\"type\":\"front_channel\",\"client_id\":\"a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7d\",\"discovery_url\":\"https://{yourDomain}/.well-known/openid-configuration\"},\"attributes\":[{\"user_attribute\":\"preferred_username\",\"description\":\"Preferred Username\",\"label\":\"Preferred username\",\"is_required\":true,\"is_extra\":false,\"is_missing\":false,\"sso_field\":[\"userName\"]},{\"user_attribute\":\"external_id\",\"description\":\"description\",\"label\":\"label\",\"is_required\":true,\"is_extra\":true,\"is_missing\":false,\"sso_field\":[\"externalId\"]}]}"));
        IdpKnownResponse response = client.organization().identityProviders().get("idp_id");
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"id\": \"con_zW1UHutvkVWSWdCC\",\n"
                + "  \"name\": \"oidcIdp\",\n"
                + "  \"strategy\": \"oidc\",\n"
                + "  \"domains\": [\n"
                + "    \"mydomain.com\"\n"
                + "  ],\n"
                + "  \"display_name\": \"OIDC IdP\",\n"
                + "  \"show_as_button\": true,\n"
                + "  \"assign_membership_on_login\": false,\n"
                + "  \"is_enabled\": true,\n"
                + "  \"access_level\": \"readonly\",\n"
                + "  \"options\": {\n"
                + "    \"type\": \"front_channel\",\n"
                + "    \"client_id\": \"a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7d\",\n"
                + "    \"discovery_url\": \"https://{yourDomain}/.well-known/openid-configuration\"\n"
                + "  },\n"
                + "  \"attributes\": [\n"
                + "    {\n"
                + "      \"user_attribute\": \"preferred_username\",\n"
                + "      \"description\": \"Preferred Username\",\n"
                + "      \"label\": \"Preferred username\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": false,\n"
                + "      \"is_missing\": false,\n"
                + "      \"sso_field\": [\n"
                + "        \"userName\"\n"
                + "      ]\n"
                + "    },\n"
                + "    {\n"
                + "      \"user_attribute\": \"external_id\",\n"
                + "      \"description\": \"description\",\n"
                + "      \"label\": \"label\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": true,\n"
                + "      \"is_missing\": false,\n"
                + "      \"sso_field\": [\n"
                + "        \"externalId\"\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
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
        client.organization().identityProviders().delete("idp_id");
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("DELETE", request.getMethod());
    }

    @Test
    public void testUpdate() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"id\":\"con_zW1UHutvkVWSWdCC\",\"name\":\"oidcIdp\",\"strategy\":\"oidc\",\"domains\":[\"mydomain.com\"],\"display_name\":\"OIDC IdP\",\"show_as_button\":true,\"assign_membership_on_login\":false,\"is_enabled\":true,\"access_level\":\"full\",\"options\":{\"type\":\"front_channel\",\"client_id\":\"a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7d\",\"discovery_url\":\"https://{yourDomain}/.well-known/openid-configuration\"},\"attributes\":[{\"user_attribute\":\"preferred_username\",\"description\":\"Preferred Username\",\"label\":\"Preferred username\",\"is_required\":true,\"is_extra\":false,\"is_missing\":false,\"sso_field\":[\"userName\"]},{\"user_attribute\":\"external_id\",\"description\":\"description\",\"label\":\"label\",\"is_required\":true,\"is_extra\":true,\"is_missing\":false,\"sso_field\":[\"externalId\"]}]}"));
        IdpUpdateKnownResponse response = client.organization()
                .identityProviders()
                .update(
                        "idp_id",
                        IdpUpdateKnownRequest.of(IdpAdfsUpdateRequest.builder()
                                .displayName(Optional.of("OIDC IdP"))
                                .showAsButton(Optional.of(true))
                                .assignMembershipOnLogin(Optional.of(false))
                                .isEnabled(Optional.of(true))
                                .options(Optional.of(IdpAdfsOptionsRequest.of(
                                        FedMetadataXml.builder().build())))
                                .build()));
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("PATCH", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "{\n"
                + "  \"display_name\": \"OIDC IdP\",\n"
                + "  \"show_as_button\": true,\n"
                + "  \"assign_membership_on_login\": false,\n"
                + "  \"is_enabled\": true,\n"
                + "  \"options\": {\n"
                + "    \"type\": \"front_channel\",\n"
                + "    \"client_id\": \"a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7d\",\n"
                + "    \"client_secret\": \"KzQp2sVxR8nTgMjFhYcEWuLoIbDvUoC6A9B1zX7yWqFjHkGrP5sQdLmNp\",\n"
                + "    \"discovery_url\": \"https://{yourDomain}/.well-known/openid-configuration\"\n"
                + "  }\n"
                + "}";
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
                + "  \"id\": \"con_zW1UHutvkVWSWdCC\",\n"
                + "  \"name\": \"oidcIdp\",\n"
                + "  \"strategy\": \"oidc\",\n"
                + "  \"domains\": [\n"
                + "    \"mydomain.com\"\n"
                + "  ],\n"
                + "  \"display_name\": \"OIDC IdP\",\n"
                + "  \"show_as_button\": true,\n"
                + "  \"assign_membership_on_login\": false,\n"
                + "  \"is_enabled\": true,\n"
                + "  \"access_level\": \"full\",\n"
                + "  \"options\": {\n"
                + "    \"type\": \"front_channel\",\n"
                + "    \"client_id\": \"a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7d\",\n"
                + "    \"discovery_url\": \"https://{yourDomain}/.well-known/openid-configuration\"\n"
                + "  },\n"
                + "  \"attributes\": [\n"
                + "    {\n"
                + "      \"user_attribute\": \"preferred_username\",\n"
                + "      \"description\": \"Preferred Username\",\n"
                + "      \"label\": \"Preferred username\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": false,\n"
                + "      \"is_missing\": false,\n"
                + "      \"sso_field\": [\n"
                + "        \"userName\"\n"
                + "      ]\n"
                + "    },\n"
                + "    {\n"
                + "      \"user_attribute\": \"external_id\",\n"
                + "      \"description\": \"description\",\n"
                + "      \"label\": \"label\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": true,\n"
                + "      \"is_missing\": false,\n"
                + "      \"sso_field\": [\n"
                + "        \"externalId\"\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
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
    public void testUpdateAttributes() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"id\":\"con_zW1UHutvkVWSWdCC\",\"name\":\"oidcIdp\",\"strategy\":\"oidc\",\"domains\":[\"mydomain.com\"],\"display_name\":\"OIDC IdP\",\"show_as_button\":true,\"assign_membership_on_login\":false,\"is_enabled\":true,\"access_level\":\"readonly\",\"options\":{\"type\":\"front_channel\",\"client_id\":\"a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7d\",\"discovery_url\":\"https://{yourDomain}/.well-known/openid-configuration\"},\"attributes\":[{\"user_attribute\":\"preferred_username\",\"description\":\"Preferred Username\",\"label\":\"Preferred username\",\"is_required\":true,\"is_extra\":false,\"is_missing\":false,\"sso_field\":[\"userName\"]},{\"user_attribute\":\"external_id\",\"description\":\"description\",\"label\":\"label\",\"is_required\":true,\"is_extra\":true,\"is_missing\":false,\"sso_field\":[\"externalId\"]}]}"));
        IdpKnownResponse response = client.organization()
                .identityProviders()
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
                + "  \"id\": \"con_zW1UHutvkVWSWdCC\",\n"
                + "  \"name\": \"oidcIdp\",\n"
                + "  \"strategy\": \"oidc\",\n"
                + "  \"domains\": [\n"
                + "    \"mydomain.com\"\n"
                + "  ],\n"
                + "  \"display_name\": \"OIDC IdP\",\n"
                + "  \"show_as_button\": true,\n"
                + "  \"assign_membership_on_login\": false,\n"
                + "  \"is_enabled\": true,\n"
                + "  \"access_level\": \"readonly\",\n"
                + "  \"options\": {\n"
                + "    \"type\": \"front_channel\",\n"
                + "    \"client_id\": \"a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7d\",\n"
                + "    \"discovery_url\": \"https://{yourDomain}/.well-known/openid-configuration\"\n"
                + "  },\n"
                + "  \"attributes\": [\n"
                + "    {\n"
                + "      \"user_attribute\": \"preferred_username\",\n"
                + "      \"description\": \"Preferred Username\",\n"
                + "      \"label\": \"Preferred username\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": false,\n"
                + "      \"is_missing\": false,\n"
                + "      \"sso_field\": [\n"
                + "        \"userName\"\n"
                + "      ]\n"
                + "    },\n"
                + "    {\n"
                + "      \"user_attribute\": \"external_id\",\n"
                + "      \"description\": \"description\",\n"
                + "      \"label\": \"label\",\n"
                + "      \"is_required\": true,\n"
                + "      \"is_extra\": true,\n"
                + "      \"is_missing\": false,\n"
                + "      \"sso_field\": [\n"
                + "        \"externalId\"\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
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
    public void testDetach() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.organization().identityProviders().detach("idp_id");
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());
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
