CREATE TABLE labels (
    id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_labels_project_name UNIQUE (project_id, name),
    CONSTRAINT fk_labels_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);

CREATE TABLE issue_labels (
    issue_id BIGINT NOT NULL,
    label_id BIGINT NOT NULL,
    PRIMARY KEY (issue_id, label_id),
    CONSTRAINT fk_issue_labels_issue FOREIGN KEY (issue_id) REFERENCES `issue` (id) ON DELETE CASCADE,
    CONSTRAINT fk_issue_labels_label FOREIGN KEY (label_id) REFERENCES labels (id) ON DELETE CASCADE
);

INSERT INTO labels (
    id,
    project_id,
    name,
    color,
    created_at
) VALUES
    (1, 1, 'bug', '#dc2626', CURRENT_TIMESTAMP(6)),
    (2, 1, 'backend', '#2563eb', CURRENT_TIMESTAMP(6)),
    (3, 1, 'docs', '#16a34a', CURRENT_TIMESTAMP(6));

INSERT INTO issue_labels (
    issue_id,
    label_id
) VALUES
    (1, 2),
    (2, 3),
    (3, 1);
