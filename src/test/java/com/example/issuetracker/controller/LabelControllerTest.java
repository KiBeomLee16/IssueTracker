package com.example.issuetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.example.issuetracker.dto.request.LabelCreateRequest;
import com.example.issuetracker.dto.response.LabelResponse;
import com.example.issuetracker.entity.Label;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.security.CustomUserDetailsService;
import com.example.issuetracker.security.JwtAuthenticationFilter;
import com.example.issuetracker.security.JwtTokenProvider;
import com.example.issuetracker.service.LabelService;

@WebMvcTest(LabelController.class)
@WithMockUser(username = "owner01", roles = "USER")
@AutoConfigureMockMvc(addFilters = false)
class LabelControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private LabelService labelService;

	@MockitoBean
	private CustomUserDetailsService customUserDetailsService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@Test
	void createLabel_success() throws Exception {
		String requestBody = """
				{
				  "name": "bug",
				  "color": "#dc2626"
				}
				""";
		LabelResponse response = createLabelResponse(1L, 1L, "bug", "#dc2626");

		when(labelService.createLabel(eq(1L), any(LabelCreateRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/projects/{projectId}/labels", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)).andExpect(status().isCreated()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Label created successfully."))
				.andExpect(jsonPath("$.data.id").value(1)).andExpect(jsonPath("$.data.name").value("bug"))
				.andExpect(jsonPath("$.data.color").value("#dc2626"));

		verify(labelService).createLabel(eq(1L), any(LabelCreateRequest.class));
	}

	@Test
	void getLabelsByProject_success() throws Exception {
		LabelResponse response = createLabelResponse(1L, 1L, "backend", "#2563eb");

		when(labelService.getLabelsByProject(1L)).thenReturn(List.of(response));

		mockMvc.perform(get("/api/projects/{projectId}/labels", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Labels retrieved successfully."))
				.andExpect(jsonPath("$.data[0].name").value("backend"));

		verify(labelService).getLabelsByProject(1L);
	}

	@Test
	void deleteLabel_success() throws Exception {
		doNothing().when(labelService).deleteLabel(1L);

		mockMvc.perform(delete("/api/labels/{labelId}", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Label deleted successfully."));

		verify(labelService).deleteLabel(1L);
	}

	private LabelResponse createLabelResponse(Long id, Long projectId, String name, String color) {
		Project project = new Project();
		ReflectionTestUtils.setField(project, "id", projectId);

		Label label = new Label(project, name, color);
		label.setId(id);

		return new LabelResponse(label);
	}
}
