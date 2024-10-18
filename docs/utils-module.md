# Spring Common Utils Module

The `spring-common-utils` module provides a collection of utility classes designed to simplify common tasks in Spring Boot applications. This module includes utilities for serialization, object mapping, and other cross-cutting concerns that promote code reuse and maintainability across multiple microservices.

## HTTP Utilities

### HttpTraceHeader
The `HttpTraceHeader` class provides utility methods for managing HTTP headers in Spring Boot applications. It focuses on extracting and processing default HTTP trace headers, particularly useful for tracking requests across different services and ensuring that each request is uniquely identifiable.

## Serialization Utilities

### ByteHandlerUtil
The `ByteHandlerUtil` class provides methods for generating byte arrays from objects and comparing byte arrays. It simplifies serialization tasks, especially when working with byte-based data processing.

### ObjectMapperUtil
The `ObjectMapperUtil` class configures an instance of `ObjectMapper` for JSON serialization and deserialization, providing a consistent approach for handling JSON data across the application.

## How to Use the Common Utils Module

### 1. Importing the Module
To use the `spring-common-utils` module in your project, add the following dependency in your `pom.xml` (for Maven). Make sure to replace `1.0.1-SNAPSHOT` with the appropriate version of the module you are using.

```xml
<!-- Common Utils -->
<dependency>
  <groupId>com.erebelo</groupId>
  <artifactId>spring-common-utils</artifactId>
  <version>1.0.1-SNAPSHOT</version>
</dependency>
```