package com.example.issuetracker.service;

import com.example.issuetracker.dto.ProjectCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.ProjectUpdateRequest;

import com.example.issuetracker.dto.response.ProjectResponse;
import com.example.issuetracker.dto.response.ProjectStatsResponse;


import java.util.List;

public interface ProjectService {

	ProjectResponse createProject(ProjectCreateRequest request);

	List<ProjectResponse> getProjects();

	ProjectResponse getProject(Long projectId);

	ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request);

	void deleteProject(Long projectId);
	
	ProjectStatsResponse getProjectStats(Long projectId);
}