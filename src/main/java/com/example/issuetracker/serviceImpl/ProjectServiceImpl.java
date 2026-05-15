package com.example.issuetracker.serviceImpl;

import com.example.issuetracker.dto.ProjectCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.ProjectUpdateRequest;
import com.example.issuetracker.dto.response.ProjectResponse;
import com.example.issuetracker.dto.response.ProjectStatsResponse;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.CommentRepository;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository repo;
    
    @Autowired
    private IssueRepository issueRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Override
    public ProjectResponse createProject(ProjectCreateRequest request) {
        Project project = new Project(
                request.getName(),
                request.getDescription()
        );

        Project savedProject = repo.save(project);

        return new ProjectResponse(savedProject);
    }

    @Override
    public List<ProjectResponse> getProjects() {
        return repo.findAll()
                .stream()
                .map(ProjectResponse::new)
                .toList();
    }

    @Override
    public ProjectResponse getProject(Long projectId) {
        Project project = repo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));

        return new ProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request) {
        Project project = repo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));

        project.update(
                request.getName(),
                request.getDescription(),
                request.getStatus()
        );

        Project updatedProject = repo.save(project);

        return new ProjectResponse(updatedProject);
    }

    @Override
    public void deleteProject(Long projectId) {
        Project project = repo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));

        repo.delete(project);
    }

    @Override
    public ProjectStatsResponse getProjectStats(Long projectId) {
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

        return new ProjectStatsResponse(
                project.getId(),
                project.getName(),
                totalIssues,
                todoCount,
                inProgressCount,
                doneCount,
                highPriorityCount,
                mediumPriorityCount,
                lowPriorityCount,
                totalComments
        );
    }
}