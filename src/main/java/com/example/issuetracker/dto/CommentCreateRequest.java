package com.example.issuetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment content must be less than 1000 characters")
    private String content;
}