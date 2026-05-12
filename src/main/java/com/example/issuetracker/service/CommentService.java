package com.example.issuetracker.service;

import com.example.issuetracker.dto.CommentCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.CommentUpdateRequest;
import com.example.issuetracker.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    com.example.issuetracker.dto.response.CommentResponse createComment(Long issueId, CommentCreateRequest request);

    List<com.example.issuetracker.dto.response.CommentResponse> getCommentsByIssue(Long issueId);

    CommentResponse getComment(Long commentId);

    CommentResponse updateComment(Long commentId, CommentUpdateRequest request);

    void deleteComment(Long commentId);
}