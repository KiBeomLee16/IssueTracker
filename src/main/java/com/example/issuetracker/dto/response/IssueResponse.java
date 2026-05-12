package com.example.issuetracker.dto.response;

import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter

public class IssueResponse {

    private final Long id;
    private final Long projectId;
    private final String title;
    private final String description;
    private final IssueStatus status;
    private final IssuePriority priority;
    private final LocalDate dueDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public IssueResponse(Issue issue) {
        this.id = issue.getId();
        this.projectId = issue.getProject().getId();
        this.title = issue.getTitle();
        this.description = issue.getDescription();
        this.status = issue.getStatus();
        this.priority = issue.getPriority();
        this.dueDate = issue.getDueDate();
        this.createdAt = issue.getCreatedAt();
        this.updatedAt = issue.getUpdatedAt();
    }
    



    public static IssueResponse responseDto(Issue issue) {
        return new IssueResponse(issue);
    }




}