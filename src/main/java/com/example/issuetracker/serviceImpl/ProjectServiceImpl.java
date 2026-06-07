package com.example.issuetracker.serviceImpl;

import com.example.issuetracker.dto.UpdateRequest.ProjectUpdateRequest;
import com.example.issuetracker.dto.request.ProjectCreateRequest;
import com.example.issuetracker.dto.response.ProjectResponse;
import com.example.issuetracker.dto.response.ProjectStatsResponse;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.entity.ProjectMember;
import com.example.issuetracker.entity.ProjectMemberRole;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.CommentRepository;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.repository.ProjectMemberRepository;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.security.CurrentUserProvider;
import com.example.issuetracker.service.ProjectService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectRepository repo;

	@Autowired
	private IssueRepository issueRepo;

	@Autowired
	private CommentRepository commentRepo;
	@Autowired
	private ProjectMemberRepository projectMemberRepository;

	@Autowired
	private CurrentUserProvider currentUserProvider;

	@Autowired
	private ProjectAuthorizationService projectAuthorizationService;

	@Transactional
	@Override
	public ProjectResponse createProject(ProjectCreateRequest request) {
		User currentUser = currentUserProvider.getCurrentUser();

		Project project = new Project(request.getName(), request.getDescription());
		Project savedProject = repo.save(project);

		projectMemberRepository.save(ProjectMember.owner(savedProject, currentUser));

		return new ProjectResponse(savedProject);
	}

	@Override
	public List<ProjectResponse> getProjects() {
		if (currentUserProvider.isAdmin()) {
			return repo.findAll().stream().map(ProjectResponse::new).toList();
		}

		Long currentUserId = currentUserProvider.getCurrentUserId();

		return projectMemberRepository.findAllByUser_Id(currentUserId).stream().map(ProjectMember::getProject)
				.map(ProjectResponse::new).toList();
	}

	@Override
	public ProjectResponse getProject(Long projectId) {
		projectAuthorizationService.requireProjectMember(projectId);

		Project project = repo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));

		return new ProjectResponse(project);
	}

	@Override
	public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request) {
		projectAuthorizationService.requireProjectOwner(projectId);

		Project project = repo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));

		project.update(request.getName(), request.getDescription(), request.getStatus());

		Project updatedProject = repo.save(project);

		return new ProjectResponse(updatedProject);
	}

	@Override
	public void deleteProject(Long projectId) {
		projectAuthorizationService.requireProjectOwner(projectId);

		Project project = repo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));

		repo.delete(project);
	}

	@Override
	public ProjectStatsResponse getProjectStats(Long projectId) {
		projectAuthorizationService.requireProjectMember(projectId);
		Project project = repo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));

		long totalIssues = issueRepo.countByProject_Id(projectId);

		long todoCount = issueRepo.countByProject_IdAndStatus(projectId, IssueStatus.TODO);
		long inProgressCount = issueRepo.countByProject_IdAndStatus(projectId, IssueStatus.IN_PROGRESS);
		long doneCount = issueRepo.countByProject_IdAndStatus(projectId, IssueStatus.DONE);

		long highPriorityCount = issueRepo.countByProject_IdAndPriority(projectId, IssuePriority.HIGH);
		long mediumPriorityCount = issueRepo.countByProject_IdAndPriority(projectId, IssuePriority.MEDIUM);
		long lowPriorityCount = issueRepo.countByProject_IdAndPriority(projectId, IssuePriority.LOW);

		long totalComments = commentRepo.countByIssue_Project_Id(projectId);

		long totalMembers = projectMemberRepository.countByProject_Id(projectId);
		long ownerCount = projectMemberRepository.countByProject_IdAndRole(projectId, ProjectMemberRole.OWNER);
		long memberCount = projectMemberRepository.countByProject_IdAndRole(projectId, ProjectMemberRole.MEMBER);

		long assignedIssueCount = issueRepo.countByProject_IdAndAssigneeIsNotNull(projectId);
		long unassignedIssueCount = issueRepo.countByProject_IdAndAssigneeIsNull(projectId);

		LocalDate today = LocalDate.now();
		long overdueIssueCount = issueRepo.countByProject_IdAndDueDateBeforeAndStatusNot(projectId, today,
				IssueStatus.DONE);
		long dueSoonIssueCount = issueRepo.countByProject_IdAndDueDateBetweenAndStatusNot(projectId, today,
				today.plusDays(7), IssueStatus.DONE);

		double completionRate = calculateRate(doneCount, totalIssues);
		double averageCommentsPerIssue = calculateAverage(totalComments, totalIssues);

		return new ProjectStatsResponse(project.getId(), project.getName(), totalIssues, todoCount, inProgressCount,
				doneCount, highPriorityCount, mediumPriorityCount, lowPriorityCount, totalComments, totalMembers,
				ownerCount, memberCount, assignedIssueCount, unassignedIssueCount, overdueIssueCount, dueSoonIssueCount,
				completionRate, averageCommentsPerIssue);
	}

	private double calculateRate(long numerator, long denominator) {
		if (denominator == 0) {
			return 0.0;
		}

		return Math.round((numerator * 10000.0 / denominator)) / 100.0;
	}

	private double calculateAverage(long numerator, long denominator) {
		if (denominator == 0) {
			return 0.0;
		}

		return Math.round((numerator * 100.0 / denominator)) / 100.0;
	}
}
