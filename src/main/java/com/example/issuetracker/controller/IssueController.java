package com.example.issuetracker.controller;

import com.example.issuetracker.dto.IssueCreateRequest;
import com.example.issuetracker.dto.IssueResponse;
import com.example.issuetracker.dto.IssueUpdateRequest;
import com.example.issuetracker.response.ApiResponse;
import com.example.issuetracker.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping("/{projectId}/issues/create")
    public ResponseEntity<ApiResponse<IssueResponse>> createIssue(
            @PathVariable Long projectId,
            @Valid @RequestBody IssueCreateRequest request
    ) {
        IssueResponse response = issueService.createIssue(projectId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Issue created successfully.", response));
    }

    @GetMapping("/{projectId}/issues")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getIssuesByProject(
            @PathVariable Long projectId
    ) {
        List<IssueResponse> response = issueService.getIssuesByProject(projectId);

        return ResponseEntity.ok(
                ApiResponse.success("Issues retrieved successfully.", response)
        );
    }

    @GetMapping("/issues/{issueId}")
    public ResponseEntity<ApiResponse<IssueResponse>> getIssue(
            @PathVariable Long issueId
    ) {
        IssueResponse response = issueService.getIssue(issueId);

        return ResponseEntity.ok(
                ApiResponse.success("Issue retrieved successfully.", response)
        );
    }

    @PutMapping("/issues/update/{issueId}")
    public ResponseEntity<ApiResponse<IssueResponse>> updateIssue(
            @PathVariable Long issueId,
            @Valid @RequestBody IssueUpdateRequest request
    ) {
        IssueResponse response = issueService.updateIssue(issueId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Issue updated successfully.", response)
        );
    }

    @DeleteMapping("/issues/delete/{issueId}")
    public ResponseEntity<ApiResponse<Void>> deleteIssue(
            @PathVariable Long issueId
    ) {
        issueService.deleteIssue(issueId);

        return ResponseEntity.ok(
                ApiResponse.success("Issue deleted successfully.")
        );
    }
}