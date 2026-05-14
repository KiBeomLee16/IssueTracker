package com.example.issuetracker.service;

import java.util.List;

import com.example.issuetracker.dto.UserCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.UserUpdateRequest;
import com.example.issuetracker.dto.response.UserResponse;

public interface UserService {
	
	
	UserResponse createUser(UserCreateRequest request);
	List<UserResponse> getUsers();
	UserResponse getUser(Long id);
	UserResponse updateUser(Long id, UserUpdateRequest request);
	void deleteUser(Long id) ;
	
}
