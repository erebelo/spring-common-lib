# Spring Common Http Module

The `spring-common-http` module supplies pre-configured HTTP client utilities tailored for Spring Boot applications, providing simplified access to external services. This module includes customizable `RestTemplate` configurations supporting both default and custom settings via HttpClientProperties, enabling efficient connection management, proxy configurations, and basic authentication.

## How to Use the Common Http Module

### 1. Importing the Module

To use the `spring-common-http` module in your project, add the following dependency in your `pom.xml` (for Maven). Make sure to replace `1.0.1-SNAPSHOT` with the appropriate version of the module you are using.

```xml
<!-- Common Http -->
<dependency>
    <groupId>com.erebelo</groupId>
    <artifactId>spring-common-http</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Inject the `RestTemplate` instance

```java
@Autowired
private RestTemplate restTemplate;
```

## Properties for `RestTemplate`

The following properties allow for the configuration of the `RestTemplate` behavior in the application.

For standard configurations, use the `default` alias name. To define additional custom `RestTemplate` configurations, replace `default` with a unique alias, such as `serviceTwo`.

### 1. (Optional) Disabling for `RestTemplate` instance from Common Http Module

| Property Key                        | Default Value | Description                                                                                                                                                                                                                                                                                              |
| ----------------------------------- | ------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `spring.common.http-client.enabled` | `true`        | Enables or disables the `RestTemplate` instance from the `spring-common-http` module. When set to `true`, the customized Http client is active, allowing for connection management, proxy configurations, and basic authentication. Set this property to `false` to disable the module's `RestTemplate`. |

**NOTE**: When disabling this property, ensure to create a default `RestTemplate` bean in your configuration as follows:

```java
@Bean
public RestTemplate defaultRestTemplate() {
    return new RestTemplate();
}
```

### 2. (Optional) Customizing the Connection Settings

| Property Key                                                           | Default Value  | Description                  |
| ---------------------------------------------------------------------- | -------------- | ---------------------------- |
| `spring.common.http-client.services.default.request.conn-timeout`      | 3000 (Integer) | Connection timeout (ms)      |
| `spring.common.http-client.services.default.request.conn-read-timeout` | 5000 (Integer) | Connection read timeout (ms) |

### 3. (Optional) Requests with Basic Authorization

For requests where `RestTemplate` needs to automatically add `Authorization` header, add the following properties to configuration file (`application.properties`):

```properties
spring.common.http-client.services.default.auth.user=${API_USER}
spring.common.http-client.services.default.auth.pwd=${API_PASSWORD}
```

### 4. (Optional) Requests to External APIs with Proxy Authentication

To send requests that bypass any Gateway, configuring proxy authentication may be necessary. In the configuration file (`application.properties`) add the following properties:

```properties
spring.common.http-client.services.default.external=true
spring.common.http-client.proxy.host=${PROXY_HOST}
spring.common.http-client.proxy.port=${PROXY_PORT}
spring.common.http-client.proxy.user=${PROXY_USER}
spring.common.http-client.proxy.pwd=${PROXY_PASSWORD}
```

### 5. (Optional) Multiple `RestTemplate` instances

A default `RestTemplate` instance is always created with the basic configurations specified under the `default` alias. Additionally, you can configure custom `RestTemplate` instances for unique connection requirements or distinct authorization settings by specifying unique aliases.

For example, to create a second `RestTemplate` instance with the alias name `serviceTwo` you can use the following allowed properties:

```properties
spring.common.http-client.services.serviceTwo.request.conn-timeout=${CONN_TIMEOUT}
spring.common.http-client.services.serviceTwo.request.conn-read-timeout=${CONN_READ_TIMEOUT}
spring.common.http-client.services.serviceTwo.auth.user=${API_USER}
spring.common.http-client.services.serviceTwo.auth.pwd=${API_PASSWORD}
```

In the application, refer to the custom `RestTemplate` instance by its alias using the `@Qualifier` annotation:

```java
@Autowired
@Qualifier("serviceTwoRestTemplate")
private RestTemplate serviceTwoRestTemplate;
```

**NOTE**: The alias (`serviceTwo`) is case-sensitive and must match exactly in the `@Qualifier` annotation, including the `RestTemplate` suffix.

## Usage of `httpapi` Module

When using the `httpapi` module in your project, the module automatically intercepts and propagates filtered headers from the incoming request to any outgoing API call made with `RestTemplate.exchange()`.
This means that only the allowed headers are passed along, eliminating the need to manually include them in the `HttpEntity`.

### Passing `null` for `HttpEntity`

You can pass `null` in `HttpEntity` if no additional headers or body are needed. The filter will still carry over
the allowed headers from the original request.

```java
ResponseEntity<String> response = restTemplate.exchange(
        apiUrl,
        HttpMethod.GET,
        null,
        String.class
);
```

### Custom Headers

If additional headers are required by the API you’re calling (beyond those that are filtered and propagated),
you can still include them in the `HttpEntity`.

```java
HttpHeaders headers = new HttpHeaders();
headers.set("Custom-Header", "customValue");

ResponseEntity<String> response = restTemplate.exchange(
        apiUrl,
        HttpMethod.GET,
        new HttpEntity<>(headers),
        String.class
);
```
