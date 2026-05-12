package com.example.issuetracker.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.issuetracker.dto.IssueCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.IssueStatusUpdateRequest;
import com.example.issuetracker.dto.UpdateRequest.IssueUpdateRequest;
import com.example.issuetracker.dto.response.IssueResponse;
import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;

import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.response.PageResponse;
import com.example.issuetracker.service.IssueService;
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

	@Override
	public PageResponse<IssueResponse> searchIssuesByProject(Long projectId, IssueStatus status, IssuePriority priority,
		String keyword, int page, int size, String sortBy, String direction) {
		   Project project = projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found. id = " + projectId));
		   String normalizedKeyword = normalizeKeyword(keyword);
		   Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction)
	                ? Sort.Direction.ASC
	                : Sort.Direction.DESC;
		   Pageable pageable = PageRequest.of(
	                page,
	                size,
	                Sort.by(sortDirection, sortBy)
	        );
		   Page<Issue> issuePage = issueRepo.searchIssuesByProject(
	                project.getId(),
	                status,
	                priority,
	                normalizedKeyword,
	                pageable
	        );
		   
		   List<IssueResponse> content = issuePage.getContent()
	                .stream()	
	                .map(IssueResponse::responseDto)
	                .toList();
		   
		   return new PageResponse<>(
	                content,
	                issuePage.getNumber(),
	                issuePage.getSize(),
	                issuePage.getTotalElements(),
	                issuePage.getTotalPages(),
	                issuePage.isFirst(),
	                issuePage.isLast()
	        );
		   
	}
	
	private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }

	@Override
	public IssueResponse updateIssueStatus(Long issueId, IssueStatusUpdateRequest request) {
		Issue currentIssue = issueRepo.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue ID not found. id = " + issueId));
		currentIssue.setStatus(request.getStatus());
		Issue savedIssue = issueRepo.save(currentIssue);
		return IssueResponse.responseDto(savedIssue);
	}

}
