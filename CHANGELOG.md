# Changelog

## [1.0.0-beta.0](https://github.com/auth0/myorganization-java/tree/1.0.0-beta.0) (2026-04-09)

### Features

- **Organization Management** - Full SDK for managing Auth0 organization domains, identity providers, provisioning, and SCIM tokens.
- **Automatic Token Management** - `MyOrganizationClient` wraps the Fern-generated client with built-in token fetching, caching, and auto-refresh (60-second buffer before expiry).
- **Multiple Authentication Methods** - Flexible authentication options:
  - **Client Credentials** - OAuth2 client_credentials grant for M2M authentication.
  - **Private Key JWT** - Client assertion flow with RS256, RS384, RS512, ES256, ES384, ES512.
  - **Static Token** - For testing or pre-obtained tokens.
  - **Custom Token Provider** - Bring your own `TokenProvider` implementation.
- **OptionalNullable for PATCH Requests** - Three-state nullable semantics (`absent`, `null`, `present`) for partial updates.
- **Auth0 Telemetry** - Automatic `Auth0-Client` and `User-Agent` headers on every request.
- **Domain-Based URL Derivation** - Automatically derives the base URL and token endpoint from the Auth0 tenant domain.
- **Raw Response Access** - Access HTTP headers and status codes via `withRawResponse()`.
- **Configurable Retries** - Automatic retries with exponential backoff on 408, 429, and 5XX responses, respecting `Retry-After` and `X-RateLimit-Reset` headers.
- **Configurable Timeouts** - Client-level and per-request timeout configuration.
- **Custom Headers** - Add custom HTTP headers at the client or request level.

### Installation

**Gradle**

```groovy
implementation 'com.auth0:myorganization-java:1.0.0-beta.0'
```

**Maven**

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>myorganization-java</artifactId>
    <version>1.0.0-beta.0</version>
</dependency>
```

### Basic Usage

```java
import com.auth0.client.myorganization.auth.MyOrganizationClient;

MyOrganizationClient client = MyOrganizationClient.builder()
    .domain("acme.auth0.com")
    .clientCredentials("YOUR_CLIENT_ID", "YOUR_CLIENT_SECRET")
    .organization("org_abc123")
    .build();

System.out.println(client.organizationDetails().get());
```

### Dependencies

| Dependency | Version |
|---|---|
| OkHttp | 5.2.1 |
| Jackson Databind | 2.21.2 |
| Jackson Datatype JDK8 | 2.21.2 |
| Jackson Datatype JSR310 | 2.21.2 |
| Auth0 java-jwt | 4.4.0 |

**Runtime Requirements:**
- Java 8+
