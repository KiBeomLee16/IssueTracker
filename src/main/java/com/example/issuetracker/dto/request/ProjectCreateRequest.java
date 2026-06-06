package com.example.issuetracker.dto.request;

import com.example.issuetracker.entity.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class ProjectCreateRequest {

	@NotBlank(message = "Project name is required.")
	private String name;

	private String description;

	private ProjectStatus status;

	public ProjectCreateRequest(String name, String description) {
		this.name = name;
		this.description = description;
	}
}