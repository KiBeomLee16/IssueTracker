package com.example.issuetracker.dto;

import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class IssueUpdateRequest {

    @NotBlank(message = "Issue title is required.")
    private String title;

    private String description;

    @NotNull(message = "Issue status is required.")
    private IssueStatus status;

    @NotNull(message = "Issue priority is required.")
    private IssuePriority priority;

    @FutureOrPresent(message = "Due date must be today or future.")
    private LocalDate dueDate;
}