package com.example.container;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.issuetracker.IssueTrackerApiApplication;

@Testcontainers
@SpringBootTest(classes = IssueTrackerApiApplication.class)
class FlywayMigrationIntegrationTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withDatabaseName("issue_tracker")
			.withUsername("test").withPassword("test");

	@DynamicPropertySource
	static void configureDatasource(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
		registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
	}

	@Autowired
	DataSource dataSource;

	@Test
	void flywayMigrationsCreateTablesAndSeedData() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);

		Integer migrationCount = jdbcTemplate
				.queryForObject("SELECT COUNT(*) FROM flyway_schema_history WHERE success = 1", Integer.class);

		Integer refreshTokenTableCount = jdbcTemplate.queryForObject("""
				SELECT COUNT(*)
				FROM information_schema.tables
				WHERE table_schema = DATABASE()
				  AND table_name = 'refresh_tokens'
				""", Integer.class);

		Integer issueHistoryTableCount = jdbcTemplate.queryForObject("""
				SELECT COUNT(*)
				FROM information_schema.tables
				WHERE table_schema = DATABASE()
				  AND table_name = 'issue_histories'
				""", Integer.class);

		Integer labelTableCount = jdbcTemplate.queryForObject("""
				SELECT COUNT(*)
				FROM information_schema.tables
				WHERE table_schema = DATABASE()
				  AND table_name = 'labels'
				""", Integer.class);

		Integer issueLabelTableCount = jdbcTemplate.queryForObject("""
				SELECT COUNT(*)
				FROM information_schema.tables
				WHERE table_schema = DATABASE()
				  AND table_name = 'issue_labels'
				""", Integer.class);

		Integer labelCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM labels", Integer.class);
		Integer projectCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM projects", Integer.class);
		Integer projectMemberCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM project_members", Integer.class);
		Integer issueCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `issue`", Integer.class);
		Integer issueHistoryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM issue_histories", Integer.class);

		String commentIssueDeleteRule = jdbcTemplate.queryForObject("""
				SELECT delete_rule
				FROM information_schema.referential_constraints
				WHERE constraint_schema = DATABASE()
				  AND constraint_name = 'fk_comment_issue'
				""", String.class);

		String issueProjectDeleteRule = jdbcTemplate.queryForObject("""
				SELECT delete_rule
				FROM information_schema.referential_constraints
				WHERE constraint_schema = DATABASE()
				  AND constraint_name = 'fk_issue_project'
				""", String.class);

		String projectMemberProjectDeleteRule = jdbcTemplate.queryForObject("""
				SELECT delete_rule
				FROM information_schema.referential_constraints
				WHERE constraint_schema = DATABASE()
				  AND constraint_name = 'fk_project_member_project'
				""", String.class);

		assertThat(userCount).isEqualTo(3);
		assertThat(migrationCount).isEqualTo(9);
		assertThat(refreshTokenTableCount).isEqualTo(1);
		assertThat(issueHistoryTableCount).isEqualTo(1);
		assertThat(labelTableCount).isEqualTo(1);
		assertThat(issueLabelTableCount).isEqualTo(1);
		assertThat(projectCount).isEqualTo(2);
		assertThat(projectMemberCount).isEqualTo(4);
		assertThat(labelCount).isEqualTo(8);
		assertThat(issueCount).isEqualTo(16);
		assertThat(issueHistoryCount).isEqualTo(6);
		assertThat(commentIssueDeleteRule).isEqualTo("CASCADE");
		assertThat(issueProjectDeleteRule).isEqualTo("CASCADE");
		assertThat(projectMemberProjectDeleteRule).isEqualTo("CASCADE");
	}
}
