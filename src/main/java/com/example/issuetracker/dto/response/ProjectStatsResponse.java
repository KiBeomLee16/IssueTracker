package com.example.issuetracker.dto.response;

import lombok.Getter;

@Getter
public class ProjectStatsResponse {

	private Long projectId;
	private String projectName;

	private long totalIssues;

	private long todoCount;
	private long inProgressCount;
	private long doneCount;

	private long highPriorityCount;
	private long mediumPriorityCount;
	private long lowPriorityCount;

	private long totalComments;

	private long totalMembers;
	private long ownerCount;
	private long memberCount;

	private long assignedIssueCount;
	private long unassignedIssueCount;

	private long overdueIssueCount;
	private long dueSoonIssueCount;

	private double completionRate;
	private double averageCommentsPerIssue;

	public ProjectStatsResponse(Long projectId, String projectName, long totalIssues, long todoCount,
			long inProgressCount, long doneCount, long highPriorityCount, long mediumPriorityCount,
			long lowPriorityCount, long totalComments) {
		this(projectId, projectName, totalIssues, todoCount, inProgressCount, doneCount, highPriorityCount,
				mediumPriorityCount, lowPriorityCount, totalComments, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0.0, 0.0);
	}

	public ProjectStatsResponse(Long projectId, String projectName, long totalIssues, long todoCount,
			long inProgressCount, long doneCount, long highPriorityCount, long mediumPriorityCount,
			long lowPriorityCount, long totalComments, long totalMembers, long ownerCount, long memberCount,
			long assignedIssueCount, long unassignedIssueCount, long overdueIssueCount, long dueSoonIssueCount,
			double completionRate, double averageCommentsPerIssue) {
		this.projectId = projectId;
		this.projectName = projectName;
		this.totalIssues = totalIssues;
		this.todoCount = todoCount;
		this.inProgressCount = inProgressCount;
		this.doneCount = doneCount;
		this.highPriorityCount = highPriorityCount;
		this.mediumPriorityCount = mediumPriorityCount;
		this.lowPriorityCount = lowPriorityCount;
		this.totalComments = totalComments;
		this.totalMembers = totalMembers;
		this.ownerCount = ownerCount;
		this.memberCount = memberCount;
		this.assignedIssueCount = assignedIssueCount;
		this.unassignedIssueCount = unassignedIssueCount;
		this.overdueIssueCount = overdueIssueCount;
		this.dueSoonIssueCount = dueSoonIssueCount;
		this.completionRate = completionRate;
		this.averageCommentsPerIssue = averageCommentsPerIssue;
	}
}
