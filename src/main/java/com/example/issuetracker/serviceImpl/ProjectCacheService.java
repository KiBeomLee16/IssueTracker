package com.example.issuetracker.serviceImpl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.issuetracker.dto.response.ProjectResponse;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectCacheService {

	private final ProjectRepository projectRepository;

	@Cacheable(cacheNames = "projectById", key = "#projectId", sync = true)
	public ProjectResponse getProject(Long projectId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));

		return new ProjectResponse(project);
	}
}
