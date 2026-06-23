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
    ('Design issue detail editor', 'Add editable title, description, status, priority, and due date controls to the demo detail panel.', 'IN_PROGRESS', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 4 DAY), CURRENT_TIMESTAMP(6), 1, 2, 2),
    ('Add drag and drop status updates', 'Allow issue cards to move between board lanes with accessible fallback controls.', 'TODO', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 3 DAY), CURRENT_TIMESTAMP(6), 1, 3, 2),
    ('Expand portfolio demo dataset', 'Provide enough issues, comments, labels, and history to make the demo board feel realistic.', 'DONE', 'MEDIUM', CURRENT_DATE, DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 6 DAY), CURRENT_TIMESTAMP(6), 1, 2, 3),
    ('Add Redis cache observability', 'Track project cache hits, misses, memory usage, and eviction behavior.', 'TODO', 'MEDIUM', DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 2 DAY), CURRENT_TIMESTAMP(6), 1, 3, 2),
    ('Run 100-user k6 baseline', 'Record p95, p99, throughput, and error rate before and after Redis caching.', 'IN_PROGRESS', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 5 DAY), CURRENT_TIMESTAMP(6), 1, 2, 2),
    ('Prepare AWS deployment runbook', 'Document instance setup, secrets, HTTPS, health checks, rollback, and cost controls.', 'TODO', 'MEDIUM', DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY), DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 1 DAY), CURRENT_TIMESTAMP(6), 1, NULL, 2),
    ('Improve keyboard accessibility', 'Support keyboard selection, visible focus, and semantic labels across the issue board.', 'TODO', 'LOW', DATE_ADD(CURRENT_DATE, INTERVAL 8 DAY), DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 2 DAY), CURRENT_TIMESTAMP(6), 1, 3, 3),
    ('Configure CI container publishing', 'Publish versioned application images after tests and coverage checks pass.', 'IN_PROGRESS', 'MEDIUM', DATE_ADD(CURRENT_DATE, INTERVAL 12 DAY), DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 3 DAY), CURRENT_TIMESTAMP(6), 1, NULL, 2),
    ('Document cache invalidation strategy', 'Explain cache eviction on project updates and the fallback behavior when Redis is unavailable.', 'DONE', 'LOW', CURRENT_DATE, DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 7 DAY), CURRENT_TIMESTAMP(6), 1, 2, 3);

INSERT IGNORE INTO labels (
    project_id,
    name,
    color,
    created_at
) VALUES
    (1, 'frontend', '#7c3aed', CURRENT_TIMESTAMP(6)),
    (1, 'performance', '#ea580c', CURRENT_TIMESTAMP(6)),
    (1, 'devops', '#0891b2', CURRENT_TIMESTAMP(6));

INSERT IGNORE INTO issue_labels (issue_id, label_id)
SELECT issue.id, label.id
FROM `issue` issue
JOIN labels label ON label.project_id = issue.project_id
WHERE issue.project_id = 1
  AND (
      (issue.title = 'Design issue detail editor' AND label.name IN ('frontend', 'bug'))
      OR (issue.title = 'Add drag and drop status updates' AND label.name = 'frontend')
      OR (issue.title = 'Expand portfolio demo dataset' AND label.name IN ('frontend', 'docs'))
      OR (issue.title = 'Add Redis cache observability' AND label.name IN ('backend', 'performance'))
      OR (issue.title = 'Run 100-user k6 baseline' AND label.name = 'performance')
      OR (issue.title = 'Prepare AWS deployment runbook' AND label.name IN ('devops', 'docs'))
      OR (issue.title = 'Improve keyboard accessibility' AND label.name = 'frontend')
      OR (issue.title = 'Configure CI container publishing' AND label.name = 'devops')
      OR (issue.title = 'Document cache invalidation strategy' AND label.name IN ('backend', 'docs'))
  );

INSERT INTO `comment` (
    content,
    issue_id,
    created_at,
    updated_at,
    author_id
) VALUES
    ('Keep authorization checks outside the cached method.', (SELECT id FROM `issue` WHERE project_id = 1 AND title = 'Design issue detail editor' ORDER BY id DESC LIMIT 1), DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 2 DAY), CURRENT_TIMESTAMP(6), 2),
    ('The full card should be clickable, not only the small detail button.', (SELECT id FROM `issue` WHERE project_id = 1 AND title = 'Design issue detail editor' ORDER BY id DESC LIMIT 1), DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 1 DAY), CURRENT_TIMESTAMP(6), 3),
    ('Add keyboard controls as a fallback for drag and drop.', (SELECT id FROM `issue` WHERE project_id = 1 AND title = 'Add drag and drop status updates' ORDER BY id DESC LIMIT 1), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 3),
    ('Seed data now covers every board lane and priority.', (SELECT id FROM `issue` WHERE project_id = 1 AND title = 'Expand portfolio demo dataset' ORDER BY id DESC LIMIT 1), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 2),
    ('Capture Redis INFO stats after the load test.', (SELECT id FROM `issue` WHERE project_id = 1 AND title = 'Add Redis cache observability' ORDER BY id DESC LIMIT 1), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 2),
    ('The 100 VU Redis run completed with zero project request failures.', (SELECT id FROM `issue` WHERE project_id = 1 AND title = 'Run 100-user k6 baseline' ORDER BY id DESC LIMIT 1), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 2),
    ('Prefer GHCR first and move to ECR only when AWS-specific deployment is needed.', (SELECT id FROM `issue` WHERE project_id = 1 AND title = 'Configure CI container publishing' ORDER BY id DESC LIMIT 1), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 3),
    ('Use visible focus styles on every selectable issue card.', (SELECT id FROM `issue` WHERE project_id = 1 AND title = 'Improve keyboard accessibility' ORDER BY id DESC LIMIT 1), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 3),
    ('Keep the first deployment small and document the upgrade path.', (SELECT id FROM `issue` WHERE project_id = 1 AND title = 'Prepare AWS deployment runbook' ORDER BY id DESC LIMIT 1), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 2),
    ('A five-minute TTL limits stale data even if an eviction is missed.', (SELECT id FROM `issue` WHERE project_id = 1 AND title = 'Document cache invalidation strategy' ORDER BY id DESC LIMIT 1), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 2);

INSERT INTO issue_histories (
    issue_id,
    actor_id,
    action,
    field_name,
    before_value,
    after_value,
    created_at
)
SELECT id, 2, 'STATUS_CHANGED', 'status', 'TODO', 'IN_PROGRESS', DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 2 DAY)
FROM `issue`
WHERE project_id = 1 AND title = 'Design issue detail editor'
UNION ALL
SELECT id, 2, 'STATUS_CHANGED', 'status', 'TODO', 'IN_PROGRESS', DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 1 DAY)
FROM `issue`
WHERE project_id = 1 AND title = 'Run 100-user k6 baseline'
UNION ALL
SELECT id, 3, 'STATUS_CHANGED', 'status', 'IN_PROGRESS', 'DONE', DATE_SUB(CURRENT_TIMESTAMP(6), INTERVAL 1 DAY)
FROM `issue`
WHERE project_id = 1 AND title = 'Expand portfolio demo dataset'
UNION ALL
SELECT id, 3, 'STATUS_CHANGED', 'status', 'IN_PROGRESS', 'DONE', CURRENT_TIMESTAMP(6)
FROM `issue`
WHERE project_id = 1 AND title = 'Document cache invalidation strategy';
