package com.example.issuetracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.issuetracker.dto.UpdateRequest.ProjectUpdateRequest;
import com.example.issuetracker.dto.request.ProjectCreateRequest;
import com.example.issuetracker.dto.response.ProjectResponse;
import com.example.issuetracker.dto.response.ProjectStatsResponse;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.entity.ProjectMember;
import com.example.issuetracker.entity.ProjectMemberRole;
import com.example.issuetracker.entity.ProjectStatus;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.entity.UserRole;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.CommentRepository;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.repository.ProjectMemberRepository;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.security.CurrentUserProvider;
import com.example.issuetracker.serviceImpl.ProjectAuthorizationService;
import com.example.issuetracker.serviceImpl.ProjectServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

	@Mock
	private ProjectRepository projectRepo;

	@Mock
	private IssueRepository issueRepo;

	@Mock
	private CommentRepository commentRepo;

	@InjectMocks
	private ProjectServiceImpl projectService;

	private Project project;

	@Mock
	private CurrentUserProvider currentUserProvider;

	@Mock
	private ProjectMemberRepository projectMemberRepository;

	@Mock
	private ProjectAuthorizationService projectAuthorizationService;
	private User user;

	@BeforeEach
	void setUp() {
		project = new Project();

		ReflectionTestUtils.setField(project, "id", 1L);
		ReflectionTestUtils.setField(project, "name", "Issue Tracker");
		ReflectionTestUtils.setField(project, "description", "Issue tracker project");
		user = new User("John", "john@example.com", "john01", "password", UserRole.ADMIN);
		ReflectionTestUtils.setField(user, "id", 1L);
	}

	@Test
	void createProject_success() {
		// given
		ProjectCreateRequest request = new ProjectCreateRequest();
		request.setName("Issue Tracker");
		request.setDescription("Issue tracker project");

		when(projectRepo.save(any(Project.class))).thenAnswer(invocation -> {
			Project savedProject = invocation.getArgument(0);

			ReflectionTestUtils.setField(savedProject, "id", 1L);

			return savedProject;
		});

		User user = new User("John", "john@example.com", "john01", "password", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", 1L);

		when(currentUserProvider.getCurrentUser()).thenReturn(user);
		when(projectMemberRepository.save(any(ProjectMember.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		ProjectResponse response = projectService.createProject(request);

		// then
		assertEquals(1L, response.getId());
		assertEquals("Issue Tracker", response.getName());
		assertEquals("Issue tracker project", response.getDescription());

		verify(projectRepo).save(any(Project.class));
	}

	@Test
	void getProjects_success() {
		// given
		when(currentUserProvider.isAdmin()).thenReturn(true);
		when(projectRepo.findAll()).thenReturn(List.of(project));
		// when

		List<ProjectResponse> response = projectService.getProjects();

		// then
		assertEquals(1, response.size());
		assertEquals(1L, response.get(0).getId());
		assertEquals("Issue Tracker", response.get(0).getName());

		verify(projectRepo).findAll();
	}

	@Test
	void getProjectById_success() {
		// given
		when(projectRepo.findById(1L)).thenReturn(Optional.of(project));

		// when
		ProjectResponse response = projectService.getProject(1L);

		// then
		assertEquals(1L, response.getId());
		assertEquals("Issue Tracker", response.getName());
		assertEquals("Issue tracker project", response.getDescription());

		verify(projectRepo).findById(1L);
	}

	@Test
	void getProjectById_notFound() {
		// given
		when(projectRepo.findById(999L)).thenReturn(Optional.empty());

		// when & then
		assertThrows(ResourceNotFoundException.class, () -> {
			projectService.getProject(999L);
		});

		verify(projectRepo).findById(999L);
	}
	
	@Test
	void getProjects_success_whenUser() {
		// given
		Long currentUserId = 1L;

		Project project = new Project("test_project_1", "sample1");
		ReflectionTestUtils.setField(project, "id", 10L);

		ProjectMember projectMember = new ProjectMember();
		ReflectionTestUtils.setField(projectMember, "id", 100L);
		ReflectionTestUtils.setField(projectMember, "project", project);

		when(currentUserProvider.isAdmin()).thenReturn(false);
		when(currentUserProvider.getCurrentUserId()).thenReturn(currentUserId);
		when(projectMemberRepository.findAllByUser_Id(currentUserId))
				.thenReturn(List.of(projectMember));

		// when
		List<ProjectResponse> result = projectService.getProjects();

		// then
		assertEquals(1, result.size());
		assertEquals(10L, result.get(0).getId());
		assertEquals("test_project_1", result.get(0).getName());
		assertEquals("sample1", result.get(0).getDescription());

		verify(currentUserProvider).isAdmin();
		verify(currentUserProvider).getCurrentUserId();
		verify(projectMemberRepository).findAllByUser_Id(currentUserId);

	}

	@Test
	void updateProject_success() {
		// given
		ProjectUpdateRequest request = new ProjectUpdateRequest();
		request.setName("Updated Project");
		request.setDescription("Updated description");

		when(projectRepo.findById(1L)).thenReturn(Optional.of(project));

		when(projectRepo.save(any(Project.class))).thenAnswer(invocation -> {
			return invocation.getArgument(0);
		});

		// when
		ProjectResponse response = projectService.updateProject(1L, request);

		// then
		assertEquals(1L, response.getId());
		assertEquals("Updated Project", response.getName());
		assertEquals("Updated description", response.getDescription());

		verify(projectRepo).findById(1L);
		verify(projectRepo).save(any(Project.class));
	}

	@Test
	void updateProject_notFound() {
		// given
		ProjectUpdateRequest request = new ProjectUpdateRequest();
		request.setName("Updated Project");
		request.setDescription("Updated description");

		when(projectRepo.findById(999L)).thenReturn(Optional.empty());

		// when & then
		assertThrows(ResourceNotFoundException.class, () -> {
			projectService.updateProject(999L, request);
		});

		verify(projectRepo).findById(999L);
	}

	@Test
	void deleteProject_success() {
		// given
		when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
		doNothing().when(projectRepo).delete(project);

		// when
		projectService.deleteProject(1L);

		// then
		verify(projectRepo).findById(1L);
		verify(projectRepo).delete(project);
	}

	@Test
	void deleteProject_notFound() {
		// given
		when(projectRepo.findById(999L)).thenReturn(Optional.empty());

		// when & then
		assertThrows(ResourceNotFoundException.class, () -> {
			projectService.deleteProject(999L);
		});

		verify(projectRepo).findById(999L);
	}

	@Test
	void getProjectStats_success() {
		// given
		when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
		when(issueRepo.countByProject_Id(1L)).thenReturn(10L);
		when(issueRepo.countByProject_IdAndStatus(1L, IssueStatus.TODO)).thenReturn(3L);
		when(issueRepo.countByProject_IdAndStatus(1L, IssueStatus.IN_PROGRESS)).thenReturn(4L);
		when(issueRepo.countByProject_IdAndStatus(1L, IssueStatus.DONE)).thenReturn(3L);
		when(issueRepo.countByProject_IdAndPriority(1L, IssuePriority.HIGH)).thenReturn(2L);
		when(issueRepo.countByProject_IdAndPriority(1L, IssuePriority.MEDIUM)).thenReturn(5L);
		when(issueRepo.countByProject_IdAndPriority(1L, IssuePriority.LOW)).thenReturn(3L);
		when(commentRepo.countByIssue_Project_Id(1L)).thenReturn(15L);
		when(projectMemberRepository.countByProject_Id(1L)).thenReturn(4L);
		when(projectMemberRepository.countByProject_IdAndRole(1L, ProjectMemberRole.OWNER)).thenReturn(1L);
		when(projectMemberRepository.countByProject_IdAndRole(1L, ProjectMemberRole.MEMBER)).thenReturn(3L);
		when(issueRepo.countByProject_IdAndAssigneeIsNotNull(1L)).thenReturn(7L);
		when(issueRepo.countByProject_IdAndAssigneeIsNull(1L)).thenReturn(3L);
		when(issueRepo.countByProject_IdAndDueDateBeforeAndStatusNot(any(), any(), any())).thenReturn(2L);
		when(issueRepo.countByProject_IdAndDueDateBetweenAndStatusNot(any(), any(), any(), any())).thenReturn(4L);

		// when
		ProjectStatsResponse response = projectService.getProjectStats(1L);

		// then
		assertEquals(1L, response.getProjectId());
		assertEquals("Issue Tracker", response.getProjectName());
		assertEquals(10L, response.getTotalIssues());
		assertEquals(4L, response.getTotalMembers());
		assertEquals(1L, response.getOwnerCount());
		assertEquals(3L, response.getMemberCount());
		assertEquals(7L, response.getAssignedIssueCount());
		assertEquals(3L, response.getUnassignedIssueCount());
		assertEquals(2L, response.getOverdueIssueCount());
		assertEquals(4L, response.getDueSoonIssueCount());
		assertEquals(30.0, response.getCompletionRate());
		assertEquals(1.5, response.getAverageCommentsPerIssue());
	}
}
