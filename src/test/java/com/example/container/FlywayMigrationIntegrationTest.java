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

		assertThat(userCount).isEqualTo(3);
		assertThat(migrationCount).isEqualTo(6);
		assertThat(refreshTokenTableCount).isEqualTo(1);
		assertThat(issueHistoryTableCount).isEqualTo(1);
		assertThat(labelTableCount).isEqualTo(1);
		assertThat(issueLabelTableCount).isEqualTo(1);
		assertThat(labelCount).isEqualTo(3);
	}
}
