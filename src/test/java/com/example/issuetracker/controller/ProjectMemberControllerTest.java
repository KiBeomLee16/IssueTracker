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

import java.time.LocalDateTime;
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

import com.example.issuetracker.dto.request.ProjectMemberAddRequest;
import com.example.issuetracker.dto.response.ProjectMemberResponse;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.entity.ProjectMember;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.security.CustomUserDetailsService;
import com.example.issuetracker.security.JwtAuthenticationFilter;
import com.example.issuetracker.security.JwtTokenProvider;
import com.example.issuetracker.service.ProjectMemberService;

@WebMvcTest(ProjectMemberController.class)
@WithMockUser(username = "user01", roles = "USER")
@AutoConfigureMockMvc(addFilters = false)
class ProjectMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectMemberService projectMemberService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getProjectMembers_success() throws Exception {
        // given
        ProjectMemberResponse response = createProjectMemberResponse();

        when(projectMemberService.getProjectMembers(1L))
                .thenReturn(List.of(response));

        // when & then
        mockMvc.perform(get("/api/projects/{projectId}/members", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project members retrieved successfully."))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].projectId").value(1))
                .andExpect(jsonPath("$.data[0].user.id").value(2))
                .andExpect(jsonPath("$.data[0].role").value("MEMBER"));

        verify(projectMemberService).getProjectMembers(1L);
    }

    @Test
    void addProjectMember_success() throws Exception {
        // given
        String requestBody = """
                {
                  "userId": 2
                }
                """;

        ProjectMemberResponse response = createProjectMemberResponse();

        when(projectMemberService.addProjectMember(eq(1L), any(ProjectMemberAddRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/projects/{projectId}/members", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project member added successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.user.id").value(2))
                .andExpect(jsonPath("$.data.role").value("MEMBER"));

        verify(projectMemberService).addProjectMember(eq(1L), any(ProjectMemberAddRequest.class));
    }

    @Test
    void removeProjectMember_success() throws Exception {
        // given
        doNothing().when(projectMemberService).removeProjectMember(1L, 2L);

        // when & then
        mockMvc.perform(delete("/api/projects/{projectId}/members/{userId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project member removed successfully."));

        verify(projectMemberService).removeProjectMember(1L, 2L);
    }

    private ProjectMemberResponse createProjectMemberResponse() {
        Project project = new Project("Issue Tracker", "Project description");
        User user = new User();

        ReflectionTestUtils.setField(project, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 2L);
        ReflectionTestUtils.setField(user, "name", "John Doe");
        ReflectionTestUtils.setField(user, "email", "john@example.com");
        ReflectionTestUtils.setField(user, "userId", "john01");
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.of(2026, 6, 1, 10, 0));
        ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.of(2026, 6, 1, 10, 0));

        ProjectMember projectMember = ProjectMember.member(project, user);
        ReflectionTestUtils.setField(projectMember, "id", 1L);
        ReflectionTestUtils.setField(projectMember, "createdAt", LocalDateTime.of(2026, 6, 1, 10, 0));

        return new ProjectMemberResponse(projectMember);
    }
}
