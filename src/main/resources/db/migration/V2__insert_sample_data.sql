INSERT INTO users (
    id,
    password,
    name,
    email,
    user_id,
    created_at,
    updated_at,
    role
) VALUES
    (1, '$2a$10$A6BH1p2YMzwI.31IIIA3GO2NmGbjUwoTPNZCbXG8844JO/hdCnMpK', 'Admin User', 'admin@example.com', 'admin01', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 'ADMIN'),
    (2, '$2a$10$A6BH1p2YMzwI.31IIIA3GO2NmGbjUwoTPNZCbXG8844JO/hdCnMpK', 'Project Owner', 'owner@example.com', 'owner01', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 'USER'),
    (3, '$2a$10$A6BH1p2YMzwI.31IIIA3GO2NmGbjUwoTPNZCbXG8844JO/hdCnMpK', 'Project Member', 'member@example.com', 'member01', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 'USER');

INSERT INTO projects (
    id,
    name,
    description,
    status,
    created_at,
    updated_at
) VALUES
    (1, 'Portfolio Issue Tracker', 'Sample project for API demonstration.', 'ACTIVE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO project_members (
    id,
    project_id,
    user_id,
    role,
    created_at
) VALUES
    (1, 1, 2, 'OWNER', CURRENT_TIMESTAMP(6)),
    (2, 1, 3, 'MEMBER', CURRENT_TIMESTAMP(6));

INSERT INTO `issue` (
    id,
    title,
    description,
    status,
    priority,
    due_date,
    created_at,
    updated_at,
    project_id,
    assignee_id,
    author_id
) VALUES
    (1, 'Set up production environment', 'Prepare Docker Compose production profile and environment variables.', 'TODO', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 1, 3, 2),
    (2, 'Write API test scenarios', 'Document signup, login, project, issue, and comment API scenarios.', 'IN_PROGRESS', 'MEDIUM', DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 1, 2, 3),
    (3, 'Review authorization policy', 'Confirm project owner and member authorization rules.', 'DONE', 'LOW', CURRENT_DATE, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 1, 2, 2);

INSERT INTO `comment` (
    id,
    content,
    issue_id,
    created_at,
    updated_at,
    author_id
) VALUES
    (1, 'Production profile should use DDL_AUTO=validate.', 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 2),
    (2, 'Add Swagger examples after deployment URL is ready.', 2, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 3),
    (3, 'Authorization policy has been reviewed for project members.', 3, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 2);
