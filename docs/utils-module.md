# Spring Common Utils Module

The `spring-common-utils` module provides a collection of utility classes designed to simplify common tasks in Spring Boot applications. This module includes utilities for serialization, object mapping, and other cross-cutting concerns that promote code reuse and maintainability across multiple microservices.

## Utilities

### HTTP

- `HttpTraceHeader`: Provides methods for managing HTTP headers in Spring Boot applications. It focuses on extracting and processing default HTTP trace headers, particularly useful for tracking requests across different services and ensuring that each request is uniquely identifiable.

### Serialization

- `ByteHandler`: Provides methods for generating byte arrays from objects and comparing byte arrays. It simplifies serialization tasks, especially when working with byte-based data processing.
- `ObjectMapperProvider`: Configures an instance of `ObjectMapper` for JSON serialization and deserialization, providing a consistent approach for handling JSON data across the application.

### Threading

- `AsyncThreadContext`: Provides methods for propagating the current HTTP request attributes and ThreadContext to new threads created by asynchronous operations, such as those initiated by CompletableFuture. This is particularly useful for maintaining context-specific data (like HTTP headers) across threads, ensuring that logging and other context-sensitive operations remain consistent.
- `AsyncExecutorConfiguration`: Configures a `ThreadPoolTaskExecutor` for executing asynchronous tasks while preserving the current HTTP request attributes and ThreadContext in new threads. This ensures that the context from the original request is maintained, enabling seamless tracing and consistent logging across asynchronous operations.
- `ParallelStreamContext`: Provides a utility to process elements of a stream in parallel while preserving the HTTP request attributes and ThreadContext across threads.

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

## Properties for Utils

The following property allow setting up an asynchronous task executor with a customized thread pool configuration in the application:

### 1. (Optional) Enabling for a Customized Thread Pool

| Property Key                                | Default Value | Description                                                                                                                                                                                                           |
| ------------------------------------------- |---------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `spring.common.async-task-executor.enabled` | `false`       | Enables or disables the `AsyncExecutorConfiguration`. When set to `true`, the custom thread pool settings are activated, and the current HTTP request attributes and ThreadContext and are propagated to new threads. |
