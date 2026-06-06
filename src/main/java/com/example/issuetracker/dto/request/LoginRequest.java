package com.example.issuetracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

	@NotBlank(message = "User Id is required.")

	private String userId;

	@NotBlank(message = "Password is required.")
	private String password;
}