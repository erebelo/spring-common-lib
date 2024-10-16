# Spring Common Lib

Common Library for Spring Boot applications, providing shared utilities and cross-cutting features across multiple modules for use throughout microservices.

## Features

- **[Logging](https://github.com/erebelo/spring-common-lib/tree/main/docs/logging-module.md)**: Utilizes Log4j2 for application logging, featuring an ECS (Elastic Common Schema) layout for JSON format in non-local environments.

## Requirements

- Java 17
- Spring Boot 3.3.4
- Apache Maven 3.8.6

## References

- This project utilizes the [spring-common-parent](https://github.com/erebelo/spring-common-parent) to manage the Spring Boot version and provide common configurations for plugins and formatting.

## How to Use

Refer to the **[Features](#features)** section above, where each module includes a link to its documentation for instructions on how to import and use it.

## Run App

Use the following command to build and format the project:

```sh
mvn clean install
```
