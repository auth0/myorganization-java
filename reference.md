# Reference
## OrganizationDetails
<details><summary><code>client.organizationDetails.get() -> OrgDetailsRead</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve details for this Organization, including display name and branding options. To learn more about Auth0 Organizations, read [Organizations](https://auth0.com/docs/manage-users/organizations).
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organizationDetails().get();
```
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organizationDetails.update(request) -> OrgDetailsRead</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update details for this Organization, such as display name and branding options. To learn more about Auth0 Organizations, read [Organizations](https://auth0.com/docs/manage-users/organizations).
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organizationDetails().update(
    OrgDetails
        .builder()
        .name(
            OptionalNullable.of("testorg")
        )
        .displayName(
            OptionalNullable.of("Test Organization")
        )
        .branding(
            OptionalNullable.of(
                OrgBranding
                    .builder()
                    .logoUrl(
                        OptionalNullable.of("https://example.com/logo.png")
                    )
                    .colors(
                        OptionalNullable.of(
                            OrgBrandingColors
                                .builder()
                                .primary("#000000")
                                .pageBackground("#FFFFFF")
                                .build()
                        )
                    )
                    .build()
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**request:** `OrgDetails` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Organization Configuration
<details><summary><code>client.organization.configuration.get() -> GetConfigurationResponseContent</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the My Organization API configuration. Returns only the `connection_deletion_behavior` and `allowed_strategies`. Identifier attributes such as `user_attribute_profile_id` and `connection_profile_id` are not included. Cache this information, as it does not change frequently.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().configuration().get();
```
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Organization Domains
<details><summary><code>client.organization.domains.list() -> SyncPagingIterable&amp;lt;OrgDomain&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a list of all pending and verified domains for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().domains().list(
    ListOrganizationDomainsRequestParameters
        .builder()
        .from(
            OptionalNullable.of("from")
        )
        .take(
            OptionalNullable.of(1)
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**from:** `Optional<String>` — An optional cursor from which to start the selection (exclusive).
    
</dd>
</dl>

<dl>
<dd>

**take:** `Optional<Integer>` — Number of results per page. Defaults to 50.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.domains.create(request) -> OrgDomain</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new domain for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().domains().create(
    CreateOrganizationDomainRequestContent
        .builder()
        .domain("acme.com")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**domain:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.domains.get(domainId) -> OrgDomain</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve details of a domain specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().domains().get("domain_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**domainId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.domains.delete(domainId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove a domain specified by ID from this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().domains().delete("domain_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**domainId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Organization IdentityProviders
<details><summary><code>client.organization.identityProviders.list() -> ListIdentityProvidersResponseContent</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a list of all Identity Providers for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().list();
```
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.create(request) -> IdpKnownResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new Identity Provider for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().create(
    IdpKnownRequest.of(
        IdpOidcRequest
            .builder()
            .strategy(IdpOidcRequestStrategy.OIDC)
            .options(
                IdpOidcOptionsRequest
                    .builder()
                    .type(IdpOidcOptionsTypeEnum.FRONT_CHANNEL)
                    .clientId("a8f3b2e7-5d1c-4f9a-8b0d-2e1c3a5b6f7d")
                    .clientSecret(
                        OptionalNullable.of("KzQp2sVxR8nTgMjFhYcEWuLoIbDvUoC6A9B1zX7yWqFjHkGrP5sQdLmNp")
                    )
                    .discoveryUrl("https://{yourDomain}/.well-known/openid-configuration")
                    .build()
            )
            .name("oidcIdp")
            .domains(
                OptionalNullable.of(
                    Arrays.asList("mydomain.com")
                )
            )
            .displayName(
                OptionalNullable.of("OIDC IdP")
            )
            .showAsButton(
                OptionalNullable.of(true)
            )
            .assignMembershipOnLogin(
                OptionalNullable.of(false)
            )
            .isEnabled(
                OptionalNullable.of(true)
            )
            .build()
    )
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**request:** `IdpKnownRequest` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.get(idpId) -> IdpKnownResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve details of an Identity Provider specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().get("idp_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.delete(idpId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete an Identity Provider specified by ID from this Organization. This will remove the association and delete the underlying Identity Provider. Members will no longer be able to authenticate using this Identity Provider.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().delete("idp_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.update(idpId, request) -> IdpUpdateKnownResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Update the details of an Identity Provider specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().update(
    "idp_id",
    IdpUpdateKnownRequest.of(
        IdpAdfsUpdateRequest
            .builder()
            .displayName(
                OptionalNullable.of("OIDC IdP")
            )
            .showAsButton(
                OptionalNullable.of(true)
            )
            .assignMembershipOnLogin(
                OptionalNullable.of(false)
            )
            .isEnabled(
                OptionalNullable.of(true)
            )
            .options(
                OptionalNullable.of(
                    IdpAdfsOptionsRequest.of(
                        FedMetadataXml
                            .builder()
                            .build()
                    )
                )
            )
            .build()
    )
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>

<dl>
<dd>

**request:** `IdpUpdateKnownRequest` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.updateAttributes(idpId, request) -> IdpKnownResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Refresh the attribute mapping for an Identity Provider specified by ID for this Organization. Mappings are reset to the admin-defined defaults.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().updateAttributes(
    "idp_id",
    new HashMap<String, Object>() {{
        put("key", "value");
    }}
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>

<dl>
<dd>

**request:** `Map<String, Object>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.detach(idpId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove an Identity Provider specified by ID from this Organization. This only removes the association; the underlying Identity Provider is not deleted. Members will no longer be able to authenticate using this Identity Provider.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().detach("idp_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Organization Configuration IdentityProviders
<details><summary><code>client.organization.configuration.identityProviders.get() -> IdentityProvidersConfig</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the [Connection Profile](https://auth0.com/docs/authenticate/enterprise-connections/connection-profile) for this application. You should cache this information as it does not change frequently.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().configuration().identityProviders().get();
```
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Organization Domains Verify
<details><summary><code>client.organization.domains.verify.create(domainId) -> OrgDomain</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Initiate the verification process for a domain specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().domains().verify().create("domain_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**domainId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Organization Domains IdentityProviders
<details><summary><code>client.organization.domains.identityProviders.get(domainId) -> ListDomainIdentityProvidersResponseContent</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the list of Identity Providers associated with a domain specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().domains().identityProviders().get("domain_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**domainId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Organization IdentityProviders Domains
<details><summary><code>client.organization.identityProviders.domains.create(idpId, request) -> CreateIdpDomainResponseContent</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Associate a domain with an Identity Provider specified by ID for this Organization. The domain must be claimed and verified.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().domains().create(
    "idp_id",
    CreateIdpDomainRequestContent
        .builder()
        .domain("my-domain.com")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>

<dl>
<dd>

**domain:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.domains.delete(idpId, domain)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Remove a domain specified by name from an Identity Provider specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().domains().delete("idp_id", "domain");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>

<dl>
<dd>

**domain:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Organization IdentityProviders Provisioning
<details><summary><code>client.organization.identityProviders.provisioning.get(idpId) -> GetIdPProvisioningConfigResponseContent</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve the Provisioning Configuration for an Identity Provider specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().provisioning().get("idp_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.provisioning.create(idpId) -> CreateIdPProvisioningConfigResponseContent</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new Provisioning Configuration for an Identity Provider specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().provisioning().create("idp_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.provisioning.delete(idpId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Delete the Provisioning Configuration for an Identity Provider specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().provisioning().delete("idp_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.provisioning.updateAttributes(idpId, request) -> GetIdPProvisioningConfigResponseContent</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Refresh the attribute mapping for the Provisioning Configuration of an Identity Provider specified by ID for this Organization. Mappings are reset to the admin-defined defaults.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().provisioning().updateAttributes(
    "idp_id",
    new HashMap<String, Object>() {{
        put("key", "value");
    }}
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>

<dl>
<dd>

**request:** `Map<String, Object>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Organization IdentityProviders Provisioning ScimTokens
<details><summary><code>client.organization.identityProviders.provisioning.scimTokens.list(idpId) -> ListIdpProvisioningScimTokensResponseContent</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a list of [SCIM tokens](https://auth0.com/docs/authenticate/protocols/scim/configure-inbound-scim#scim-endpoints-and-tokens) for the Provisioning Configuration of an Identity Provider specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().provisioning().scimTokens().list("idp_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.provisioning.scimTokens.create(idpId, request) -> IdpScimTokenCreate</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Create a new SCIM token for the Provisioning Configuration of an Identity Provider specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().provisioning().scimTokens().create(
    "idp_id",
    CreateIdpProvisioningScimTokenRequestContent
        .builder()
        .tokenLifetime(
            OptionalNullable.of(86400)
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>

<dl>
<dd>

**tokenLifetime:** `Optional<Integer>` — Lifetime of the token in seconds. Do not set for non-expiring tokens.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.organization.identityProviders.provisioning.scimTokens.delete(idpId, idpScimTokenId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Revoke a SCIM token specified by token ID for the Provisioning Configuration of an Identity Provider specified by ID for this Organization.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.organization().identityProviders().provisioning().scimTokens().delete("idp_id", "idp_scim_token_id");
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idpId:** `String` 
    
</dd>
</dl>

<dl>
<dd>

**idpScimTokenId:** `String` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

