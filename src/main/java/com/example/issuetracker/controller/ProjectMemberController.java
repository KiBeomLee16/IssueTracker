package com.example.issuetracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.issuetracker.dto.request.ProjectMemberAddRequest;
import com.example.issuetracker.dto.response.ProjectMemberResponse;
import com.example.issuetracker.response.ApiResponse;
import com.example.issuetracker.service.ProjectMemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    @Autowired
    private ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectMemberResponse>>> getProjectMembers(
            @PathVariable Long projectId
    ) {
        List<ProjectMemberResponse> response = projectMemberService.getProjectMembers(projectId);

        return ResponseEntity.ok(
                ApiResponse.success("Project members retrieved successfully.", response)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> addProjectMember(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectMemberAddRequest request
    ) {
        ProjectMemberResponse response = projectMemberService.addProjectMember(projectId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project member added successfully.", response));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeProjectMember(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        projectMemberService.removeProjectMember(projectId, userId);

        return ResponseEntity.ok(
                ApiResponse.success("Project member removed successfully.")
        );
    }
}
