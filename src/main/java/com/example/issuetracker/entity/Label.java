package com.example.issuetracker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "labels", uniqueConstraints = @UniqueConstraint(name = "uk_labels_project_name", columnNames = {
		"project_id", "name" }))
@Getter
@Setter
@NoArgsConstructor
public class Label {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, length = 20)
	private String color;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	public Label(Project project, String name, String color) {
		this.project = project;
		this.name = name;
		this.color = color;
		this.createdAt = LocalDateTime.now();
	}
}
