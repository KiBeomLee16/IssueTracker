# ERD

This document describes the current database structure of the Issue Tracker API.

## Entity Relationship Diagram

```mermaid
erDiagram
    USERS ||--o{ PROJECT_MEMBERS : joins
    PROJECTS ||--o{ PROJECT_MEMBERS : has
    PROJECTS ||--o{ ISSUES : contains
    USERS ||--o{ ISSUES : authors
    USERS ||--o{ ISSUES : assigned_to
    ISSUES ||--o{ COMMENTS : has
    PROJECTS ||--o{ LABELS : defines
    ISSUES }o--o{ LABELS : tagged_with
    USERS ||--o{ COMMENTS : authors

    USERS {
        BIGINT id PK
        VARCHAR user_id UK
        VARCHAR password
        VARCHAR name
        VARCHAR email UK
        VARCHAR role
        DATETIME created_at
        DATETIME updated_at
    }

    PROJECTS {
        BIGINT id PK
        VARCHAR name
        VARCHAR description
        VARCHAR status
        DATETIME created_at
        DATETIME updated_at
    }

    PROJECT_MEMBERS {
        BIGINT id PK
        BIGINT project_id FK
        BIGINT user_id FK
        VARCHAR role
        DATETIME created_at
    }

    ISSUES {
        BIGINT id PK
        VARCHAR title
        VARCHAR description
        VARCHAR status
        VARCHAR priority
        DATE due_date
        BIGINT project_id FK
        BIGINT assignee_id FK
        BIGINT author_id FK
        DATETIME created_at
        DATETIME updated_at
    }

    COMMENTS {
        BIGINT id PK
        VARCHAR content
        BIGINT issue_id FK
        BIGINT author_id FK
        DATETIME created_at
        DATETIME updated_at
    }

    LABELS {
        BIGINT id PK
        BIGINT project_id FK
        VARCHAR name
        VARCHAR color
        DATETIME created_at
    }
```

## Relationships

### User - Project Member

A user can join multiple projects through `project_members`.

```text
USERS 1 : N PROJECT_MEMBERS
```

### Project - Project Member

A project can have multiple members. A project member has a project-level role: `OWNER` or `MEMBER`.

```text
PROJECTS 1 : N PROJECT_MEMBERS
```

### Project - Issue

A project can contain multiple issues.

```text
PROJECTS 1 : N ISSUES
```

### User - Issue Author

A user can author multiple issues. `ISSUES.author_id` is required.

```text
USERS 1 : N ISSUES
```

### User - Issue Assignee

A user can be assigned to multiple issues. `ISSUES.assignee_id` is nullable.

```text
USERS 1 : N ISSUES
```

### Issue - Comment

An issue can have multiple comments.

```text
ISSUES 1 : N COMMENTS
```

### Project - Label

A project can define multiple labels. Label names are unique within a project.

```text
PROJECTS 1 : N LABELS
```

### Issue - Label

An issue can have multiple labels, and a label can be attached to multiple issues through `issue_labels`.

```text
ISSUES N : M LABELS
```

### User - Comment Author

A user can author multiple comments. `COMMENTS.author_id` is required.

```text
USERS 1 : N COMMENTS
```

## Constraints

- `USERS.user_id` is unique.
- `USERS.email` is unique.
- `PROJECT_MEMBERS(project_id, user_id)` is unique.
- `ISSUES.project_id` references `PROJECTS.id`.
- `ISSUES.assignee_id` references `USERS.id` and is nullable.
- `ISSUES.author_id` references `USERS.id` and is required.
- `COMMENTS.issue_id` references `ISSUES.id`.
- `COMMENTS.author_id` references `USERS.id` and is required.
- `PROJECT_MEMBERS.project_id` references `PROJECTS.id`.
- `PROJECT_MEMBERS.user_id` references `USERS.id`.
- `LABELS.project_id` references `PROJECTS.id`.
- `LABELS(project_id, name)` is unique.
- `ISSUE_LABELS(issue_id, label_id)` is the primary key.
- `ISSUE_LABELS.issue_id` references `ISSUES.id`.
- `ISSUE_LABELS.label_id` references `LABELS.id`.

## Notes

- `USERS.role` is used for global role-based authorization: `USER` or `ADMIN`.
- `PROJECT_MEMBERS.role` is used for project-level authorization: `OWNER` or `MEMBER`.
- `ISSUES.status` is used for issue workflow management.
- `ISSUES.priority` is used to manage issue priority.
- `ISSUES.due_date` is used to manage the issue deadline.
- `LABELS.color` stores a hex color value for API clients.
