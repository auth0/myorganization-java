![Java SDK for Auth0 MyOrganization](https://cdn.auth0.com/website/sdks/banners/myorganization-java-banner.png)

<div align="center">

[![Maven Central](https://img.shields.io/maven-central/v/com.auth0/myorganization-java.svg?style=flat-square)](https://search.maven.org/artifact/com.auth0/myorganization-java)
[![License](https://img.shields.io/github/license/auth0/myorganization-java.svg?style=flat-square)](https://github.com/auth0/myorganization-java/blob/main/LICENSE)
[![Build Status](https://img.shields.io/github/actions/workflow/status/auth0/myorganization-java/release.yml?branch=main&style=flat-square)](https://github.com/auth0/myorganization-java/actions?query=branch%3Amain)

:books: [Documentation](#documentation) · :rocket: [Getting Started](#getting-started) · :speech_balloon: [Feedback](#feedback)

</div>

---

## Documentation

- [API Reference](./reference.md) - Complete API reference documentation.
- [Docs site](https://www.auth0.com/docs) — explore our docs site and learn more about Auth0.

## Getting Started

### Requirements

- Java 8+

### Installation

#### Gradle

```groovy
implementation 'com.auth0:myorganization-java:1.0.0-beta.0'
```

#### Maven

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>myorganization-java</artifactId>
    <version>1.0.0-beta.0</version>
</dependency>
```

### Usage

The primary entry point is `MyOrganizationClient`, which wraps the Fern-generated client with automatic token management, token caching with auto-refresh, Auth0 telemetry headers, and domain-based URL derivation.

#### Client Credentials (M2M)

Use `.clientCredentials()` for machine-to-machine authentication via the OAuth2 client credentials grant. The SDK automatically fetches, caches, and refreshes access tokens.

```java
import com.auth0.client.myorganization.auth.MyOrganizationClient;

MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .clientCredentials("YOUR_CLIENT_ID", "YOUR_CLIENT_SECRET")
    .organization("org_abc123")
    .build();

client.organization().domains().create(...);
```

> **Note**
> The domain is sanitized automatically (`https://` prefix and trailing slashes are stripped).

> The default audience is `https://{domain}/my-org/`. To specify a custom audience, use `.audience()`:

```java
MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .clientCredentials("YOUR_CLIENT_ID", "YOUR_CLIENT_SECRET")
    .audience("https://custom-api.example.com/")
    .build();
```

#### Private Key JWT

Use `.privateKeyJwt()` for authentication using a signed JWT assertion instead of a client secret. The SDK creates a JWT signed with your private key, then exchanges it for an access token via the `client_credentials` grant with `client_assertion`.

Supported signing algorithms: RS256, RS384, RS512, ES256, ES384, ES512.

```java
import com.auth0.client.myorganization.auth.MyOrganizationClient;

String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----";

MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .privateKeyJwt("YOUR_CLIENT_ID", privateKeyPem, "RS256")
    .organization("org_abc123")
    .build();
```

> The default audience is `https://{domain}/my-org/`. To specify a custom audience, use `.audience()`:

```java
MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .privateKeyJwt("YOUR_CLIENT_ID", privateKeyPem, "RS256")
    .audience("https://custom-api.example.com/")
    .build();
```

#### Custom Token Provider

Use `.tokenProvider()` to provide your own `TokenProvider` implementation. This gives you full control over how access tokens are obtained, cached, and refreshed.

```java
import com.auth0.client.myorganization.auth.MyOrganizationClient;
import com.auth0.client.myorganization.auth.Token;

MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .tokenProvider(() -> Token.of(fetchTokenFromVault()))
    .build();
```

#### Static Token

Use `.staticToken()` when you already have a bearer token:

```java
import com.auth0.client.myorganization.auth.MyOrganizationClient;

MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .staticToken("YOUR_ACCESS_TOKEN")
    .build();
```

### Request Options

Options can be applied at the client level (affecting all requests) or per-request:

```java
import com.auth0.client.myorganization.auth.MyOrganizationClient;
import com.auth0.client.myorganization.core.RequestOptions;

// Client-level options (applied to every request).
MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .clientCredentials("YOUR_CLIENT_ID", "YOUR_CLIENT_SECRET")
    .maxRetries(3)
    .timeout(30)
    .build();

// Per-request overrides.
client.organization().domains().create(
    ...,
    RequestOptions.builder()
        .timeout(60)
        .addHeader("X-Request-Id", "abc-123")
        .build()
);
```

Available builder options:

| Option | Scope | Description |
|---|---|---|
| **Authentication** | | |
| `.clientCredentials(id, secret)` | Client | OAuth2 client credentials (M2M) |
| `.privateKeyJwt(id, pem, alg)` | Client | Private Key JWT assertion |
| `.tokenProvider(provider)` | Client | Custom `TokenProvider` |
| `.staticToken(token)` | Client | Static bearer token |
| `.token(token)` | Client | Alias for `.staticToken()` |
| **Configuration** | | |
| `.domain(domain)` | Client | Auth0 tenant domain (required) |
| `.tenantDomain(domain)` | Client | Alias for `.domain()` |
| `.audience(audience)` | Client | Custom API audience |
| `.organization(org)` | Client | Organization name or ID |
| `.httpClient(client)` | Client | Provide a custom `OkHttpClient` |
| `.maxRetries(n)` | Client | Set max retry attempts |
| `.timeout(seconds)` | Both | Set request timeout |
| `.addHeader(name, value)` | Both | Add custom HTTP headers |

> **Scope**: *Client* = only when creating the client via builder. *Both* = client-level or per-request via `RequestOptions`.

### Raw Responses

The SDK provides access to raw response data, including headers, through the `withRawResponse()` method:

```java
MyOrganizationApiHttpResponse response = client.organization().domains().withRawResponse().create(...);

System.out.println(response.body());
System.out.println(response.headers().get("X-My-Header"));
```

### Error Handling

When the API returns a non-success status code (4xx or 5xx response), a `MyOrganizationApiException` is thrown:

```java
import com.auth0.client.myorganization.core.MyOrganizationApiException;

try {
    client.organization().domains().create(...);
} catch (MyOrganizationApiException e) {
    System.out.println("Status: " + e.statusCode());
    System.out.println("Body: " + e.body());
}
```

### OptionalNullable for PATCH Requests

For PATCH requests, the SDK uses `OptionalNullable<T>` to handle three-state nullable semantics:

- **ABSENT**: Field not provided (omitted from JSON)
- **NULL**: Field explicitly set to null (included as `null` in JSON)
- **PRESENT**: Field has a non-null value

```java
import com.auth0.client.myorganization.core.OptionalNullable;

UpdateRequest request = UpdateRequest.builder()
    .fieldName(OptionalNullable.absent())    // Skip field
    .anotherField(OptionalNullable.ofNull()) // Clear field
    .yetAnotherField(OptionalNullable.of("value")) // Set value
    .build();
```

> **Note:** For required fields, you cannot use `absent()`. Required fields must always be present with either a non-null value or explicitly set to null using `ofNull()`.

### Retries

The SDK automatically retries requests with exponential backoff on the following status codes:

- `408` (Timeout)
- `429` (Too Many Requests)
- `5XX` (Internal Server Errors)

The default retry limit is 2 attempts. The `Retry-After` and `X-RateLimit-Reset` headers are respected when present. Configure via `.maxRetries()`:

```java
MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .clientCredentials("YOUR_CLIENT_ID", "YOUR_CLIENT_SECRET")
    .maxRetries(5)
    .build();
```

### Timeouts

The SDK defaults to a 60 second timeout. Configure at the client or request level:

```java
import com.auth0.client.myorganization.auth.MyOrganizationClient;
import com.auth0.client.myorganization.core.RequestOptions;

// Client level
MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .clientCredentials("YOUR_CLIENT_ID", "YOUR_CLIENT_SECRET")
    .timeout(30)
    .build();

// Request level
client.organization().domains().create(
    ...,
    RequestOptions.builder()
        .timeout(60)
        .build()
);
```

### Base URL

The base URL defaults to `https://{domain}/my-org`. The client automatically derives the base URL from the domain:

```java
MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .clientCredentials("YOUR_CLIENT_ID", "YOUR_CLIENT_SECRET")
    .build();
```

### Custom HTTP Client

The SDK is built to work with any instance of `OkHttpClient`. By default, if no client is provided, the SDK will construct one:

```java
import com.auth0.client.myorganization.auth.MyOrganizationClient;
import okhttp3.OkHttpClient;

OkHttpClient customClient = ...;

MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .clientCredentials("YOUR_CLIENT_ID", "YOUR_CLIENT_SECRET")
    .httpClient(customClient)
    .build();
```

### Organization

Use `.organization()` to specify the organization name or ID. The value is sent as the `organization` parameter in OAuth2 token requests (client credentials and private key JWT flows).

```java
MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .clientCredentials("YOUR_CLIENT_ID", "YOUR_CLIENT_SECRET")
    .organization("org_abc123")
    .build();
```

### Using the Fern Client Directly

If you need to use the underlying Fern-generated client directly, you can instantiate `MyOrganizationApi`:

```java
import com.auth0.client.myorganization.MyOrganizationApi;

MyOrganizationApi client = MyOrganizationApi.builder()
    .token("<token>")
    .tenantDomain("YOUR_TENANT_DOMAIN")
    .build();
```

> **Note:** The Fern client (`MyOrganizationApi`) does **not** support automatic token management, token caching/refresh, Auth0 telemetry headers, or domain-based URL derivation. You are responsible for obtaining and refreshing access tokens yourself. For most use cases, prefer `MyOrganizationClient` as shown in the [Usage](#usage) section.

## Feedback

### Contributing

We appreciate feedback and contribution to this repo! Before you get started, please see the following:

- [Auth0's General Contribution Guidelines](https://github.com/auth0/open-source-template/blob/master/GENERAL-CONTRIBUTING.md)
- [Auth0's Code of Conduct Guidelines](https://github.com/auth0/open-source-template/blob/master/CODE-OF-CONDUCT.md)

While we value open-source contributions to this SDK, this library is generated programmatically. Additions made directly to this library would have to be moved over to our generation code, otherwise they would be overwritten upon the next generated release. Feel free to open a PR as a proof of concept, but know that we will not be able to merge it as-is. We suggest opening an issue first to discuss with us!

### Raise an Issue

To provide feedback or report a bug, please [raise an issue on our issue tracker](https://github.com/auth0/myorganization-java/issues).

### Vulnerability Reporting

Please do not report security vulnerabilities on the public GitHub issue tracker. The [Responsible Disclosure Program](https://auth0.com/responsible-disclosure-policy) details the procedure for disclosing security issues.

---

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: light)" srcset="https://cdn.auth0.com/website/sdks/logos/auth0_light_mode.png" width="150">
    <source media="(prefers-color-scheme: dark)" srcset="https://cdn.auth0.com/website/sdks/logos/auth0_dark_mode.png" width="150">
    <img alt="Auth0 Logo" src="https://cdn.auth0.com/website/sdks/logos/auth0_light_mode.png" width="150">
  </picture>
</p>

<p align="center">Auth0 is an easy to implement, adaptable authentication and authorization platform.<br />To learn more check out <a href="https://auth0.com/why-auth0">Why Auth0?</a></p>

<p align="center">This project is licensed under the Apache-2.0 license. See the <a href="./LICENSE"> LICENSE</a> file for more info.</p>
