package com.example.issuetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Constructor;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.issuetracker.dto.UpdateRequest.ProjectUpdateRequest;
import com.example.issuetracker.dto.request.ProjectCreateRequest;
import com.example.issuetracker.dto.response.ProjectResponse;
import com.example.issuetracker.dto.response.ProjectStatsResponse;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.security.CustomUserDetailsService;
import com.example.issuetracker.security.JwtAuthenticationFilter;
import com.example.issuetracker.security.JwtTokenProvider;
import com.example.issuetracker.service.ProjectService;

@WebMvcTest(ProjectController.class)
@WithMockUser(username = "user01", roles = "USER")
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProjectService projectService;

	@MockitoBean
	private CustomUserDetailsService customUserDetailsService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@Test
	void createProject_success() throws Exception {
		// given
		String requestBody = """
				{
				  "name": "Issue Tracker",
				  "description": "Issue tracker REST API project"
				}
				""";

		ProjectResponse response = createProjectResponse(1L, "Issue Tracker", "Issue tracker REST API project");

		when(projectService.createProject(any(ProjectCreateRequest.class))).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/projects").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Project created successfully."))
				.andExpect(jsonPath("$.data.id").value(1)).andExpect(jsonPath("$.data.name").value("Issue Tracker"))
				.andExpect(jsonPath("$.data.description").value("Issue tracker REST API project"));
	}

	@Test
	void getProjects_success() throws Exception {
		// given
		ProjectResponse response = createProjectResponse(1L, "Issue Tracker", "Issue tracker REST API project");

		when(projectService.getProjects()).thenReturn(List.of(response));

		// when & then
		mockMvc.perform(get("/api/projects")).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Projects retrieved successfully."))
				.andExpect(jsonPath("$.data[0].id").value(1))
				.andExpect(jsonPath("$.data[0].name").value("Issue Tracker"))
				.andExpect(jsonPath("$.data[0].description").value("Issue tracker REST API project"));
	}

	@Test
	void getProject_success() throws Exception {
		// given
		ProjectResponse response = createProjectResponse(1L, "Issue Tracker", "Issue tracker REST API project");

		when(projectService.getProject(1L)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/projects/{projectId}", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Project retrieved successfully."))
				.andExpect(jsonPath("$.data.id").value(1)).andExpect(jsonPath("$.data.name").value("Issue Tracker"))
				.andExpect(jsonPath("$.data.description").value("Issue tracker REST API project"));
	}

	@Test
	void updateProject_success() throws Exception {
		// given
		String requestBody = """
				{
				  "name": "Updated Project",
				  "description": "Updated project description",
				  "status": "ACTIVE"
				}
				""";

		ProjectResponse response = createProjectResponse(1L, "Updated Project", "Updated project description");

		when(projectService.updateProject(eq(1L), any(ProjectUpdateRequest.class))).thenReturn(response);

		// when & then
		mockMvc.perform(
				put("/api/projects/{projectId}", 1L).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Project updated successfully."))
				.andExpect(jsonPath("$.data.id").value(1)).andExpect(jsonPath("$.data.name").value("Updated Project"))
				.andExpect(jsonPath("$.data.description").value("Updated project description"));
	}

	@Test
	void deleteProject_success() throws Exception {
		// given
		doNothing().when(projectService).deleteProject(1L);

		// when & then
		mockMvc.perform(delete("/api/projects/{projectId}", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Project deleted successfully."));
	}

	@Test
	void getProjectStats_success() throws Exception {
		// given
		ProjectStatsResponse response = createProjectStatsResponse();

		when(projectService.getProjectStats(1L)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/projects/{projectId}/stats", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Project stats retrieved successfully."))
				.andExpect(jsonPath("$.data.projectId").value(1))
				.andExpect(jsonPath("$.data.projectName").value("Issue Tracker"))
				.andExpect(jsonPath("$.data.totalIssues").value(10)).andExpect(jsonPath("$.data.todoCount").value(3))
				.andExpect(jsonPath("$.data.inProgressCount").value(4))
				.andExpect(jsonPath("$.data.doneCount").value(3));
	}

	private ProjectResponse createProjectResponse(Long id, String name, String description) {
		Project project = createInstance(Project.class);

		ReflectionTestUtils.setField(project, "id", id);
		ReflectionTestUtils.setField(project, "name", name);
		ReflectionTestUtils.setField(project, "description", description);

		return new ProjectResponse(project);
	}

	private ProjectStatsResponse createProjectStatsResponse() {
		return new ProjectStatsResponse(1L, "Issue Tracker", 10L, 3L, 4L, 3L, 10L, 3L, 4L, 3L);
	}

	private <T> T createInstance(Class<T> clazz) {
		try {
			Constructor<T> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}