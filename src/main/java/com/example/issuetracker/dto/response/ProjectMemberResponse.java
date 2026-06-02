package com.example.issuetracker.dto.response;

import java.time.LocalDateTime;

import com.example.issuetracker.entity.ProjectMember;
import com.example.issuetracker.entity.ProjectMemberRole;

import lombok.Getter;

@Getter
public class ProjectMemberResponse {

    private final Long id;
    private final Long projectId;
    private final UserResponse user;
    private final ProjectMemberRole role;
    private final LocalDateTime createdAt;

    public ProjectMemberResponse(ProjectMember projectMember) {
        this.id = projectMember.getId();
        this.projectId = projectMember.getProject().getId();
        this.user = UserResponse.getUserResponse(projectMember.getUser());
        this.role = projectMember.getRole();
        this.createdAt = projectMember.getCreatedAt();
    }
}
