INSERT INTO projects (
    name,
    description,
    status,
    created_at,
    updated_at
) VALUES (
    'Mobile Release Workspace',
    'Second demo project for release planning and project-scoped assignment permissions.',
    'ACTIVE',
    CURRENT_TIMESTAMP(6),
    CURRENT_TIMESTAMP(6)
);

INSERT INTO project_members (
    project_id,
    user_id,
    role,
    created_at
) VALUES
    ((SELECT id FROM projects WHERE name = 'Mobile Release Workspace' ORDER BY id DESC LIMIT 1),
     (SELECT id FROM users WHERE user_id = 'member01'), 'OWNER', CURRENT_TIMESTAMP(6)),
    ((SELECT id FROM projects WHERE name = 'Mobile Release Workspace' ORDER BY id DESC LIMIT 1),
     (SELECT id FROM users WHERE user_id = 'owner01'), 'MEMBER', CURRENT_TIMESTAMP(6));

INSERT INTO `issue` (
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
    ('Triage Android startup crash', 'Reproduce and prioritize the startup crash reported on Android 15 devices.',
     'TODO', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6),
     (SELECT id FROM projects WHERE name = 'Mobile Release Workspace' ORDER BY id DESC LIMIT 1),
     (SELECT id FROM users WHERE user_id = 'member01'), (SELECT id FROM users WHERE user_id = 'member01')),
    ('Prepare app store metadata', 'Finalize release notes, screenshots, privacy links, and localized descriptions.',
     'IN_PROGRESS', 'MEDIUM', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6),
     (SELECT id FROM projects WHERE name = 'Mobile Release Workspace' ORDER BY id DESC LIMIT 1),
     (SELECT id FROM users WHERE user_id = 'owner01'), (SELECT id FROM users WHERE user_id = 'member01')),
    ('Complete release candidate QA', 'Run the regression checklist against the signed release candidate build.',
     'DONE', 'HIGH', CURRENT_DATE, DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 3 DAY), CURRENT_TIMESTAMP(6),
     (SELECT id FROM projects WHERE name = 'Mobile Release Workspace' ORDER BY id DESC LIMIT 1),
     (SELECT id FROM users WHERE user_id = 'owner01'), (SELECT id FROM users WHERE user_id = 'owner01')),
    ('Monitor launch metrics', 'Create dashboards for crash-free sessions, latency, and conversion after launch.',
     'TODO', 'MEDIUM', DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6),
     (SELECT id FROM projects WHERE name = 'Mobile Release Workspace' ORDER BY id DESC LIMIT 1),
     NULL, (SELECT id FROM users WHERE user_id = 'member01'));

INSERT INTO labels (
    project_id,
    name,
    color,
    created_at
) VALUES
    ((SELECT id FROM projects WHERE name = 'Mobile Release Workspace' ORDER BY id DESC LIMIT 1),
     'mobile', '#2563eb', CURRENT_TIMESTAMP(6)),
    ((SELECT id FROM projects WHERE name = 'Mobile Release Workspace' ORDER BY id DESC LIMIT 1),
     'release', '#16a34a', CURRENT_TIMESTAMP(6));

INSERT INTO issue_labels (issue_id, label_id)
SELECT issue.id, label.id
FROM `issue` issue
JOIN labels label ON label.project_id = issue.project_id
JOIN projects project ON project.id = issue.project_id
WHERE project.name = 'Mobile Release Workspace'
  AND (
      (issue.title = 'Triage Android startup crash' AND label.name = 'mobile')
      OR (issue.title = 'Prepare app store metadata' AND label.name = 'release')
      OR (issue.title = 'Complete release candidate QA' AND label.name IN ('mobile', 'release'))
      OR (issue.title = 'Monitor launch metrics' AND label.name = 'release')
  );

INSERT INTO `comment` (
    content,
    issue_id,
    created_at,
    updated_at,
    author_id
) VALUES
    ('The crash only reproduces after restoring a previous session.',
     (SELECT id FROM `issue` WHERE title = 'Triage Android startup crash' ORDER BY id DESC LIMIT 1),
     CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), (SELECT id FROM users WHERE user_id = 'owner01')),
    ('Korean and English release notes are ready for review.',
     (SELECT id FROM `issue` WHERE title = 'Prepare app store metadata' ORDER BY id DESC LIMIT 1),
     CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), (SELECT id FROM users WHERE user_id = 'member01')),
    ('Regression completed with no release-blocking failures.',
     (SELECT id FROM `issue` WHERE title = 'Complete release candidate QA' ORDER BY id DESC LIMIT 1),
     CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), (SELECT id FROM users WHERE user_id = 'owner01')),
    ('Alert thresholds should be reviewed after the first 24 hours.',
     (SELECT id FROM `issue` WHERE title = 'Monitor launch metrics' ORDER BY id DESC LIMIT 1),
     CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), (SELECT id FROM users WHERE user_id = 'member01'));

INSERT INTO issue_histories (
    issue_id,
    actor_id,
    action,
    field_name,
    before_value,
    after_value,
    created_at
)
SELECT issue.id, (SELECT id FROM users WHERE user_id = 'member01'), 'STATUS_CHANGED', 'status', 'TODO', 'IN_PROGRESS',
       DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 1 DAY)
FROM `issue` issue
WHERE issue.title = 'Prepare app store metadata'
UNION ALL
SELECT issue.id, (SELECT id FROM users WHERE user_id = 'owner01'), 'STATUS_CHANGED', 'status', 'IN_PROGRESS', 'DONE',
       CURRENT_TIMESTAMP(6)
FROM `issue` issue
WHERE issue.title = 'Complete release candidate QA';
