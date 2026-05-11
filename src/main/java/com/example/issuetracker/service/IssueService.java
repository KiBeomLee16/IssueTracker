package com.example.issuetracker.service;

import com.example.issuetracker.dto.IssueCreateRequest;
import com.example.issuetracker.dto.IssueResponse;
import com.example.issuetracker.dto.IssueUpdateRequest;

import java.util.List;

public interface IssueService {

    IssueResponse createIssue(Long projectId, IssueCreateRequest request);

    List<IssueResponse> getIssuesByProject(Long projectId);

    IssueResponse getIssue(Long issueId);

    IssueResponse updateIssue(Long issueId, IssueUpdateRequest request);

    void deleteIssue(Long issueId);
}