package com.example.issuetracker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueAssignRequest {

    @NotNull(message = "담당자 ID는 필수입니다.")
    private Long assigneeId;
}