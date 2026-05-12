package com.example.issuetracker.controller;

import com.example.issuetracker.dto.CommentCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.CommentUpdateRequest;
import com.example.issuetracker.dto.response.CommentResponse;
import com.example.issuetracker.response.ApiResponse;
import com.example.issuetracker.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/issues/{issueId}/comments")
    public ApiResponse<CommentResponse> createComment(
            @PathVariable Long issueId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = commentService.createComment(issueId, request);

        return ApiResponse.success("Comment created successfully", response);
    }

    @GetMapping("/issues/{issueId}/comments")
    public ApiResponse<List<CommentResponse>> getCommentsByIssue(@PathVariable Long issueId) {
        List<CommentResponse> responses = commentService.getCommentsByIssue(issueId);

        return ApiResponse.success("Comments retrieved successfully", responses);
    }

    @GetMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> getComment(@PathVariable Long commentId) {
        CommentResponse response = commentService.getComment(commentId);

        return ApiResponse.success("Comment retrieved successfully", response);
    }

    @PutMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        CommentResponse response = commentService.updateComment(commentId, request);

        return ApiResponse.success("Comment updated successfully", response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);

        return ApiResponse.success("Comment deleted successfully");
    }
}