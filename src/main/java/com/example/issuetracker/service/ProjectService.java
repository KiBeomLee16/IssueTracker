package com.example.issuetracker.service;

import com.example.issuetracker.dto.IssueResponse;
import com.example.issuetracker.dto.ProjectCreateRequest;
import com.example.issuetracker.dto.ProjectResponse;
import com.example.issuetracker.dto.ProjectUpdateRequest;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.response.PageResponse;

import java.util.List;

public interface ProjectService {

	ProjectResponse createProject(ProjectCreateRequest request);

	List<ProjectResponse> getProjects();

	ProjectResponse getProject(Long projectId);

	ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request);

	void deleteProject(Long projectId);
}