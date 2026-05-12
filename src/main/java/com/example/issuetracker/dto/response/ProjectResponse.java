package com.example.issuetracker.dto.response;

import com.example.issuetracker.entity.Project;
import com.example.issuetracker.entity.ProjectStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final ProjectStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.status = project.getStatus();
        this.createdAt = project.getCreatedAt();
        this.updatedAt = project.getUpdatedAt();
    }
}