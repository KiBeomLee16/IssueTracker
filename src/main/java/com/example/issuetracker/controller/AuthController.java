package com.example.issuetracker.controller;

import com.example.issuetracker.dto.request.LoginRequest;
import com.example.issuetracker.dto.request.RefreshTokenRequest;
import com.example.issuetracker.dto.request.UserCreateRequest;
import com.example.issuetracker.dto.response.LoginResponse;
import com.example.issuetracker.dto.response.UserResponse;
import com.example.issuetracker.response.ApiResponse;
import com.example.issuetracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/signup")
	public ApiResponse<UserResponse> signup(@Valid @RequestBody UserCreateRequest request) {
		UserResponse response = authService.signup(request);

		return new ApiResponse<>(true, "Signup successful.", response);
	}

	@PostMapping("/login")
	public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse response = authService.login(request);

		return new ApiResponse<>(true, "Login successful.", response);
	}

	@PostMapping("/refresh")
	public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
		LoginResponse response = authService.refresh(request);

		return new ApiResponse<>(true, "Token refreshed successfully.", response);
	}

	@PostMapping("/logout")
	public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
		authService.logout(request);

		return ApiResponse.success("Logout successful.");
	}
}
