package com.example.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.issuetracker.IssueTrackerApiApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Testcontainers
@SpringBootTest(classes = IssueTrackerApiApplication.class)
@AutoConfigureMockMvc
public class IssueTrackerApiIntegrationTest {

	private static final String PASSWORD = "password123!";

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
			.withDatabaseName("issue_tracker")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void configureDatasource(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
		registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
		registry.add("jwt.secret", () -> "test-secret-key-test-secret-key-test-secret-key");
		registry.add("jwt.expiration-ms", () -> "3600000");
		registry.add("jwt.refresh-expiration-ms", () -> "1209600000");
		registry.add("jwt.refresh-cleanup-interval-ms", () -> "3600000");
		registry.add("app.admin.bootstrap.enabled", () -> "false");
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void ownerMemberIssueWorkflow_success() throws Exception {
		String suffix = uniqueSuffix();
		String ownerUserId = "it_owner_" + suffix;
		String memberUserId = "it_member_" + suffix;

		Long ownerId = signup("Integration Owner", "owner_" + suffix + "@example.com", ownerUserId);
		Long memberId = signup("Integration Member", "member_" + suffix + "@example.com", memberUserId);

		assertThat(ownerId).isNotNull();
		assertThat(memberId).isNotNull();

		String ownerToken = login(ownerUserId);
		String memberToken = login(memberUserId);

		Long projectId = createProject(ownerToken, "Integration Project " + suffix);
		addProjectMember(ownerToken, projectId, memberId);

		Long labelId = createLabel(ownerToken, projectId, "bug-" + suffix, "#ef4444");
		Long issueId = createIssue(ownerToken, projectId, "Integration issue " + suffix);
		updateIssueLabels(ownerToken, issueId, labelId);

		createComment(memberToken, issueId, "I can comment because I am a project member.");

		mockMvc.perform(patch("/api/issues/{issueId}/status", issueId)
				.header(HttpHeaders.AUTHORIZATION, bearer(memberToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "status": "IN_PROGRESS"
						}
						"""))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.success").value(false));

		assignIssue(ownerToken, issueId, memberId);

		mockMvc.perform(patch("/api/issues/{issueId}/status", issueId)
				.header(HttpHeaders.AUTHORIZATION, bearer(memberToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "status": "IN_PROGRESS"
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
				.andExpect(jsonPath("$.data.assignee.id").value(memberId));

		mockMvc.perform(get("/api/issues/{issueId}/histories", issueId)
				.header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].action").value("STATUS_CHANGED"))
				.andExpect(jsonPath("$.data[0].fieldName").value("status"))
				.andExpect(jsonPath("$.data[0].afterValue").value("IN_PROGRESS"))
				.andExpect(jsonPath("$.data[1].action").value("ASSIGNEE_CHANGED"))
				.andExpect(jsonPath("$.data[1].fieldName").value("assignee"));

		mockMvc.perform(get("/api/projects/{projectId}/stats", projectId)
				.header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.totalIssues").value(1))
				.andExpect(jsonPath("$.data.inProgressCount").value(1))
				.andExpect(jsonPath("$.data.totalComments").value(1))
				.andExpect(jsonPath("$.data.totalMembers").value(2))
				.andExpect(jsonPath("$.data.assignedIssueCount").value(1));
	}

	@Test
	void nonMemberCannotReadProjectOrIssue() throws Exception {
		String suffix = uniqueSuffix();
		String ownerUserId = "owner_forbidden_" + suffix;
		String outsiderUserId = "outsider_" + suffix;

		signup("Forbidden Owner", "forbidden_owner_" + suffix + "@example.com", ownerUserId);
		signup("Outsider", "outsider_" + suffix + "@example.com", outsiderUserId);

		String ownerToken = login(ownerUserId);
		String outsiderToken = login(outsiderUserId);

		Long projectId = createProject(ownerToken, "Forbidden Project " + suffix);
		Long issueId = createIssue(ownerToken, projectId, "Forbidden issue " + suffix);

		mockMvc.perform(get("/api/projects/{projectId}", projectId)
				.header(HttpHeaders.AUTHORIZATION, bearer(outsiderToken)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.success").value(false));

		mockMvc.perform(get("/api/issues/{issueId}", issueId)
				.header(HttpHeaders.AUTHORIZATION, bearer(outsiderToken)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.success").value(false));
	}

	private Long signup(String name, String email, String userId) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "name": "%s",
						  "email": "%s",
						  "userId": "%s",
						  "password": "%s"
						}
						""".formatted(name, email, userId, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andReturn();

		return json(result).at("/data/id").asLong();
	}

	private String login(String userId) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "userId": "%s",
						  "password": "%s"
						}
						""".formatted(userId, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andReturn();

		return json(result).at("/data/accessToken").asText();
	}

	private Long createProject(String token, String projectName) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/projects")
				.header(HttpHeaders.AUTHORIZATION, bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "name": "%s",
						  "description": "Created by integration test",
						  "status": "ACTIVE"
						}
						""".formatted(projectName)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andReturn();

		return json(result).at("/data/id").asLong();
	}

	private void addProjectMember(String token, Long projectId, Long userId) throws Exception {
		mockMvc.perform(post("/api/projects/{projectId}/members", projectId)
				.header(HttpHeaders.AUTHORIZATION, bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "userId": %d
						}
						""".formatted(userId)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.user.id").value(userId));
	}

	private Long createLabel(String token, Long projectId, String name, String color) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/projects/{projectId}/labels", projectId)
				.header(HttpHeaders.AUTHORIZATION, bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "name": "%s",
						  "color": "%s"
						}
						""".formatted(name, color)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andReturn();

		return json(result).at("/data/id").asLong();
	}

	private Long createIssue(String token, Long projectId, String title) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/projects/{projectId}/issues", projectId)
				.header(HttpHeaders.AUTHORIZATION, bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "title": "%s",
						  "description": "Created by integration test",
						  "status": "TODO",
						  "priority": "HIGH",
						  "dueDate": "%s"
						}
						""".formatted(title, LocalDate.now().plusDays(7))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andReturn();

		return json(result).at("/data/id").asLong();
	}

	private void updateIssueLabels(String token, Long issueId, Long labelId) throws Exception {
		mockMvc.perform(put("/api/issues/{issueId}/labels", issueId)
				.header(HttpHeaders.AUTHORIZATION, bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "labelIds": [%d]
						}
						""".formatted(labelId)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.labels[0].id").value(labelId));
	}

	private void createComment(String token, Long issueId, String content) throws Exception {
		mockMvc.perform(post("/api/issues/{issueId}/comments", issueId)
				.header(HttpHeaders.AUTHORIZATION, bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "content": "%s"
						}
						""".formatted(content)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.issueId").value(issueId));
	}

	private void assignIssue(String token, Long issueId, Long assigneeId) throws Exception {
		mockMvc.perform(patch("/api/issues/{issueId}/assignee", issueId)
				.header(HttpHeaders.AUTHORIZATION, bearer(token))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "assigneeId": %d
						}
						""".formatted(assigneeId)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.assignee.id").value(assigneeId));
	}

	private JsonNode json(MvcResult result) throws Exception {
		return objectMapper.readTree(result.getResponse().getContentAsString());
	}

	private String bearer(String token) {
		return "Bearer " + token;
	}

	private String uniqueSuffix() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
	}
}
