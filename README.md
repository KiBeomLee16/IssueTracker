# Issue Tracker REST API

A Java + Spring Boot based REST API for managing projects, issues, comments, and users.

This project was built as a personal backend portfolio project.  
It currently focuses on REST API development and will later be expanded with testing, Docker, CI/CD, Kubernetes, and cloud or low-cost server deployment.

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
| Testing Planned | JUnit, Mockito |
| DevOps Planned | Docker, GitHub Actions or Jenkins, Kubernetes |

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
- Exception handling structure
- Entity relationships
- REST-style URL design

---

## Entity Relationships

 
Project 1 : N Issue

Issue N : 1 Project
Issue N : 1 User     // assignee

Issue 1 : N Comment
 

### Project

A project can have multiple issues.

### Issue

An issue belongs to one project.  
An issue can also have one assigned user.

### Comment

An issue can have multiple comments.

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

## Project API

| Method | URL | Description |
|---|---|---|
| POST | `/api/projects` | Create a project |
| GET | `/api/projects` | Get all projects |
| GET | `/api/projects/{projectId}` | Get a project by ID |
| PUT | `/api/projects/{projectId}` | Update a project |
| DELETE | `/api/projects/{projectId}` | Delete a project |
| GET | `/api/projects/{projectId}/stats` | Get project issue statistics |

---

### Create Project

 http
POST /api/projects
 

Request Body:

 json
{
  "name": "Issue Tracker",
  "description": "Issue tracker REST API project"
}
 

Response:

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
 

---

### Get All Projects

 http
GET /api/projects
 

---

### Get Project by ID

 http
GET /api/projects/{projectId}
 

---

### Update Project

 http
PUT /api/projects/{projectId}
 

Request Body:

 json
{
  "name": "Issue Tracker Updated",
  "description": "Updated project description"
}
 

---

### Delete Project

 http
DELETE /api/projects/{projectId}
 

---

### Get Project Statistics

 http
GET /api/projects/{projectId}/stats
 

Response Example:

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

---

### Create Issue

 http
POST /api/projects/{projectId}/issues
 

Request Body:

 json
{
  "title": "Login API bug",
  "description": "Login API returns 500 error",
  "priority": "HIGH",
  "dueDate": "2026-06-30"
}
 

---

### Get Issues by Project

 http
GET /api/projects/{projectId}/issues
 

---

### Get Issue by ID

 http
GET /api/issues/{issueId}
 

---

### Update Issue

 http
PUT /api/issues/{issueId}
 

Request Body:

 json
{
  "title": "Login API bug updated",
  "description": "Updated issue description",
  "priority": "MEDIUM",
  "dueDate": "2026-07-10"
}
 

---

### Delete Issue

 http
DELETE /api/issues/{issueId}
 

---

### Search, Filter, Paginate, and Sort Issues

 http
GET /api/projects/{projectId}/issues/page
 

Query Parameter Example:

 http
GET /api/projects/1/issues/page?page=0&size=10&sortBy=id&direction=desc
 

Filter Example:

 http
GET /api/projects/1/issues/page?status=TODO&priority=HIGH&page=0&size=10&sortBy=id&direction=desc
 

---

### Update Issue Status

 http
PATCH /api/issues/{issueId}/status
 

Request Body:

 json
{
  "status": "IN_PROGRESS"
}
 

Available status values:

 
TODO
IN_PROGRESS
DONE
 

---

### Assign Issue to User

 http
PATCH /api/issues/{issueId}/assignee
 

Request Body:

 json
{
  "userId": 1
}
 

---

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

---

### Create Comment

 http
POST /api/issues/{issueId}/comments
 

Request Body:

 json
{
  "content": "This issue needs to be fixed first."
}
 

---

### Get Comments by Issue

 http
GET /api/issues/{issueId}/comments
 

---

### Get Comment by ID

 http
GET /api/comments/{commentId}
 

---

### Update Comment

 http
PUT /api/comments/{commentId}
 

Request Body:

 json
{
  "content": "Updated comment content."
}
 

---

### Delete Comment

 http
DELETE /api/comments/{commentId}
 

---

## User API

| Method | URL | Description |
|---|---|---|
| POST | `/api/users` | Create a user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{userId}` | Get a user by ID |
| PUT | `/api/users/{userId}` | Update a user |
| DELETE | `/api/users/{userId}` | Delete a user |

---

### Create User

 http
POST /api/users
 

Request Body:

 json
{
  "name": "John Doe",
  "email": "john@example.com"
}
 

---

### Get All Users

 http
GET /api/users
 

---

### Get User by ID

 http
GET /api/users/{userId}
 

---

### Update User

 http
PUT /api/users/{userId}
 

Request Body:

 json
{
  "name": "John Updated",
  "email": "john.updated@example.com"
}
 

---

### Delete User

 http
DELETE /api/users/{userId}
 

---

# How to Run

## 1. Clone the Repository

 bash
git clone https://github.com/your-username/issue-tracker-api.git
cd issue-tracker-api
 

---

## 2. Create MySQL Database

 sql
CREATE DATABASE issue_tracker;
 

---

## 3. Configure Database Settings

Example `application.properties`:

 properties
spring.application.name=issue-tracker-api

spring.datasource.url=jdbc:mysql://localhost:3306/issue_tracker
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
 

Update `username`, `password`, and database name based on your local MySQL environment.

---

## 4. Run the Application

Using Maven Wrapper:

 bash
./mvnw spring-boot:run
 

On Windows:

 bash
mvnw.cmd spring-boot:run
 

Or run the main class from your IDE:

 
IssueTrackerApplication.java
 

---

## 5. Check Server Status

Base URL:

 http
http://localhost:8080
 

Actuator health check:

 http
GET /actuator/health
 

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
 

---

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

# Completed Features

- Project CRUD
- Issue CRUD
- Issue search and filtering
- Issue pagination and sorting
- Issue status update
- Issue assignee assignment and unassignment
- Comment CRUD
- User CRUD
- Project issue statistics
- Common API response format
- Validation
- REST-style API URL structure
- Postman testing

---

# Future Improvements

## Testing

- Add JUnit unit tests
- Add Mockito-based service tests
- Add controller tests
- Add repository tests

## Authentication and Authorization

- Add Spring Security
- Implement JWT authentication
- Add user roles
- Add permission rules based on author or assignee

## DevOps

- Add Dockerfile
- Add docker-compose with MySQL
- Add CI/CD using GitHub Actions or Jenkins
- Practice Kubernetes deployment
- Deploy to AWS or a low-cost server

## Documentation

- Add Swagger / OpenAPI documentation
- Organize Postman Collection
- Add ERD image
- Improve request and response examples

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

This project is designed to demonstrate backend development skills using Java, Spring Boot, JPA, MySQL, REST API design, validation, exception handling, and layered architecture.
