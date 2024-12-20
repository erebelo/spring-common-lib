# Spring Common Lib

Common Library for Spring Boot applications, providing shared utilities and cross-cutting features across multiple modules for use throughout microservices.

## Features

- **[Logging](https://github.com/erebelo/spring-common-lib/tree/main/docs/logging-module.md)**: Utilizes Log4j2 for application logging, featuring an ECS (Elastic Common Schema) layout for JSON format in non-local environments.
- **[Http](https://github.com/erebelo/spring-common-lib/tree/main/docs/http-module.md)**: Provides pre-configured HTTP client utilities, offering customizable RestTemplate configurations for efficient connection management, proxy settings, and basic authentication.
- **[Utils](https://github.com/erebelo/spring-common-lib/tree/main/docs/utils-module.md)**: Includes utility classes for serialization, object mapping, managing thread context in asynchronous operations, and configuring asynchronous task execution.

## Requirements

- Java 17
- Spring Boot 3.x.x
- Apache Maven 3.8.6

## Libraries

- [spring-common-parent](https://github.com/erebelo/spring-common-parent): Manages the Spring Boot version and provide common configurations for plugins and formatting.

## How to Use

Refer to the **[Features](#features)** section above, where each module includes a link to its documentation for instructions on how to import and use it.

## Run App

Use the following command to build and format the project:

```sh
mvn clean install
```
