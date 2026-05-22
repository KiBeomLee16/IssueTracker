# Architecture

This document describes the overall architecture of the Issue Tracker API.

## System Architecture

```mermaid
flowchart TD
    Client[Client / Postman / Swagger UI] --> Controller[Controller Layer]
    Controller --> Service[Service Layer]
    Service --> Repository[Repository Layer]
    Repository --> DB[(MySQL Database)]

    Controller --> Security[Spring Security]
    Security --> JWT[JWT Authentication Filter]
    JWT --> Controller
```

## Docker Compose Architecture

```mermaid
flowchart TD
    User[User Browser / Postman] --> App[Spring Boot App Container]
    App --> MySQL[(MySQL 8 Container)]
    MySQL --> Volume[(MySQL Docker Volume)]

    App -. reads env .-> Env[.env]
```

## Authentication Flow

```mermaid
sequenceDiagram
    participant Client
    participant AuthController
    participant AuthService
    participant JwtProvider
    participant API

    Client->>AuthController: POST /api/auth/login
    AuthController->>AuthService: validate credentials
    AuthService->>JwtProvider: generate accessToken
    JwtProvider-->>AuthService: JWT accessToken
    AuthService-->>Client: accessToken

    Client->>API: Request with Authorization: Bearer token
    API->>API: JwtAuthenticationFilter validates token
    API-->>Client: Protected resource response
```

## Layer Responsibilities

| Layer | Responsibility |
|---|---|
| Controller | Handles HTTP requests and responses |
| Service | Contains business logic |
| Repository | Handles database access through Spring Data JPA |
| Entity | Represents database tables |
| DTO | Transfers request and response data |
| Security | Handles JWT authentication and role-based authorization |
| Database | Stores users, projects, issues, and comments |
