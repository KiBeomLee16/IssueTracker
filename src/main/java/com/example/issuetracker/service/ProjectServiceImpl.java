package com.example.issuetracker.service;

import com.example.issuetracker.dto.ProjectCreateRequest;
import com.example.issuetracker.dto.ProjectResponse;
import com.example.issuetracker.dto.ProjectUpdateRequest;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository repo;

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
}