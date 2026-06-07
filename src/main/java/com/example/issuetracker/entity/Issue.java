package com.example.issuetracker.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "issue")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class Issue {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column(nullable = false)
	private String title;

	@Column(length = 2000)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private IssueStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private IssuePriority priority;

	private LocalDate dueDate;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assignee_id")
	private User assignee;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "author_id", nullable = false)
	private User author;

	@ManyToMany
	@JoinTable(name = "issue_labels", joinColumns = @JoinColumn(name = "issue_id"), inverseJoinColumns = @JoinColumn(name = "label_id"))
	private Set<Label> labels = new LinkedHashSet<>();

	public Issue(String title, String description, IssueStatus status, IssuePriority priority, LocalDate dueDate,
			Project project) {
		this.title = title;
		this.description = description;
		this.status = status == null ? IssueStatus.TODO : status;
		this.priority = priority == null ? IssuePriority.MEDIUM : priority;
		this.dueDate = dueDate;
		this.project = project;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public void update(String title, String description, IssueStatus status, IssuePriority priority,
			LocalDate dueDate) {
		this.title = title;
		this.description = description;
		this.status = status;
		this.priority = priority;
		this.dueDate = dueDate;
		this.updatedAt = LocalDateTime.now();
	}

	public static Issue create(Project project, String title, String description, IssueStatus status,
			IssuePriority priority, LocalDate dueDate, User author, User assignee) {
		Issue issue = new Issue();
		issue.project = project;
		issue.title = title;
		issue.description = description;
		issue.status = status == null ? IssueStatus.TODO : status;
		issue.priority = priority == null ? IssuePriority.MEDIUM : priority;
		issue.dueDate = dueDate;
		issue.author = author;
		issue.assignee = assignee;
		issue.createdAt = LocalDateTime.now();
		issue.updatedAt = LocalDateTime.now();
		return issue;
	}

	public void replaceLabels(Collection<Label> labels) {
		this.labels.clear();
		this.labels.addAll(labels);
		this.updatedAt = LocalDateTime.now();
	}
}
