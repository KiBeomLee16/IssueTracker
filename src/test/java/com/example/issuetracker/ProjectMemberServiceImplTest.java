package com.example.issuetracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.issuetracker.dto.request.ProjectMemberAddRequest;
import com.example.issuetracker.dto.response.ProjectMemberResponse;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.entity.ProjectMember;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.repository.ProjectMemberRepository;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.repository.UserRepository;
import com.example.issuetracker.serviceImpl.ProjectAuthorizationService;
import com.example.issuetracker.serviceImpl.ProjectMemberServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceImplTest {

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private ProjectMemberRepository projectMemberRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ProjectAuthorizationService projectAuthorizationService;

	@InjectMocks
	private ProjectMemberServiceImpl projectMemberService;

	private Project project;
	private User user;
	private ProjectMember member;
	private ProjectMember owner;

	@BeforeEach
	void setUp() {
		project = new Project("Issue Tracker", "Project description");
		user = new User();

		ReflectionTestUtils.setField(project, "id", 1L);
		ReflectionTestUtils.setField(user, "id", 2L);
		ReflectionTestUtils.setField(user, "name", "John Doe");
		ReflectionTestUtils.setField(user, "email", "john@example.com");
		ReflectionTestUtils.setField(user, "userId", "john01");
		ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.of(2026, 6, 1, 10, 0));
		ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.of(2026, 6, 1, 10, 0));

		member = ProjectMember.member(project, user);
		owner = ProjectMember.owner(project, user);

		ReflectionTestUtils.setField(member, "id", 1L);
		ReflectionTestUtils.setField(member, "createdAt", LocalDateTime.of(2026, 6, 1, 10, 0));
		ReflectionTestUtils.setField(owner, "id", 2L);
		ReflectionTestUtils.setField(owner, "createdAt", LocalDateTime.of(2026, 6, 1, 10, 0));
	}

	@Test
	void getProjectMembers_success() {
		// given
		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
		when(projectMemberRepository.findAllByProject_Id(1L)).thenReturn(List.of(member));

		// when
		List<ProjectMemberResponse> response = projectMemberService.getProjectMembers(1L);

		// then
		assertEquals(1, response.size());
		assertEquals(1L, response.get(0).getId());
		assertEquals(1L, response.get(0).getProjectId());
		assertEquals(2L, response.get(0).getUser().getId());

		verify(projectAuthorizationService).requireProjectMember(1L);
		verify(projectMemberRepository).findAllByProject_Id(1L);
	}

	@Test
	void addProjectMember_success() {
		// given
		ProjectMemberAddRequest request = new ProjectMemberAddRequest();
		ReflectionTestUtils.setField(request, "userId", 2L);

		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
		when(userRepository.findById(2L)).thenReturn(Optional.of(user));
		when(projectMemberRepository.existsByProject_IdAndUser_Id(1L, 2L)).thenReturn(false);
		when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(invocation -> {
			ProjectMember savedMember = invocation.getArgument(0);
			ReflectionTestUtils.setField(savedMember, "id", 1L);
			ReflectionTestUtils.setField(savedMember, "createdAt", LocalDateTime.of(2026, 6, 1, 10, 0));
			return savedMember;
		});

		// when
		ProjectMemberResponse response = projectMemberService.addProjectMember(1L, request);

		// then
		assertEquals(1L, response.getId());
		assertEquals(1L, response.getProjectId());
		assertEquals(2L, response.getUser().getId());

		verify(projectAuthorizationService).requireProjectOwner(1L);
		verify(projectMemberRepository).save(any(ProjectMember.class));
	}

	@Test
	void addProjectMember_duplicateMember() {
		// given
		ProjectMemberAddRequest request = new ProjectMemberAddRequest();
		ReflectionTestUtils.setField(request, "userId", 2L);

		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
		when(userRepository.findById(2L)).thenReturn(Optional.of(user));
		when(projectMemberRepository.existsByProject_IdAndUser_Id(1L, 2L)).thenReturn(true);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> {
			projectMemberService.addProjectMember(1L, request);
		});

		verify(projectAuthorizationService).requireProjectOwner(1L);
		verify(projectMemberRepository, never()).save(any(ProjectMember.class));
	}

	@Test
	void removeProjectMember_success() {
		// given
		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
		when(projectMemberRepository.findByProject_IdAndUser_Id(1L, 2L)).thenReturn(Optional.of(member));
		doNothing().when(projectMemberRepository).delete(member);

		// when
		projectMemberService.removeProjectMember(1L, 2L);

		// then
		verify(projectAuthorizationService).requireProjectOwner(1L);
		verify(projectMemberRepository).delete(member);
	}

	@Test
	void removeProjectMember_ownerCannotBeRemoved() {
		// given
		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
		when(projectMemberRepository.findByProject_IdAndUser_Id(1L, 2L)).thenReturn(Optional.of(owner));

		// when & then
		assertThrows(IllegalArgumentException.class, () -> {
			projectMemberService.removeProjectMember(1L, 2L);
		});

		verify(projectAuthorizationService).requireProjectOwner(1L);
		verify(projectMemberRepository, never()).delete(any(ProjectMember.class));
	}
}
