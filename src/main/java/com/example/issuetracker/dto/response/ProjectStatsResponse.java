package com.example.issuetracker.dto.response;

import lombok.Getter;

@Getter
public class ProjectStatsResponse {

    private Long projectId;
    private String projectName;

    private long totalIssues;

    private long todoCount;
    private long inProgressCount;
    private long doneCount;

    private long highPriorityCount;
    private long mediumPriorityCount;
    private long lowPriorityCount;

    private long totalComments;

    public ProjectStatsResponse(
            Long projectId,
            String projectName,
            long totalIssues,
            long todoCount,
            long inProgressCount,
            long doneCount,
            long highPriorityCount,
            long mediumPriorityCount,
            long lowPriorityCount,
            long totalComments
    ) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.totalIssues = totalIssues;
        this.todoCount = todoCount;
        this.inProgressCount = inProgressCount;
        this.doneCount = doneCount;
        this.highPriorityCount = highPriorityCount;
        this.mediumPriorityCount = mediumPriorityCount;
        this.lowPriorityCount = lowPriorityCount;
        this.totalComments = totalComments;
    }
}