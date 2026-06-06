package com.example.issuetracker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectMemberAddRequest {

	@NotNull(message = "User ID is required.")
	private Long userId;
}
