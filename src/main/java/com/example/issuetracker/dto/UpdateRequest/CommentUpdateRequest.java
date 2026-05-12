package com.example.issuetracker.dto.UpdateRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentUpdateRequest {

    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment content must be less than 1000 characters")
    private String content;
}