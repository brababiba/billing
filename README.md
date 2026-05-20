# Billing Platform Backend

Backend prototype of a multi-tenant billing platform built with Java and Spring Boot.

The project is focused on learning modern backend development practices, clean architecture, security, testing, and scalable SaaS-oriented design.

## Features

- JWT authentication
- Workspace-based multi-tenant model
- Role & permission foundation (RBAC)
- Workspace-scoped access control
- REST API with Spring Boot
- PostgreSQL + Flyway migrations
- Swagger / OpenAPI documentation
- Integration testing with MockMvc
- Docker-based local environment

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker
- Maven
- Swagger / OpenAPI
- JUnit 5

## Running Locally

Start PostgreSQL:

```bash
docker compose up -d
```

Run the application:

```bash
./mvnw spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Running Tests

```bash
./mvnw test
```

## Current Focus

- Workspace authorization
- Permission-based RBAC
- Billing and subscription domain modeling
- SaaS-oriented backend architecture
