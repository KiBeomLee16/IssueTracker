# Issue Tracker REST API

A Java + Spring Boot based REST API for managing projects, issues, comments, and users.

This project was built as a personal backend portfolio project. It focuses on REST API development, layered architecture, validation, exception handling, testing, API documentation, CI, Docker, and Docker Compose based local infrastructure.

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Build Tool | Maven |
| Web | Spring Web MVC |
| ORM | Spring Data JPA, Hibernate |
| Database | MySQL |
| Validation | Jakarta Validation |
| Utility | Lombok |
| Monitoring | Spring Boot Actuator |
| API Testing | Postman |
| Testing | JUnit 5, Mockito, MockMvc |
| CI | GitHub Actions |
| API Docs | Swagger / OpenAPI |
| DevOps | Docker, Docker Compose |
| DevOps Planned | Docker image build in CI, Kubernetes, cloud or low-cost server deployment |

---

## Main Features

### Project

- Create project
- Get all projects
- Get project by ID
- Update project
- Delete project
- Get project issue statistics

### Issue

- Create issue
- Get issues by project
- Get issue by ID
- Update issue
- Delete issue
- Search and filter issues
- Pagination and sorting
- Update issue status
- Assign issue to user
- Unassign issue from user

### Comment

- Create comment
- Get comments by issue
- Get comment by ID
- Update comment
- Delete comment

### User

- Create user
- Get all users
- Get user by ID
- Update user
- Delete user

### Common

- Common API response format
- Validation handling
- Global exception handling
- Entity relationships
- REST-style URL design
- Swagger / OpenAPI documentation
- GitHub Actions CI
- Dockerfile support
- Docker Compose support with MySQL
- MySQL volume persistence

---

## Entity Relationships

 
Project 1 : N Issue
Issue   N : 1 Project
Issue   N : 1 User     // assignee
Issue   1 : N Comment
 

### Project

A project can have multiple issues.

### Issue

An issue belongs to one project. An issue can also have one assigned user.

### Comment

An issue belongs to one issue.

### User

A user can be assigned to issues.

---

## Package Structure

 
com.example.issuetracker
 ├── controller
 ├── dto
 ├── dto.response
 ├── dto.UpdateRequest
 ├── entity
 ├── exception
 ├── repository
 ├── response
 ├── service
 └── serviceImpl
 

---

## Common API Response Format

All API responses use a common response wrapper called `ApiResponse`.

### Success Response Example

 json
{
  "success": true,
  "message": "Project created successfully.",
  "data": {
    "id": 1,
    "name": "Issue Tracker",
    "description": "Issue tracker project"
  }
}
 

### Error Response Example

 json
{
  "success": false,
  "message": "Project not found.",
  "data": null
}
 

---

# API Documentation

## Swagger / OpenAPI

Swagger UI:

 
http://localhost:8080/swagger-ui/index.html
 

OpenAPI JSON:

 
http://localhost:8080/v3/api-docs
 

---

# API Endpoints

## Project API

| Method | URL | Description |
|---|---|---|
| POST | `/api/projects` | Create a project |
| GET | `/api/projects` | Get all projects |
| GET | `/api/projects/{projectId}` | Get a project by ID |
| PUT | `/api/projects/{projectId}` | Update a project |
| DELETE | `/api/projects/{projectId}` | Delete a project |
| GET | `/api/projects/{projectId}/stats` | Get project issue statistics |

### Create Project

 http
POST /api/projects
 

Request body:

 json
{
  "name": "Issue Tracker",
  "description": "Issue tracker REST API project"
}
 

Response example:

 json
{
  "success": true,
  "message": "Project created successfully.",
  "data": {
    "id": 1,
    "name": "Issue Tracker",
    "description": "Issue tracker REST API project"
  }
}
 

### Get Project Statistics

 http
GET /api/projects/{projectId}/stats
 

Response example:

 json
{
  "success": true,
  "message": "Project stats retrieved successfully.",
  "data": {
    "projectId": 1,
    "projectName": "Issue Tracker",
    "totalIssues": 10,
    "todoCount": 3,
    "inProgressCount": 4,
    "doneCount": 3
  }
}
 

---

## Issue API

| Method | URL | Description |
|---|---|---|
| POST | `/api/projects/{projectId}/issues` | Create an issue |
| GET | `/api/projects/{projectId}/issues` | Get issues by project |
| GET | `/api/issues/{issueId}` | Get an issue by ID |
| PUT | `/api/issues/{issueId}` | Update an issue |
| DELETE | `/api/issues/{issueId}` | Delete an issue |
| GET | `/api/projects/{projectId}/issues/page` | Search, filter, paginate, and sort issues |
| PATCH | `/api/issues/{issueId}/status` | Update issue status |
| PATCH | `/api/issues/{issueId}/assignee` | Assign issue to user |
| DELETE | `/api/issues/{issueId}/assignee` | Unassign issue from user |

### Create Issue

 http
POST /api/projects/{projectId}/issues
 

Request body:

 json
{
  "title": "Login API bug",
  "description": "Login API returns 500 error",
  "priority": "HIGH",
  "dueDate": "2026-06-30"
}
 

### Search, Filter, Paginate, and Sort Issues

 http
GET /api/projects/{projectId}/issues/page
 

Query parameter example:

 http
GET /api/projects/1/issues/page?page=0&size=10&sortBy=id&direction=desc
 

Filter example:

 http
GET /api/projects/1/issues/page?status=TODO&priority=HIGH&page=0&size=10&sortBy=id&direction=desc
 

### Update Issue Status

 http
PATCH /api/issues/{issueId}/status
 

Request body:

 json
{
  "status": "IN_PROGRESS"
}
 

Available status values:

 
TODO
IN_PROGRESS
DONE
 

### Assign Issue to User

 http
PATCH /api/issues/{issueId}/assignee
 

Request body:

 json
{
  "userId": 1
}
 

### Unassign Issue from User

 http
DELETE /api/issues/{issueId}/assignee
 

---

## Comment API

| Method | URL | Description |
|---|---|---|
| POST | `/api/issues/{issueId}/comments` | Create a comment |
| GET | `/api/issues/{issueId}/comments` | Get comments by issue |
| GET | `/api/comments/{commentId}` | Get a comment by ID |
| PUT | `/api/comments/{commentId}` | Update a comment |
| DELETE | `/api/comments/{commentId}` | Delete a comment |

### Create Comment

 http
POST /api/issues/{issueId}/comments
 

Request body:

 json
{
  "content": "This issue needs to be fixed first."
}
 

### Update Comment

 http
PUT /api/comments/{commentId}
 

Request body:

 json
{
  "content": "Updated comment content."
}
 

---

## User API

| Method | URL | Description |
|---|---|---|
| POST | `/api/users` | Create a user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{userId}` | Get a user by ID |
| PUT | `/api/users/{userId}` | Update a user |
| DELETE | `/api/users/{userId}` | Delete a user |

### Create User

 http
POST /api/users
 

Request body:

 json
{
  "name": "John Doe",
  "email": "john@example.com"
}
 

### Update User

 http
PUT /api/users/{userId}
 

Request body:

 json
{
  "name": "John Updated",
  "email": "john.updated@example.com"
}
 

---

# How to Run Locally

## 1. Clone the Repository

 bash
git clone https://github.com/your-username/issue-tracker-api.git
cd issue-tracker-api
 

## 2. Create MySQL Database

 sql
CREATE DATABASE issue_tracker;
 

## 3. Configure Database Settings

Example `application.yml`:

 yaml
server:
  port: 8080

spring:
  application:
    name: issue-tracker-api

  datasource:
    url: jdbc:mysql://localhost:3306/issue_tracker?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    open-in-view: false
    properties:
      hibernate:
        format_sql: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
 

Update `username`, `password`, and database name based on your local MySQL environment.

## 4. Run the Application

Using Maven Wrapper:

 bash
./mvnw spring-boot:run
 

On Windows:

 bash
mvnw.cmd spring-boot:run
 

Or using local Maven:

 bash
mvn spring-boot:run
 

Or run the main class from your IDE:

 
IssueTrackerApiApplication.java
 

## 5. Check Server Status

Base URL:

 
http://localhost:8080
 

Actuator health check:

 http
GET /actuator/health
 

Swagger UI:

 
http://localhost:8080/swagger-ui/index.html
 

OpenAPI JSON:

 
http://localhost:8080/v3/api-docs
 

---

# Docker Compose

Docker Compose is the recommended way to run this project locally because it starts both the Spring Boot application and MySQL together.

Docker Compose starts:

- Spring Boot API container
- MySQL 8 container
- Persistent MySQL volume

## Docker Compose Services

| Service | Description |
|---|---|
| `app` | Spring Boot application container |
| `mysql` | MySQL 8 database container |

## Database Connection in Docker Compose

The application connects to MySQL using the Docker Compose service name:

 
jdbc:mysql://mysql:3306/issue_tracker
 

The MySQL container is exposed to the local machine on port `3307` to avoid conflicts with a locally installed MySQL server.

 
Local MySQL access: localhost:3307
Docker network access: mysql:3306
 

## Start Containers

Build and start containers:

 bash
docker compose up --build
 

Start containers in detached mode:

 bash
docker compose up -d --build
 

## Stop Containers

Stop and remove containers:

 bash
docker compose down
 

Stop containers and remove the MySQL volume:

 bash
docker compose down -v
 

> Warning: `docker compose down -v` removes the MySQL volume and deletes all persisted database data.

## Check Running Containers

 bash
docker ps
 

Expected containers:

 
issue-tracker-api-app-1
issue-tracker-api-mysql-1
 

## Check Logs

Check all logs:

 bash
docker compose logs
 

Check application logs:

 bash
docker compose logs app
 

Check MySQL logs:

 bash
docker compose logs mysql
 

Follow application logs:

 bash
docker compose logs -f app
 

## Database Information

| Item | Value |
|---|---|
| Database | `issue_tracker` |
| Username | `root` |
| Password | `root` |
| MySQL Docker service | `mysql` |
| MySQL container port | `3306` |
| MySQL local port | `3307` |
| JDBC URL in Docker | `jdbc:mysql://mysql:3306/issue_tracker` |
| Volume | `mysql_data` |

## Access MySQL from Local Tools

For tools like MySQL Workbench or DBeaver:

 
Host: localhost
Port: 3307
Username: root
Password: root
Database: issue_tracker
 

## Verify Application

Swagger UI:

 
http://localhost:8080/swagger-ui/index.html
 

OpenAPI Docs:

 
http://localhost:8080/v3/api-docs
 

Actuator Health:

 
http://localhost:8080/actuator/health
 

Expected health response:

 json
{
  "status": "UP"
}
 

## API Test Example

Create a project:

 http
POST http://localhost:8080/api/projects
 

Request body:

 json
{
  "name": "Docker Compose Test Project",
  "description": "Testing Spring Boot and MySQL with Docker Compose"
}
 

Get projects:

 http
GET http://localhost:8080/api/projects
 

If the created project remains after running the following commands, the MySQL volume is working correctly:

 bash
docker compose down
docker compose up -d
 

---

# Docker Only

Docker Compose is recommended for normal local testing. This Docker-only option is useful when MySQL is already running on your local machine.

## Build Docker Image

 bash
docker build -t issue-tracker-api:latest .
 

## Run Docker Container with Local MySQL

When running the application inside a Docker container, use `host.docker.internal` instead of `localhost` to connect to MySQL running on your local machine.

 bash
docker run --name issue-tracker-api-container -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/issue_tracker?serverTimezone=Asia/Seoul&characterEncoding=UTF-8" \
  -e SPRING_DATASOURCE_USERNAME="root" \
  -e SPRING_DATASOURCE_PASSWORD="root" \
  issue-tracker-api:latest
 

One-line command:

 bash
docker run --name issue-tracker-api-container -p 8080:8080 -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/issue_tracker?serverTimezone=Asia/Seoul&characterEncoding=UTF-8" -e SPRING_DATASOURCE_USERNAME="root" -e SPRING_DATASOURCE_PASSWORD="root" issue-tracker-api:latest
 

## Docker Commands

Check running containers:

 bash
docker ps
 

Check all containers:

 bash
docker ps -a
 

Stop the container:

 bash
docker stop issue-tracker-api-container
 

Start the container again:

 bash
docker start issue-tracker-api-container
 

View logs:

 bash
docker logs -f issue-tracker-api-container
 

Remove the container:

 bash
docker rm issue-tracker-api-container
 

Force remove the container:

 bash
docker rm -f issue-tracker-api-container
 

---

# Docker Troubleshooting

## Port 3306 is Already in Use

If MySQL is already running on the local machine, Docker may fail with:

 
ports are not available: exposing port TCP 0.0.0.0:3306
 

This project uses local port `3307` for the MySQL container:

 yaml
ports:
  - "3307:3306"
 

The Spring Boot application still uses the Docker Compose service name and container port:

 
jdbc:mysql://mysql:3306/issue_tracker
 

This is because containers communicate through the Docker Compose network.

## Port 8080 is Already in Use

Check running containers:

 bash
docker ps
 

Remove an old standalone container if needed:

 bash
docker rm -f issue-tracker-api-container
 

Or stop Docker Compose containers:

 bash
docker compose down
 

## MySQL Connection Refused

This can happen if the Spring Boot application starts before MySQL is ready.

The project uses MySQL `healthcheck` and `depends_on` to wait until MySQL is healthy before starting the application.

## Access Denied for User Root

Check that the MySQL password and Spring datasource password match:

 yaml
MYSQL_ROOT_PASSWORD: root
SPRING_DATASOURCE_PASSWORD: root
 

If the MySQL volume was already created with a different password, reset the volume:

 bash
docker compose down -v
docker compose up --build
 

> Warning: this deletes the existing MySQL data.

## Unknown Database `issue_tracker`

Check that the compose file contains:

 yaml
MYSQL_DATABASE: issue_tracker
 

If the database was not created correctly during the first initialization, reset the volume:

 bash
docker compose down -v
docker compose up --build
 

> Warning: this deletes the existing MySQL data.

---

# Recommended Postman Test Flow

1. Create user
2. Create project
3. Create issue
4. Assign issue to user
5. Update issue status
6. Create comment
7. Get project statistics
8. Test issue search, filtering, pagination, and sorting
9. Test update APIs
10. Test delete APIs

## Test Flow Example

### 1. Create User

 http
POST /api/users
 

### 2. Create Project

 http
POST /api/projects
 

### 3. Create Issue

 http
POST /api/projects/{projectId}/issues
 

### 4. Assign Issue

 http
PATCH /api/issues/{issueId}/assignee
 

### 5. Update Issue Status

 http
PATCH /api/issues/{issueId}/status
 

### 6. Create Comment

 http
POST /api/issues/{issueId}/comments
 

### 7. Check Project Statistics

 http
GET /api/projects/{projectId}/stats
 

---

# Testing

This project includes both Service Layer Unit Tests and Controller Web Layer Tests.

## Service Layer Unit Tests

Service layer tests verify the business logic of each service implementation without starting the full Spring Boot application context.

Tested classes:

- `ProjectServiceImplTest`
- `UserServiceImplTest`
- `CommentServiceImplTest`
- `IssueServiceImplTest`

Main tools:

- JUnit 5
- Mockito
- `@ExtendWith(MockitoExtension.class)`
- Mocked repository dependencies

These tests focus on validating service logic such as creating, updating, deleting, finding resources, assigning users to issues, and handling not-found cases.

## Controller Web Layer Tests

Controller tests verify REST API request and response behavior using MockMvc.

Tested classes:

- `ProjectControllerTest`
- `UserControllerTest`
- `CommentControllerTest`
- `IssueControllerTest`

Main tools:

- JUnit 5
- Mockito
- MockMvc
- `@WebMvcTest`
- JSON response validation with `jsonPath`

These tests focus on verifying HTTP status codes, request mappings, validation behavior, and common API response structures using `ApiResponse.success(...)` and `ApiResponse.fail(...)`.

## Running Tests

Run all tests:

 bash
./mvnw test
 

On Windows:

 bash
mvnw.cmd test
 

Or using local Maven:

 bash
mvn test
 

---

# CI

This project uses GitHub Actions to run tests automatically.

Workflow:

 
.github/workflows/ci.yml
 

Main command:

 bash
mvn -B test
 

The CI workflow runs automatically on push or pull request based on the workflow configuration.

---

# Completed Features

- Project CRUD
- Issue CRUD
- Issue search and filtering
- Issue pagination and sorting
- Issue status update
- Issue priority enum
- Issue assignee assignment and unassignment
- Comment CRUD
- User CRUD
- Project issue statistics
- Common API response format
- Global exception handling
- Validation handling
- REST-style API URL structure
- Postman testing
- Service layer unit tests
- Controller web layer tests
- GitHub Actions CI
- Swagger / OpenAPI documentation
- Dockerfile
- Docker image build verification
- Docker container run verification
- Docker Compose setup with Spring Boot and MySQL
- MySQL volume persistence verification
- Docker Compose restart verification

---

# Future Improvements

## Authentication and Authorization

- Add Spring Security
- Implement JWT authentication
- Add user roles
- Add permission rules based on author or assignee

## DevOps

- Add Docker image build step to GitHub Actions
- Practice Kubernetes deployment
- Deploy to AWS or a low-cost server
- Separate development and production environment settings when needed
- Move secrets to environment variables or `.env` file for production-like usage

## Documentation

- Add ERD image
- Improve request and response examples
- Add deployment guide

## Feature Expansion

- Add issue author field
- Add comment author field
- Add issue history tracking
- Add file attachments
- Add due date notification
- Add project member management

---

# Project Goal

The goal of this project is to build a practical issue tracking REST API, starting from core CRUD features and gradually expanding into testing, Docker, CI/CD, and deployment.

This project is designed to demonstrate backend development skills using Java, Spring Boot, JPA, MySQL, REST API design, validation, exception handling, testing, API documentation, Docker, Docker Compose, and layered architecture.
