package com.example.issuetracker.dto.response;

import java.time.LocalDateTime;

import com.example.issuetracker.entity.IssueHistory;
import com.example.issuetracker.entity.IssueHistoryAction;

import lombok.Getter;

@Getter
public class IssueHistoryResponse {

	private final Long id;
	private final Long issueId;
	private final Long actorId;
	private final String actorUserId;
	private final IssueHistoryAction action;
	private final String fieldName;
	private final String beforeValue;
	private final String afterValue;
	private final LocalDateTime createdAt;

	public IssueHistoryResponse(IssueHistory history) {
		this.id = history.getId();
		this.issueId = history.getIssue().getId();
		this.actorId = history.getActor().getId();
		this.actorUserId = history.getActor().getUserId();
		this.action = history.getAction();
		this.fieldName = history.getFieldName();
		this.beforeValue = history.getBeforeValue();
		this.afterValue = history.getAfterValue();
		this.createdAt = history.getCreatedAt();
	}
}
