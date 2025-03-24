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

1. **Generate a Personal Access Token**:

   Go to your GitHub account -> **Settings** -> **Developer settings** -> **Personal access tokens** -> **Tokens (classic)** -> **Generate new token (classic)**:

   - Fill out the **Note** field: `Pull packages`.
   - Set the scope:
     - `read:packages` (to download packages)
   - Click **Generate token**.

2. **Set Up Maven Authentication**:

   In your local Maven `settings.xml`, define the GitHub repository authentication using the following structure:

   ```xml
   <servers>
     <server>
       <id>github</id>
       <username>USERNAME</username>
       <password>TOKEN</password>
     </server>
   </servers>
   ```

   **NOTE**: Replace `USERNAME` with your GitHub username and `TOKEN` with the personal access token you just generated.
   Configure Repositories:
   In your child modules, configure the repositories section to include the GitHub Packages repository, where the spring-common-parent is hosted. This will allow your child modules to resolve dependencies from GitHub Packages.

3. **Configure Repositories**:

   Add the GitHub Packages repository to the `repositories` section in the `pom.xml` to enable dependency resolution from GitHub Packages:

   ```xml
     <repositories>
       <repository>
         <id>github</id>
         <url>https://maven.pkg.github.com/erebelo/spring-common-lib</url>
       </repository>
     </repositories>
   ```

4. Refer to the **[Features](#features)** section above, where each module includes a link to its documentation for instructions on how to import and use it.

## Run App

Use the following command to build and format the project:

```sh
mvn clean install
```

## Publish a New Package Version

To publish a new version of the package to GitHub Packages, follow these steps:

1. **Generate a Personal Access Token**:

   Go to GitHub account -> **Settings** -> **Developer settings** -> **Personal access tokens** -> **Tokens (classic)** -> **Generate new token (classic)**.

   - Fill out the **Note** field: `Package publishing`.
   - Set the following scopes:
     - `write:packages` (to publish packages)
     - `read:packages` (to download packages)
   - Click **Generate token**.

2. **Set Up Maven Authentication**:

   In your local Maven `settings.xml`, define the GitHub repository authentication using the following structure:

   ```xml
   <servers>
     <server>
       <id>github</id>
       <username>USERNAME</username>
       <password>TOKEN</password>
     </server>
   </servers>
   ```

   **NOTE**: Replace `USERNAME` with your GitHub username and `TOKEN` with the personal access token you just generated.

3. **Update the Package Version**:

   - Create a release branch from the `main` branch, named `release-X.X.X`, where `X.X.X` is the new version number you want to publish:

     ```sh
     git checkout main
     git checkout -b release-2.0.0
     ```

   - Update the version in pom.xml of the project to the desired new version number:

     ```xml
     <version>2.0.0</version>
     ```

   - Commit and push the changes to the remote repository:

     ```sh
     git add .
     git commit -m "Package release version 2.0.0"
     git push origin release-X.X.X
     ```

4. **Deploy to GitHub Packages**:

   Inside IntelliJ IDEA, open your project and navigate to **Maven** -> **Execute Maven Goal**:

   - Enter `deploy` and press **Enter**. This will run `mvn deploy` using the authentication settings from your `settings.xml`.

5. **Verify the Package**:
   Once the deployment is successful, navigate to your GitHub repository and go to the **Packages** section to verify that the new version of the package is listed.
