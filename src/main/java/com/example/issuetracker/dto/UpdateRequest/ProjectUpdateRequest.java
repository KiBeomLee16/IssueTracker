package com.example.issuetracker.dto.UpdateRequest;

import com.example.issuetracker.entity.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ProjectUpdateRequest {

    @NotBlank(message = "Project name is required.")
    private String name;

    private String description;
    @NotNull(message = "Project status is required.")
    private ProjectStatus status;
}