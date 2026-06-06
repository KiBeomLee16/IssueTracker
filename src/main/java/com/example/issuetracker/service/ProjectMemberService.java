package com.example.issuetracker.service;

import java.util.List;

import com.example.issuetracker.dto.request.ProjectMemberAddRequest;
import com.example.issuetracker.dto.response.ProjectMemberResponse;

public interface ProjectMemberService {

	List<ProjectMemberResponse> getProjectMembers(Long projectId);

	ProjectMemberResponse addProjectMember(Long projectId, ProjectMemberAddRequest request);

	void removeProjectMember(Long projectId, Long userId);
}
