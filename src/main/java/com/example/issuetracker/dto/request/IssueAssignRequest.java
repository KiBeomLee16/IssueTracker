package com.example.issuetracker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueAssignRequest {

    @NotNull(message = "Assignee id is required")
    private Long assigneeId;
}