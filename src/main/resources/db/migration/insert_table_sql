CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_user_id UNIQUE (user_id)
);

CREATE TABLE projects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id)
);

CREATE TABLE `issue` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(255) NOT NULL,
    priority VARCHAR(255) NOT NULL,
    due_date DATE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    project_id BIGINT NOT NULL,
    assignee_id BIGINT,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_issue_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_issue_assignee FOREIGN KEY (assignee_id) REFERENCES users (id),
    CONSTRAINT fk_issue_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE `comment` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content VARCHAR(1000) NOT NULL,
    issue_id BIGINT NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    author_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_comment_issue FOREIGN KEY (issue_id) REFERENCES `issue` (id),
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE project_members (
    id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_project_member_project_user UNIQUE (project_id, user_id),
    CONSTRAINT fk_project_member_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_project_member_user FOREIGN KEY (user_id) REFERENCES users (id)
);
