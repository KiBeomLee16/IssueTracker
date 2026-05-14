package com.example.issuetracker.controller;

import com.example.issuetracker.dto.UserCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.UserUpdateRequest;
import com.example.issuetracker.dto.response.UserResponse;
import com.example.issuetracker.response.ApiResponse;
import com.example.issuetracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserCreateRequest request
    ) {
        UserResponse response = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUsers() {
        return ResponseEntity.ok(
                ApiResponse.success("Users retrieved successfully.", userService.getUsers())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @PathVariable Long id
    ) {
        UserResponse response = userService.getUser(id);

        return ResponseEntity.ok(
                ApiResponse.success("User retrieved successfully.", response)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse response = userService.updateUser(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("User updated successfully.", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id
    ) {
        userService.deleteUser(id);

        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully.")
        );
    }
}