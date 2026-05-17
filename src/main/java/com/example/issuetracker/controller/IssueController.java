package com.example.issuetracker.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.issuetracker.dto.IssueAssignRequest;
import com.example.issuetracker.dto.IssueCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.IssueStatusUpdateRequest;
import com.example.issuetracker.dto.UpdateRequest.IssueUpdateRequest;
import com.example.issuetracker.dto.response.IssueResponse;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.response.ApiResponse;
import com.example.issuetracker.response.PageResponse;
import com.example.issuetracker.service.IssueService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @PostMapping({
            "/projects/{projectId}/issues",
            "/projects/{projectId}/issues/create"
    })
    public ResponseEntity<ApiResponse<IssueResponse>> createIssue(
            @PathVariable Long projectId,
            @Valid @RequestBody IssueCreateRequest request
    ) {
        IssueResponse response = issueService.createIssue(projectId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Issue created successfully.", response));
    }

    @GetMapping("/projects/{projectId}/issues")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getIssuesByProject(
            @PathVariable Long projectId
    ) {
        List<IssueResponse> response = issueService.getIssuesByProject(projectId);

        return ResponseEntity.ok(
                ApiResponse.success("Issues retrieved successfully.", response)
        );
    }

    @GetMapping({
            "/issues/{issueId}",
            "/projects/issues/{issueId}"
    })
    public ResponseEntity<ApiResponse<IssueResponse>> getIssue(
            @PathVariable Long issueId
    ) {
        IssueResponse response = issueService.getIssue(issueId);

        return ResponseEntity.ok(
                ApiResponse.success("Issue retrieved successfully.", response)
        );
    }

    @PutMapping({
            "/issues/{issueId}",
            "/projects/issues/update/{issueId}"
    })
    public ResponseEntity<ApiResponse<IssueResponse>> updateIssue(
            @PathVariable Long issueId,
            @Valid @RequestBody IssueUpdateRequest request
    ) {
        IssueResponse response = issueService.updateIssue(issueId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Issue updated successfully.", response)
        );
    }

    @DeleteMapping({
            "/issues/{issueId}",
            "/projects/issues/delete/{issueId}"
    })
    public ResponseEntity<ApiResponse<Void>> deleteIssue(
            @PathVariable Long issueId
    ) {
        issueService.deleteIssue(issueId);

        return ResponseEntity.ok(
                ApiResponse.success("Issue deleted successfully.")
        );
    }

    @GetMapping({
            "/projects/{projectId}/issues/page",
            "/projects/pages/{projectId}/issues"
    })
    public ResponseEntity<ApiResponse<PageResponse<IssueResponse>>> searchIssuesByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) IssuePriority priority,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        PageResponse<IssueResponse> response = issueService.searchIssuesByProject(
                projectId,
                status,
                priority,
                keyword,
                page,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(
                ApiResponse.success("Issues retrieved successfully", response)
        );
    }

    @PatchMapping({
            "/issues/{issueId}/status",
            "/projects/issues/{issueId}/status"
    })
    public ResponseEntity<ApiResponse<IssueResponse>> updateIssueStatus(
            @PathVariable Long issueId,
            @Valid @RequestBody IssueStatusUpdateRequest request
    ) {
        IssueResponse response = issueService.updateIssueStatus(issueId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Issue status updated successfully", response)
        );
    }

    @PatchMapping("/issues/{issueId}/assignee")
    public ResponseEntity<ApiResponse<IssueResponse>> assignIssue(
            @PathVariable Long issueId,
            @Valid @RequestBody IssueAssignRequest request
    ) {
        IssueResponse response = issueService.assignIssue(issueId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Issue assignee updated successfully.", response)
        );
    }

    @DeleteMapping("/issues/{issueId}/assignee")
    public ResponseEntity<ApiResponse<IssueResponse>> unassignIssue(
            @PathVariable Long issueId
    ) {
        IssueResponse response = issueService.unassignIssue(issueId);

        return ResponseEntity.ok(
                ApiResponse.success("Issue assignee removed successfully.", response)
        );
    }
}