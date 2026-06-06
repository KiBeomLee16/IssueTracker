CREATE TABLE issue_histories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    issue_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    field_name VARCHAR(50) NOT NULL,
    before_value VARCHAR(2000),
    after_value VARCHAR(2000),
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_issue_histories_issue FOREIGN KEY (issue_id) REFERENCES `issue` (id) ON DELETE CASCADE,
    CONSTRAINT fk_issue_histories_actor FOREIGN KEY (actor_id) REFERENCES users (id)
);

CREATE INDEX idx_issue_histories_issue_created_at ON issue_histories (issue_id, created_at);
