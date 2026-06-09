ALTER TABLE `comment`
    DROP FOREIGN KEY fk_comment_issue;

ALTER TABLE `comment`
    ADD CONSTRAINT fk_comment_issue
    FOREIGN KEY (issue_id) REFERENCES `issue` (id)
    ON DELETE CASCADE;

ALTER TABLE `issue`
    DROP FOREIGN KEY fk_issue_project;

ALTER TABLE `issue`
    ADD CONSTRAINT fk_issue_project
    FOREIGN KEY (project_id) REFERENCES projects (id)
    ON DELETE CASCADE;

ALTER TABLE project_members
    DROP FOREIGN KEY fk_project_member_project;

ALTER TABLE project_members
    ADD CONSTRAINT fk_project_member_project
    FOREIGN KEY (project_id) REFERENCES projects (id)
    ON DELETE CASCADE;
