package com.example.issuetracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

	@NotBlank(message = "Name is Required.")
	private String name;

	@NotBlank(message = "Email is Required.")
	@Email(message = "Please check your Email format.")
	private String email;

	@NotBlank(message = "User Id is Required.")
	private String userId;

	@NotBlank(message = "User Password is Required.")
	private String password;
}