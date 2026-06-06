package com.example.issuetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String password;

	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, unique = true)
	private String userId;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	public User(String name, String email, String userId, String password, UserRole role) {
		this.name = name;
		this.email = email;
		this.userId = userId;
		this.password = password;
		this.role = role;
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}