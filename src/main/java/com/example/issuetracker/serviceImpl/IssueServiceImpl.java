package com.example.issuetracker.serviceImpl;

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.issuetracker.dto.UpdateRequest.IssueStatusUpdateRequest;
import com.example.issuetracker.dto.UpdateRequest.IssueUpdateRequest;
import com.example.issuetracker.dto.request.IssueAssignRequest;
import com.example.issuetracker.dto.request.IssueCreateRequest;
import com.example.issuetracker.dto.response.IssueHistoryResponse;
import com.example.issuetracker.dto.response.IssueResponse;
import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.entity.IssueHistory;
import com.example.issuetracker.entity.IssueHistoryAction;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.exception.ForbiddenException;
import com.example.issuetracker.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;

import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.repository.IssueHistoryRepository;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.repository.UserRepository;
import com.example.issuetracker.response.PageResponse;
import com.example.issuetracker.security.CurrentUserProvider;
import com.example.issuetracker.service.IssueService;

@Service
public class IssueServiceImpl implements IssueService {
	@Autowired
	private ProjectRepository projectRepo;
	@Autowired
	private IssueRepository issueRepo;
	@Autowired
	private IssueHistoryRepository issueHistoryRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private CurrentUserProvider currentUserProvider;
	@Autowired
	private ProjectAuthorizationService projectAuthorizationService;

	@Override
	public IssueResponse createIssue(Long projectId, IssueCreateRequest request) {
		Project currentProject = projectRepo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));
		projectAuthorizationService.requireProjectMember(projectId);
		User currentUser = currentUserProvider.getCurrentUser();
		Issue issue = Issue.create(currentProject, request.getTitle(), request.getDescription(), request.getStatus(),
				request.getPriority(), request.getDueDate(), currentUser, null);

		issueRepo.save(issue);
		return new IssueResponse(issue);
	}

	@Transactional
	@Override
	public List<IssueResponse> getIssuesByProject(Long projectId) {
		projectAuthorizationService.requireProjectMember(projectId);
		List<Issue> currentIssues = issueRepo.findByProject_Id(projectId);
		if (currentIssues.size() == 0) {
			return new ArrayList<IssueResponse>();
		}
		return currentIssues.stream().map(IssueResponse::new).toList();
	}

	@Transactional
	@Override
	public IssueResponse getIssue(Long issueId) {
		Issue issue = findIssue(issueId);
		requireIssueReadable(issue);
		return new IssueResponse(issue);
	}

	@Transactional
	@Override
	public IssueResponse updateIssue(Long issueId, IssueUpdateRequest request) {
		Issue issue = findIssue(issueId);
		requireIssueManager(issue);
		if (request.getStatus() != issue.getStatus()) {
			requireIssueStatusManager(issue);
		}
		String beforeTitle = issue.getTitle();
		String beforeDescription = issue.getDescription();
		IssueStatus beforeStatus = issue.getStatus();
		IssuePriority beforePriority = issue.getPriority();
		String beforeDueDate = valueOf(issue.getDueDate());
		User actor = currentUserProvider.getCurrentUser();

		issue.update(request.getTitle(), request.getDescription(), request.getStatus(), request.getPriority(),
				request.getDueDate());
		Issue updatedIssue = issueRepo.save(issue);
		recordHistoryIfChanged(updatedIssue, actor, IssueHistoryAction.UPDATED, "title", beforeTitle,
				updatedIssue.getTitle());
		recordHistoryIfChanged(updatedIssue, actor, IssueHistoryAction.UPDATED, "description", beforeDescription,
				updatedIssue.getDescription());
		recordHistoryIfChanged(updatedIssue, actor, IssueHistoryAction.STATUS_CHANGED, "status", valueOf(beforeStatus),
				valueOf(updatedIssue.getStatus()));
		recordHistoryIfChanged(updatedIssue, actor, IssueHistoryAction.UPDATED, "priority", valueOf(beforePriority),
				valueOf(updatedIssue.getPriority()));
		recordHistoryIfChanged(updatedIssue, actor, IssueHistoryAction.UPDATED, "dueDate", beforeDueDate,
				valueOf(updatedIssue.getDueDate()));
		return new IssueResponse(updatedIssue);
	}

	@Override
	public void deleteIssue(Long issueId) {
		Issue issue = findIssue(issueId);
		requireIssueManager(issue);
		issueRepo.delete(issue);
	}

	@Transactional
	@Override
	public PageResponse<IssueResponse> searchIssuesByProject(Long projectId, IssueStatus status, IssuePriority priority,
			String keyword, int page, int size, String sortBy, String direction) {
		Project project = projectRepo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found. id = " + projectId));
		projectAuthorizationService.requireProjectMember(project.getId());
		String normalizedKeyword = normalizeKeyword(keyword);
		Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
		Page<Issue> issuePage = issueRepo.searchIssuesByProject(project.getId(), status, priority, normalizedKeyword,
				pageable);

		List<IssueResponse> content = issuePage.getContent().stream().map(IssueResponse::responseDto).toList();

		return new PageResponse<>(content, issuePage.getNumber(), issuePage.getSize(), issuePage.getTotalElements(),
				issuePage.getTotalPages(), issuePage.isFirst(), issuePage.isLast());

	}

	private String normalizeKeyword(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}

		return keyword.trim();
	}

	@Transactional
	@Override
	public IssueResponse updateIssueStatus(Long issueId, IssueStatusUpdateRequest request) {
		Issue currentIssue = findIssue(issueId);
		requireIssueStatusManager(currentIssue);
		IssueStatus beforeStatus = currentIssue.getStatus();
		User actor = currentUserProvider.getCurrentUser();
		currentIssue.setStatus(request.getStatus());
		Issue savedIssue = issueRepo.save(currentIssue);
		recordHistoryIfChanged(savedIssue, actor, IssueHistoryAction.STATUS_CHANGED, "status", valueOf(beforeStatus),
				valueOf(savedIssue.getStatus()));
		return IssueResponse.responseDto(savedIssue);
	}

	@Transactional
	@Override
	public IssueResponse assignIssue(Long issueId, IssueAssignRequest request) {
		Issue currentIssue = findIssue(issueId);
		Long projectId = currentIssue.getProject().getId();
		projectAuthorizationService.requireProjectOwner(projectId);
		User beforeAssignee = currentIssue.getAssignee();
		User actor = currentUserProvider.getCurrentUser();
		User currentUser = userRepo.findById(request.getAssigneeId()).orElseThrow(
				() -> new ResourceNotFoundException("User not Found with this id " + request.getAssigneeId()));
		projectAuthorizationService.requireUserProjectMember(projectId, currentUser.getId());
		currentIssue.setAssignee(currentUser);
		Issue savedIssue = issueRepo.save(currentIssue);
		recordHistoryIfChanged(savedIssue, actor, IssueHistoryAction.ASSIGNEE_CHANGED, "assignee",
				userValue(beforeAssignee), userValue(savedIssue.getAssignee()));
		return IssueResponse.responseDto(savedIssue);
	}

	@Transactional
	@Override
	public IssueResponse unassignIssue(Long issueId) {
		Issue currentIssue = findIssue(issueId);
		projectAuthorizationService.requireProjectOwner(currentIssue.getProject().getId());
		User beforeAssignee = currentIssue.getAssignee();
		User actor = currentUserProvider.getCurrentUser();
		currentIssue.setAssignee(null);
		Issue savedIssue = issueRepo.save(currentIssue);
		recordHistoryIfChanged(savedIssue, actor, IssueHistoryAction.ASSIGNEE_CHANGED, "assignee",
				userValue(beforeAssignee), null);
		return IssueResponse.responseDto(savedIssue);
	}

	@Transactional
	@Override
	public List<IssueHistoryResponse> getIssueHistories(Long issueId) {
		Issue issue = findIssue(issueId);
		requireIssueReadable(issue);

		return issueHistoryRepo.findByIssue_IdOrderByCreatedAtDescIdDesc(issueId).stream()
				.map(IssueHistoryResponse::new).toList();
	}

	private Issue findIssue(Long issueId) {
		return issueRepo.findById(issueId)
				.orElseThrow(() -> new ResourceNotFoundException("Issue ID not found. id = " + issueId));
	}

	private void requireIssueReadable(Issue issue) {
		projectAuthorizationService.requireProjectMember(issue.getProject().getId());
	}

	private void requireIssueManager(Issue issue) {
		Long projectId = issue.getProject().getId();
		projectAuthorizationService.requireProjectMember(projectId);

		if (projectAuthorizationService.isProjectOwner(projectId) || isCurrentUser(issue.getAuthor())) {
			return;
		}

		throw new ForbiddenException("Issue author or project owner only.");
	}

	private void requireIssueStatusManager(Issue issue) {
		Long projectId = issue.getProject().getId();
		projectAuthorizationService.requireProjectMember(projectId);

		if (projectAuthorizationService.isProjectOwner(projectId) || isCurrentUser(issue.getAssignee())) {
			return;
		}

		throw new ForbiddenException("Issue assignee or project owner only.");
	}

	private boolean isCurrentUser(User user) {
		if (user == null) {
			return false;
		}

		return user.getId().equals(currentUserProvider.getCurrentUserId());
	}

	private void recordHistoryIfChanged(Issue issue, User actor, IssueHistoryAction action, String fieldName,
			String beforeValue, String afterValue) {
		if (Objects.equals(beforeValue, afterValue)) {
			return;
		}

		issueHistoryRepo.save(new IssueHistory(issue, actor, action, fieldName, beforeValue, afterValue));
	}

	private String userValue(User user) {
		if (user == null) {
			return null;
		}

		return user.getId() + ":" + user.getUserId();
	}

	private String valueOf(Object value) {
		return value == null ? null : value.toString();
	}

}
