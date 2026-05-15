package com.example.issuetracker.dto.response;

import com.example.issuetracker.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

	private Long id;
	private String content;
	private Long issueId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	

	public static CommentResponse responseDto(Comment comment) {
		return CommentResponse.builder().id(comment.getId()).content(comment.getContent())
				.issueId(comment.getIssue().getId()).createdAt(comment.getCreatedAt()).updatedAt(comment.getUpdatedAt())
				.build();
	}
}