package com.example.issuetracker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "issue_histories")
@Getter
@NoArgsConstructor
public class IssueHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "issue_id", nullable = false)
	private Issue issue;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "actor_id", nullable = false)
	private User actor;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private IssueHistoryAction action;

	@Column(nullable = false, length = 50)
	private String fieldName;

	@Column(length = 2000)
	private String beforeValue;

	@Column(length = 2000)
	private String afterValue;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	public IssueHistory(Issue issue, User actor, IssueHistoryAction action, String fieldName, String beforeValue,
			String afterValue) {
		this.issue = issue;
		this.actor = actor;
		this.action = action;
		this.fieldName = fieldName;
		this.beforeValue = beforeValue;
		this.afterValue = afterValue;
		this.createdAt = LocalDateTime.now();
	}
}
