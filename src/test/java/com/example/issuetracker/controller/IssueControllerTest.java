package com.example.issuetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

import com.example.issuetracker.dto.UpdateRequest.IssueStatusUpdateRequest;
import com.example.issuetracker.dto.UpdateRequest.IssueUpdateRequest;
import com.example.issuetracker.dto.request.IssueAssignRequest;
import com.example.issuetracker.dto.request.IssueCreateRequest;
import com.example.issuetracker.dto.response.IssueResponse;
import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.response.PageResponse;
import com.example.issuetracker.security.CustomUserDetailsService;
import com.example.issuetracker.security.JwtAuthenticationFilter;
import com.example.issuetracker.security.JwtTokenProvider;
import com.example.issuetracker.service.IssueService;

@WebMvcTest(IssueController.class)
@WithMockUser(username = "user01", roles = "USER")
@AutoConfigureMockMvc(addFilters = false)
public class IssueControllerTest {

	   @Autowired
	    private MockMvc mockMvc;

	    @MockitoBean
	    private IssueService issueService;

	    @MockitoBean
	    private CustomUserDetailsService customUserDetailsService;

	    @MockitoBean
	    private JwtAuthenticationFilter jwtAuthenticationFilter;

	    @MockitoBean
	    private JwtTokenProvider jwtTokenProvider;

    @Test
    void createIssue_success() throws Exception {
        // given
        String requestBody = """
                {
                  "title": "Login bug",
                  "description": "Login fails with 500 error",
                  "status": "TODO",
                  "priority": "HIGH",
                  "dueDate": "2030-12-31"
                }
                """;

        IssueResponse response = createIssueResponse(
                1L,
                1L,
                "Login bug",
                "Login fails with 500 error",
                IssueStatus.TODO,
                IssuePriority.HIGH,
                LocalDate.of(2030, 12, 31),
                null
        );

        when(issueService.createIssue(eq(1L), any(IssueCreateRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/projects/{projectId}/issues", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Issue created successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.title").value("Login bug"))
                .andExpect(jsonPath("$.data.description").value("Login fails with 500 error"))
                .andExpect(jsonPath("$.data.status").value("TODO"))
                .andExpect(jsonPath("$.data.priority").value("HIGH"))
                .andExpect(jsonPath("$.data.dueDate").value("2030-12-31"));

        verify(issueService).createIssue(eq(1L), any(IssueCreateRequest.class));
    }

    @Test
    void getIssuesByProject_success() throws Exception {
        // given
        IssueResponse issue1 = createIssueResponse(
                1L,
                1L,
                "Login bug",
                "Login fails with 500 error",
                IssueStatus.TODO,
                IssuePriority.HIGH,
                LocalDate.of(2030, 12, 31),
                null
        );

        IssueResponse issue2 = createIssueResponse(
                2L,
                1L,
                "UI task",
                "Update dashboard UI",
                IssueStatus.IN_PROGRESS,
                IssuePriority.MEDIUM,
                LocalDate.of(2030, 12, 31),
                null
        );

        when(issueService.getIssuesByProject(1L))
                .thenReturn(List.of(issue1, issue2));

        // when & then
        mockMvc.perform(get("/api/projects/{projectId}/issues", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Issues retrieved successfully."))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Login bug"))
                .andExpect(jsonPath("$.data[0].status").value("TODO"))
                .andExpect(jsonPath("$.data[0].priority").value("HIGH"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].title").value("UI task"))
                .andExpect(jsonPath("$.data[1].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data[1].priority").value("MEDIUM"));

        verify(issueService).getIssuesByProject(1L);
    }

    @Test
    void getIssue_success() throws Exception {
        // given
        IssueResponse response = createIssueResponse(
                1L,
                1L,
                "Login bug",
                "Login fails with 500 error",
                IssueStatus.TODO,
                IssuePriority.HIGH,
                LocalDate.of(2030, 12, 31),
                null
        );

        when(issueService.getIssue(1L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/issues/{issueId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Issue retrieved successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.title").value("Login bug"))
                .andExpect(jsonPath("$.data.status").value("TODO"))
                .andExpect(jsonPath("$.data.priority").value("HIGH"));

        verify(issueService).getIssue(1L);
    }

    @Test
    void updateIssue_success() throws Exception {
        // given
        String requestBody = """
                {
                  "title": "Updated issue",
                  "description": "Updated issue description",
                  "status": "IN_PROGRESS",
                  "priority": "MEDIUM",
                  "dueDate": "2030-12-31"
                }
                """;

        IssueResponse response = createIssueResponse(
                1L,
                1L,
                "Updated issue",
                "Updated issue description",
                IssueStatus.IN_PROGRESS,
                IssuePriority.MEDIUM,
                LocalDate.of(2030, 12, 31),
                null
        );

        when(issueService.updateIssue(eq(1L), any(IssueUpdateRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(put("/api/issues/{issueId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Issue updated successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Updated issue"))
                .andExpect(jsonPath("$.data.description").value("Updated issue description"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.priority").value("MEDIUM"));

        verify(issueService).updateIssue(eq(1L), any(IssueUpdateRequest.class));
    }

    @Test
    void deleteIssue_success() throws Exception {
        // given
        doNothing().when(issueService).deleteIssue(1L);

        // when & then
        mockMvc.perform(delete("/api/issues/{issueId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Issue deleted successfully."));

        verify(issueService).deleteIssue(1L);
    }

    @Test
    void searchIssuesByProject_success() throws Exception {
        // given
        IssueResponse issue = createIssueResponse(
                1L,
                1L,
                "Login bug",
                "Login fails with 500 error",
                IssueStatus.TODO,
                IssuePriority.HIGH,
                LocalDate.of(2030, 12, 31),
                null
        );

        PageResponse<IssueResponse> response = new PageResponse<>(
                List.of(issue),
                0,
                10,
                1L,
                1,
                true,
                true
        );

        when(issueService.searchIssuesByProject(
                eq(1L),
                eq(IssueStatus.TODO),
                eq(IssuePriority.HIGH),
                eq("Login"),
                eq(0),
                eq(10),
                eq("id"),
                eq("desc")
        )).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/projects/{projectId}/issues/page", 1L)
                        .param("status", "TODO")
                        .param("priority", "HIGH")
                        .param("keyword", "Login")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Issues retrieved successfully."))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("Login bug"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.first").value(true))
                .andExpect(jsonPath("$.data.last").value(true));

        verify(issueService).searchIssuesByProject(
                eq(1L),
                eq(IssueStatus.TODO),
                eq(IssuePriority.HIGH),
                eq("Login"),
                eq(0),
                eq(10),
                eq("id"),
                eq("desc")
        );
    }

    @Test
    void updateIssueStatus_success() throws Exception {
        // given
        String requestBody = """
                {
                  "status": "DONE"
                }
                """;

        IssueResponse response = createIssueResponse(
                1L,
                1L,
                "Login bug",
                "Login fails with 500 error",
                IssueStatus.DONE,
                IssuePriority.HIGH,
                LocalDate.of(2030, 12, 31),
                null
        );

        when(issueService.updateIssueStatus(eq(1L), any(IssueStatusUpdateRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(patch("/api/issues/{issueId}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Issue status updated successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("DONE"));

        verify(issueService).updateIssueStatus(eq(1L), any(IssueStatusUpdateRequest.class));
    }

    @Test
    void assignIssue_success() throws Exception {
        // given
        String requestBody = """
                {
                  "assigneeId": 1
                }
                """;

        IssueResponse response = createIssueResponse(
                1L,
                1L,
                "Login bug",
                "Login fails with 500 error",
                IssueStatus.TODO,
                IssuePriority.HIGH,
                LocalDate.of(2030, 12, 31),
                createUser(1L, "John Doe", "john@example.com", "john01")
        );

        when(issueService.assignIssue(eq(1L), any(IssueAssignRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(patch("/api/issues/{issueId}/assignee", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Issue assignee updated successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.assignee.id").value(1))
                .andExpect(jsonPath("$.data.assignee.name").value("John Doe"))
                .andExpect(jsonPath("$.data.assignee.email").value("john@example.com"))
                .andExpect(jsonPath("$.data.assignee.userId").value("john01"));

        verify(issueService).assignIssue(eq(1L), any(IssueAssignRequest.class));
    }

    @Test
    void unassignIssue_success() throws Exception {
        // given
        IssueResponse response = createIssueResponse(
                1L,
                1L,
                "Login bug",
                "Login fails with 500 error",
                IssueStatus.TODO,
                IssuePriority.HIGH,
                LocalDate.of(2030, 12, 31),
                null
        );

        when(issueService.unassignIssue(1L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(delete("/api/issues/{issueId}/assignee", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Issue assignee removed successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.assignee").doesNotExist());

        verify(issueService).unassignIssue(1L);
    }

    private IssueResponse createIssueResponse(
            Long id,
            Long projectId,
            String title,
            String description,
            IssueStatus status,
            IssuePriority priority,
            LocalDate dueDate,
            User assignee
    ) {
        Project project = new Project();

        ReflectionTestUtils.setField(project, "id", projectId);
        ReflectionTestUtils.setField(project, "name", "Issue Tracker");
        ReflectionTestUtils.setField(project, "description", "Issue tracker REST API project");

        Issue issue = new Issue();

        issue.setId(id);
        issue.setProject(project);
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setStatus(status);
        issue.setPriority(priority);
        issue.setDueDate(dueDate);
        issue.setCreatedAt(LocalDateTime.of(2030, 1, 1, 10, 0));
        issue.setUpdatedAt(LocalDateTime.of(2030, 1, 1, 10, 0));
        issue.setAssignee(assignee);

        return new IssueResponse(issue);
    }

    private User createUser(Long id, String name, String email, String userId) {
        User user = new User();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setUserId(userId);

        return user;
    }
}