# Spring Common Lib

Common Library for Spring Boot applications, providing shared utilities and cross-cutting features across multiple modules for use throughout microservices.

## Features

- **Common Logging**: Utilizes Log4j2 for application logging, featuring an ECS (Elastic Common Schema) layout for JSON format in non-local environments.

## Requirements

- Java 17
- Spring Boot 3.3.4
- Apache Maven 3.8.6

## References

- This project utilizes the [spring-common-parent](https://github.com/erebelo/spring-common-parent) to manage the Spring Boot version and provide common configurations for plugins and formatting

## How to Use

1. **Add Dependencies**: Include the specific module dependencies you need in your child module's `pom.xml`. For example, to add the logging module:

  ```xml
  <dependency>
      <groupId>com.erebelo</groupId>
      <artifactId>common-logging</artifactId>
      <version>1.0.1-SNAPSHOT</version>
  </dependency>
  ```

2. **Customize as Needed**: Add any additional dependencies specific to your module within its `pom.xml`.

## Run App

Use the following command to build and format the project:

```sh
mvn clean install
```
