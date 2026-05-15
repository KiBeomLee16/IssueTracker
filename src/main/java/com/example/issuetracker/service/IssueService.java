package com.example.issuetracker.service;

import com.example.issuetracker.dto.IssueAssignRequest;
import com.example.issuetracker.dto.IssueCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.IssueStatusUpdateRequest;
import com.example.issuetracker.dto.UpdateRequest.IssueUpdateRequest;
import com.example.issuetracker.dto.response.IssueResponse;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.response.PageResponse;

import java.util.List;

public interface IssueService {

    IssueResponse createIssue(Long projectId, IssueCreateRequest request);

    List<IssueResponse> getIssuesByProject(Long projectId);

    IssueResponse getIssue(Long issueId);

    IssueResponse updateIssue(Long issueId, IssueUpdateRequest request);

    void deleteIssue(Long issueId);
    
	PageResponse<IssueResponse> searchIssuesByProject(Long projectId, IssueStatus status, IssuePriority priority,
			String keyword, int page, int size, String sortBy, String direction);
	
	IssueResponse updateIssueStatus(Long issueId, IssueStatusUpdateRequest request);
	
	IssueResponse assignIssue(Long issueId, IssueAssignRequest request);

	IssueResponse unassignIssue(Long issueId);
}