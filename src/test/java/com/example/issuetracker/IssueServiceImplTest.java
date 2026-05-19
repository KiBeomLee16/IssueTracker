package com.example.issuetracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.issuetracker.dto.IssueAssignRequest;
import com.example.issuetracker.dto.IssueCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.IssueStatusUpdateRequest;
import com.example.issuetracker.dto.UpdateRequest.IssueUpdateRequest;
import com.example.issuetracker.dto.response.IssueResponse;
import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.repository.UserRepository;
import com.example.issuetracker.serviceImpl.IssueServiceImpl;

@ExtendWith(MockitoExtension.class)
class IssueServiceImplTest {

    @Mock
    private ProjectRepository projectRepo;

    @Mock
    private IssueRepository issueRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private IssueServiceImpl issueService;

    private Project project;
    private Issue issue;
    private User user;

    @BeforeEach
    void setUp() {
        project = createProjectEntity();
        issue = createIssueEntity();
        user = createUserEntity();

        ReflectionTestUtils.setField(project, "id", 1L);
        ReflectionTestUtils.setField(project, "name", "Issue Tracker");
        ReflectionTestUtils.setField(project, "description", "Project description");

        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "name", "John Doe");
        ReflectionTestUtils.setField(user, "email", "john@example.com");

        ReflectionTestUtils.setField(issue, "id", 1L);
        ReflectionTestUtils.setField(issue, "title", "Test issue");
        ReflectionTestUtils.setField(issue, "description", "Test description");
        ReflectionTestUtils.setField(issue, "status", IssueStatus.TODO);
        ReflectionTestUtils.setField(issue, "priority", IssuePriority.HIGH);
        ReflectionTestUtils.setField(issue, "dueDate", LocalDate.of(2026, 6, 30));
        ReflectionTestUtils.setField(issue, "project", project);
        ReflectionTestUtils.setField(issue, "assignee", null);
    }

    @Test
    void createIssue_success() {
        // given
        IssueCreateRequest request = new IssueCreateRequest();
        ReflectionTestUtils.setField(request, "title", "Test issue");
        ReflectionTestUtils.setField(request, "description", "Test description");
        ReflectionTestUtils.setField(request, "status", IssueStatus.TODO);
        ReflectionTestUtils.setField(request, "priority", IssuePriority.HIGH);
        ReflectionTestUtils.setField(request, "dueDate", LocalDate.of(2026, 6, 30));

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));

        when(issueRepo.save(any(Issue.class))).thenAnswer(invocation -> {
            Issue savedIssue = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedIssue, "id", 1L);
            return savedIssue;
        });

        // when
        IssueResponse response = issueService.createIssue(1L, request);

        // then
        assertEquals(1L, response.getId());
        assertEquals("Test issue", response.getTitle());
        assertEquals("Test description", response.getDescription());
        assertEquals(IssueStatus.TODO, response.getStatus());
        assertEquals(IssuePriority.HIGH, response.getPriority());

        verify(projectRepo).findById(1L);
        verify(issueRepo).save(any(Issue.class));
    }

    @Test
    void createIssue_projectNotFound() {
        // given
        IssueCreateRequest request = new IssueCreateRequest();
        ReflectionTestUtils.setField(request, "title", "Test issue");
        ReflectionTestUtils.setField(request, "description", "Test description");
        ReflectionTestUtils.setField(request, "status", IssueStatus.TODO);
        ReflectionTestUtils.setField(request, "priority", IssuePriority.HIGH);
        ReflectionTestUtils.setField(request, "dueDate", LocalDate.of(2026, 6, 30));

        when(projectRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            issueService.createIssue(999L, request);
        });

        verify(projectRepo).findById(999L);
    }

    @Test
    void getIssuesByProject_success() {
        // given
        when(issueRepo.findByProject_Id(1L)).thenReturn(List.of(issue));

        // when
        List<IssueResponse> response = issueService.getIssuesByProject(1L);

        // then
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals("Test issue", response.get(0).getTitle());
        assertEquals(IssueStatus.TODO, response.get(0).getStatus());
        assertEquals(IssuePriority.HIGH, response.get(0).getPriority());

        verify(issueRepo).findByProject_Id(1L);
    }

    @Test
    void getIssuesByProject_emptyList() {
        // given
        when(issueRepo.findByProject_Id(1L)).thenReturn(List.of());

        // when
        List<IssueResponse> response = issueService.getIssuesByProject(1L);

        // then
        assertEquals(0, response.size());

        verify(issueRepo).findByProject_Id(1L);
    }

    @Test
    void getIssue_success() {
        // given
        when(issueRepo.findById(1L)).thenReturn(Optional.of(issue));

        // when
        IssueResponse response = issueService.getIssue(1L);

        // then
        assertEquals(1L, response.getId());
        assertEquals("Test issue", response.getTitle());
        assertEquals("Test description", response.getDescription());
        assertEquals(IssueStatus.TODO, response.getStatus());
        assertEquals(IssuePriority.HIGH, response.getPriority());

        verify(issueRepo).findById(1L);
    }

    @Test
    void getIssue_notFound() {
        // given
        when(issueRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            issueService.getIssue(999L);
        });

        verify(issueRepo).findById(999L);
    }

    @Test
    void updateIssue_success() {
        // given
        IssueUpdateRequest request = new IssueUpdateRequest();
        ReflectionTestUtils.setField(request, "title", "Updated issue");
        ReflectionTestUtils.setField(request, "description", "Updated description");
        ReflectionTestUtils.setField(request, "status", IssueStatus.IN_PROGRESS);
        ReflectionTestUtils.setField(request, "priority", IssuePriority.MEDIUM);
        ReflectionTestUtils.setField(request, "dueDate", LocalDate.of(2026, 7, 10));

        when(issueRepo.findById(1L)).thenReturn(Optional.of(issue));

        when(issueRepo.save(any(Issue.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // when
        IssueResponse response = issueService.updateIssue(1L, request);

        // then
        assertEquals(1L, response.getId());
        assertEquals("Updated issue", response.getTitle());
        assertEquals("Updated description", response.getDescription());
        assertEquals(IssueStatus.IN_PROGRESS, response.getStatus());
        assertEquals(IssuePriority.MEDIUM, response.getPriority());

        verify(issueRepo).findById(1L);
        verify(issueRepo).save(any(Issue.class));
    }

    @Test
    void updateIssue_notFound() {
        // given
        IssueUpdateRequest request = new IssueUpdateRequest();
        ReflectionTestUtils.setField(request, "title", "Updated issue");
        ReflectionTestUtils.setField(request, "description", "Updated description");
        ReflectionTestUtils.setField(request, "status", IssueStatus.IN_PROGRESS);
        ReflectionTestUtils.setField(request, "priority", IssuePriority.MEDIUM);
        ReflectionTestUtils.setField(request, "dueDate", LocalDate.of(2026, 7, 10));

        when(issueRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            issueService.updateIssue(999L, request);
        });

        verify(issueRepo).findById(999L);
    }

    @Test
    void deleteIssue_success() {
        // given
        when(issueRepo.findById(1L)).thenReturn(Optional.of(issue));
        doNothing().when(issueRepo).delete(issue);

        // when
        issueService.deleteIssue(1L);

        // then
        verify(issueRepo).findById(1L);
        verify(issueRepo).delete(issue);
    }

    @Test
    void deleteIssue_notFound() {
        // given
        when(issueRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            issueService.deleteIssue(999L);
        });

        verify(issueRepo).findById(999L);
    }

    @Test
    void updateIssueStatus_success() {
        // given
        IssueStatusUpdateRequest request = new IssueStatusUpdateRequest();
        ReflectionTestUtils.setField(request, "status", IssueStatus.IN_PROGRESS);

        when(issueRepo.findById(1L)).thenReturn(Optional.of(issue));

        when(issueRepo.save(any(Issue.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // when
        IssueResponse response = issueService.updateIssueStatus(1L, request);

        // then
        assertEquals(1L, response.getId());
        assertEquals(IssueStatus.IN_PROGRESS, response.getStatus());

        verify(issueRepo).findById(1L);
        verify(issueRepo).save(any(Issue.class));
    }

    @Test
    void updateIssueStatus_notFound() {
        // given
        IssueStatusUpdateRequest request = new IssueStatusUpdateRequest();
        ReflectionTestUtils.setField(request, "status", IssueStatus.IN_PROGRESS);

        when(issueRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            issueService.updateIssueStatus(999L, request);
        });

        verify(issueRepo).findById(999L);
    }

    @Test
    void assignIssue_success() {
        // given
        IssueAssignRequest request = new IssueAssignRequest();
        ReflectionTestUtils.setField(request, "assigneeId", 1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(issueRepo.findById(1L)).thenReturn(Optional.of(issue));

        when(issueRepo.save(any(Issue.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // when
        IssueResponse response = issueService.assignIssue(1L, request);

        // then
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getAssignee().getId());

        verify(userRepo).findById(1L);
        verify(issueRepo).findById(1L);
        verify(issueRepo).save(any(Issue.class));
    }

    @Test
    void assignIssue_userNotFound() {
        // given
        IssueAssignRequest request = new IssueAssignRequest();
        ReflectionTestUtils.setField(request, "assigneeId", 999L);

        when(userRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            issueService.assignIssue(1L, request);
        });

        verify(userRepo).findById(999L);
    }

    @Test
    void assignIssue_issueNotFound() {
        // given
        IssueAssignRequest request = new IssueAssignRequest();
        ReflectionTestUtils.setField(request, "assigneeId", 1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(issueRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            issueService.assignIssue(999L, request);
        });

        verify(userRepo).findById(1L);
        verify(issueRepo).findById(999L);
    }

    @Test
    void unassignIssue_success() {
        // given
        ReflectionTestUtils.setField(issue, "assignee", user);

        when(issueRepo.findById(1L)).thenReturn(Optional.of(issue));

        when(issueRepo.save(any(Issue.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // when
        IssueResponse response = issueService.unassignIssue(1L);

        // then
        assertEquals(1L, response.getId());
        assertNull(response.getAssignee());

        verify(issueRepo).findById(1L);
        verify(issueRepo).save(any(Issue.class));
    }

    @Test
    void unassignIssue_notFound() {
        // given
        when(issueRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            issueService.unassignIssue(999L);
        });

        verify(issueRepo).findById(999L);
    }

    private Project createProjectEntity() {
        try {
            Constructor<Project> constructor = Project.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Issue createIssueEntity() {
        try {
            Constructor<Issue> constructor = Issue.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User createUserEntity() {
        try {
            Constructor<User> constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}