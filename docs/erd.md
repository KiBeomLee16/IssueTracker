# ERD

This document describes the database structure of the Issue Tracker API.

## Entity Relationship Diagram

```mermaid
erDiagram
    USERS ||--o{ ISSUES : assigned_to
    PROJECTS ||--o{ ISSUES : contains
    ISSUES ||--o{ COMMENTS : has

    USERS {
        BIGINT id PK
        VARCHAR user_id
        VARCHAR password
        VARCHAR name
        VARCHAR email
        VARCHAR role
        DATETIME created_at
        DATETIME updated_at
    }

    PROJECTS {
        BIGINT id PK
        VARCHAR name
        TEXT description
        DATETIME created_at
        DATETIME updated_at
    }

    ISSUES {
        BIGINT id PK
        VARCHAR title
        TEXT description
        VARCHAR status
        VARCHAR priority
        DATE due_date
        BIGINT project_id FK
        BIGINT assignee_id FK
        DATETIME created_at
        DATETIME updated_at
    }

    COMMENTS {
        BIGINT id PK
        TEXT content
        BIGINT issue_id FK
        DATETIME created_at
        DATETIME updated_at
    }
```

## Relationships

### User - Issue

A user can be assigned to multiple issues.

```text
USERS 1 : N ISSUES
```

### Project - Issue

A project can contain multiple issues.

```text
PROJECTS 1 : N ISSUES
```

### Issue - Comment

An issue can have multiple comments.

```text
ISSUES 1 : N COMMENTS
```

## Notes

- `USERS.role` is used for role-based authorization.
- `ISSUES.status` is used for issue workflow management.
- `ISSUES.priority` is used to manage issue priority.
- `ISSUES.due_date` is used to manage the issue deadline.
- `ISSUES.assignee_id` is nullable when an issue is not assigned to any user.
- Author relationships for issues or comments are not included because they are listed as future improvements, not current completed features.