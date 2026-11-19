# AGENTS.md

## Project overview

- Java 27 Spring Boot 4.1.2 application for a PIX key manager.
- Artifact: `com.bancomundi:pix-key-manager`.
- The base package is `com.bancomundi.pix_key_manager` (underscore, not the original hyphenated name).
- The application starts in `src/main/java/com/bancomundi/pix_key_manager/PixKeyManagerApplication.java`.
- The application currently has no domain, controller, repository, or service classes beyond the Spring Boot entry point.

## Repository layout

- `pom.xml` — Maven dependencies and build configuration.
- `src/main/java/` — application code.
- `src/main/resources/application.yaml` — Spring configuration; currently only sets `spring.application.name=pix-key-manager`.
- `src/test/java/` — JUnit tests.
- `target/` — generated Maven output; do not edit it.
- `mvnw` / `mvnw.cmd` — Maven Wrapper entry points.

## Build and test commands

- Run all tests: `./mvnw test` (use `mvnw.cmd test` in Windows Command Prompt or PowerShell).
- Package the application: `./mvnw package`.
- Start the application: `./mvnw spring-boot:run`.
- Run a one-off Maven goal with the wrapper rather than relying on a system Maven installation.

The Maven build currently validates by compiling with Java 27 and running the Spring context test. Verify changes with `./mvnw test` before finishing.

## Dependencies and integrations

- `spring-boot-starter-webmvc` provides the web application stack.
- `spring-boot-starter-actuator` provides Actuator endpoints.
- `spring-boot-h2console` provides the H2 console integration.
- H2 is configured as a runtime dependency; no explicit H2 datasource or schema configuration exists yet.
- Keep new Spring configuration in `src/main/resources/application.yaml` or feature-specific Java configuration, and place dependency declarations in `pom.xml`.

## Code and test conventions

- Use the existing package `com.bancomundi.pix_key_manager` for application classes.
- Follow standard Spring Boot component placement under the root package so `@SpringBootApplication` can discover components.
- Use JUnit 5 with `@SpringBootTest` for application-context tests, following `PixKeyManagerApplicationTests` in `src/test/java/com/bancomundi/pix_key_manager/PixKeyManagerApplicationTests.java`.
- Keep generated `target/` files out of source changes.

