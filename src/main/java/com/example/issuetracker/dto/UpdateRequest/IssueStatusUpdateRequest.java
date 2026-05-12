package com.example.issuetracker.dto.UpdateRequest;

import com.example.issuetracker.entity.IssueStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IssueStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private IssueStatus status;
}