package com.example.issuetracker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.issuetracker.entity.ProjectMemberRole;
import com.example.issuetracker.exception.ForbiddenException;
import com.example.issuetracker.repository.ProjectMemberRepository;
import com.example.issuetracker.security.CurrentUserProvider;
import com.example.issuetracker.serviceImpl.ProjectAuthorizationService;

@ExtendWith(MockitoExtension.class)
class ProjectAuthorizationServiceTest {

	@Mock
	private ProjectMemberRepository projectMemberRepository;

	@Mock
	private CurrentUserProvider currentUserProvider;

	@InjectMocks
	private ProjectAuthorizationService projectAuthorizationService;

	@Test
	void requireProjectOwner_allowsAdminForEveryProject() {
		when(currentUserProvider.isAdmin()).thenReturn(true);

		assertDoesNotThrow(() -> projectAuthorizationService.requireProjectOwner(999L));

		verify(currentUserProvider, never()).getCurrentUserId();
		verify(projectMemberRepository, never())
				.existsByProject_IdAndUser_IdAndRole(999L, 1L, ProjectMemberRole.OWNER);
	}

	@Test
	void requireProjectOwner_allowsOwnerForOwnedProject() {
		when(currentUserProvider.isAdmin()).thenReturn(false);
		when(currentUserProvider.getCurrentUserId()).thenReturn(2L);
		when(projectMemberRepository.existsByProject_IdAndUser_IdAndRole(1L, 2L, ProjectMemberRole.OWNER))
				.thenReturn(true);

		assertDoesNotThrow(() -> projectAuthorizationService.requireProjectOwner(1L));
	}

	@Test
	void requireProjectOwner_rejectsOwnerForAnotherProject() {
		when(currentUserProvider.isAdmin()).thenReturn(false);
		when(currentUserProvider.getCurrentUserId()).thenReturn(2L);
		when(projectMemberRepository.existsByProject_IdAndUser_IdAndRole(2L, 2L, ProjectMemberRole.OWNER))
				.thenReturn(false);

		assertThrows(ForbiddenException.class, () -> projectAuthorizationService.requireProjectOwner(2L));
	}
}
