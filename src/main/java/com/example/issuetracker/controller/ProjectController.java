package com.example.issuetracker.controller;

import com.example.issuetracker.dto.ProjectCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.ProjectUpdateRequest;
import com.example.issuetracker.dto.response.ProjectResponse;
import com.example.issuetracker.response.ApiResponse;
import com.example.issuetracker.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody ProjectCreateRequest request
    ) {
        ProjectResponse response = projectService.createProject(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully.", response));
    }

    @GetMapping("/getAllProjects")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects() {
        List<ProjectResponse> response = projectService.getProjects();

        return ResponseEntity.ok(
                ApiResponse.success("Projects retrieved successfully.", response)
        );
    }

    @GetMapping("/get/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @PathVariable Long projectId
    ) {
        ProjectResponse response = projectService.getProject(projectId);

        return ResponseEntity.ok(
                ApiResponse.success("Project retrieved successfully.", response)
        );
    }

    @PutMapping("/update/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequest request
    ) {
        ProjectResponse response = projectService.updateProject(projectId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Project updated successfully.", response)
        );
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long projectId
    ) {
        projectService.deleteProject(projectId);

        return ResponseEntity.ok(
                ApiResponse.success("Project deleted successfully.")
        );
    }
}