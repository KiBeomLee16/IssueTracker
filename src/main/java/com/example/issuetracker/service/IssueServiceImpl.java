package com.example.issuetracker.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.issuetracker.dto.IssueCreateRequest;
import com.example.issuetracker.dto.IssueResponse;
import com.example.issuetracker.dto.IssueUpdateRequest;
import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.repository.ProjectRepository;
@Service
public class IssueServiceImpl implements IssueService {
	@Autowired
	private ProjectRepository projectRepo;
	@Autowired
	private IssueRepository issueRepo;

	@Override
	public IssueResponse createIssue(Long projectId, IssueCreateRequest request) {
		Project currentProject = projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));
		Issue issue = new Issue(request.getTitle(), request.getDescription(), request.getStatus(), request.getPriority(), request.getDueDate(), currentProject);
		issueRepo.save(issue);
		return new IssueResponse(issue);
	}

	@Override
	public List<IssueResponse> getIssuesByProject(Long projectId) {
		List<Issue> currentIssues = issueRepo.findByProject_Id(projectId);
		if (currentIssues.size() == 0) {
			return new ArrayList<IssueResponse>();
		}
		return currentIssues.stream().map(IssueResponse::new).toList();
	}

	@Override
	public IssueResponse getIssue(Long issueId) {
		Issue issue = issueRepo.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue Id not found"));
		return new IssueResponse(issue);
	}

	@Override
	public IssueResponse updateIssue(Long issueId, IssueUpdateRequest request) {
		Issue issue = issueRepo.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue Id not found"));
		issue.update(request.getTitle(), request.getDescription(), request.getStatus(), request.getPriority(), request.getDueDate());
		 Issue updatedIssue =  issueRepo.save(issue);
		return new IssueResponse(updatedIssue);
	}

	@Override
	public void deleteIssue(Long issueId) {
		Issue issue = issueRepo.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue not found. id=" + issueId));
		issueRepo.delete(issue);
	}

}
