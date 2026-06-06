package com.example.issuetracker.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "project_members", uniqueConstraints = {
		@UniqueConstraint(name = "uk_project_member_project_user", columnNames = { "project_id", "user_id" }) })
public class ProjectMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Project 삭제 시 멤버도 같이 삭제되게 하려면 Project 쪽 cascade보다 DB/Flyway에서 FK 정책을 명확히 잡는 편이
	// 좋습니다.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProjectMemberRole role;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private ProjectMember(Project project, User user, ProjectMemberRole role) {
		this.project = project;
		this.user = user;
		this.role = role;
	}

	public static ProjectMember owner(Project project, User user) {
		return new ProjectMember(project, user, ProjectMemberRole.OWNER);
	}

	public static ProjectMember member(Project project, User user) {
		return new ProjectMember(project, user, ProjectMemberRole.MEMBER);
	}

	public boolean isOwner() {
		return this.role == ProjectMemberRole.OWNER;
	}
}