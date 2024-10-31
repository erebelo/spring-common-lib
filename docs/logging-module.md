# String Common Logging Module

The `spring-common-logging` module provides a unified logging solution across multiple projects. This module uses **Log4j2** for logging functionality and incorporates **[ECS (Elastic Common Schema)](https://www.elastic.co/guide/en/ecs-logging/java/current/setup.html)** layout for environments beyond local development. The ECS layout ensures that logs are structured in a standardized JSON format, making it easier to handle and process logs across different logging systems.

### Headers Included in Logs

All headers filtered by the `ThreadContextFilter` (such as `RequestID`) are included in the logs. If `RequestID` is not present in the request, a value will be generated automatically using a UUID with the prefix `GEN-` (indicating **GENERATED**).

## Logging Output Examples

The following section illustrates how the logging output will appear when using the `spring-common-logging` module, both in local and development (dev) environments.

### Local Environment

In the local environment, logs typically appear in plain text format. An example log entry might look like:

```
2024-10-16T17:12:23.406-03:00 INFO  [{RequestID=GEN-f54b40fb-ce87-4a57-b97d-bc61aa1f5413}] {21908} --- [nio-8080-exec-1] c.e.s.c.HealthCheckController           : Getting health check
```

### Dev Environment

In the development (or in QA, Stage, Prod) environment, where the ECS layout is used, logs will be structured in JSON format. An example log entry might look like:

```json
{
  "@timestamp": "2024-10-16T20:13:13.821Z",
  "log.level": "INFO",
  "message": "Getting health check",
  "ecs.version": "1.2.0",
  "service.name": "spring-mongodb-demo",
  "event.dataset": "spring-mongodb-demo",
  "process.thread.name": "http-nio-8080-exec-2",
  "log.logger": "com.erebelo.springmongodbdemo.controller.HealthCheckController",
  "RequestID": "GEN-f19a1452-2ac3-4df8-80c4-f9a6a835b96b"
}
```

## How to Use the Common Logging Module

### 1. Importing the Module

To use the `spring-common-logging` module in your project, add the following dependency in your `pom.xml` (for Maven). Make sure to replace `1.0.1-SNAPSHOT` with the appropriate version of the module you are using.

```xml
<!-- Common Logging -->
<dependency>
    <groupId>com.erebelo</groupId>
    <artifactId>spring-common-logging</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Exclude Spring Boot Native Logging

To prevent conflicts between the `spring-common-logging` module and Spring Boot’s native logging (which uses `spring-boot-starter-logging` by default), you need to exclude the native logging dependency from `spring-boot-starter-web`.

Add the following exclusions to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- Resolves logging conflicts -->
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## Properties for Logging

The following properties allow configuration of the logging behavior in the application:

### 1. (Optional) Disabling for Logging

| Property Key                                   | Default Value | Description                                                                                                                                                                                                         |
| ---------------------------------------------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `spring.common.logging-context-filter.enabled` | `true`        | Enables or disables the `ThreadContextFilter`. When set to `true`, the filter is active, and HTTP request headers are added to the logging context for tracing. Set this property to `false` to disable the filter. |

### 2. (Optional) Set the Service Name

To enhance the log information and specify the name of your service, you can declare the service name in your `application.properties` file. This name will be fetched by the ECS layout and printed in the logs under the `service.name` property. If not declared, the service name will be set as empty by default.

Add the following line to your `application.properties` file:

```properties
spring.application.name=service-name
```
