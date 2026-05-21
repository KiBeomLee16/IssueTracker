package com.example.issuetracker.service;



import com.example.issuetracker.dto.request.LoginRequest;
import com.example.issuetracker.dto.request.UserCreateRequest;
import com.example.issuetracker.dto.response.LoginResponse;
import com.example.issuetracker.dto.response.UserResponse;

public interface AuthService {

    UserResponse signup(UserCreateRequest request);

    LoginResponse login(LoginRequest request);
}	