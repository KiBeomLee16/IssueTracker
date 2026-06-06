package com.example.issuetracker.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.issuetracker.entity.ProjectMemberRole;
import com.example.issuetracker.exception.ForbiddenException;
import com.example.issuetracker.repository.ProjectMemberRepository;
import com.example.issuetracker.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectAuthorizationService {

	private final ProjectMemberRepository projectMemberRepository;
	private final CurrentUserProvider currentUserProvider;

	public void requireProjectMember(Long projectId) {
		if (currentUserProvider.isAdmin())
			return;

		Long userId = currentUserProvider.getCurrentUserId();
		if (!isProjectMember(projectId, userId)) {
			throw new ForbiddenException("Project member only.");
		}
	}

	public void requireUserProjectMember(Long projectId, Long userId) {
		if (!isProjectMember(projectId, userId)) {
			throw new ForbiddenException("Project member only.");
		}
	}

	public void requireProjectOwner(Long projectId) {
		if (currentUserProvider.isAdmin())
			return;

		Long userId = currentUserProvider.getCurrentUserId();
		if (!projectMemberRepository.existsByProject_IdAndUser_IdAndRole(projectId, userId, ProjectMemberRole.OWNER)) {
			throw new ForbiddenException("Project owner only.");
		}
	}

	public boolean isProjectOwner(Long projectId) {
		if (currentUserProvider.isAdmin())
			return true;

		return projectMemberRepository.existsByProject_IdAndUser_IdAndRole(projectId,
				currentUserProvider.getCurrentUserId(), ProjectMemberRole.OWNER);
	}

	public boolean isProjectMember(Long projectId) {
		if (currentUserProvider.isAdmin())
			return true;

		return isProjectMember(projectId, currentUserProvider.getCurrentUserId());
	}

	public boolean isProjectMember(Long projectId, Long userId) {
		return projectMemberRepository.existsByProject_IdAndUser_Id(projectId, userId);
	}
}
