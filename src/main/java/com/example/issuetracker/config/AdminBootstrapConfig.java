package com.example.issuetracker.config;

import com.example.issuetracker.entity.User;
import com.example.issuetracker.entity.UserRole;
import com.example.issuetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

	@Bean
	public ApplicationRunner adminBootstrapRunner(UserRepository userRepository, PasswordEncoder passwordEncoder,
			@Value("${app.admin.bootstrap.enabled:false}") boolean enabled,
			@Value("${app.admin.bootstrap.user-id:}") String userId,
			@Value("${app.admin.bootstrap.email:}") String email,
			@Value("${app.admin.bootstrap.name:Admin}") String name,
			@Value("${app.admin.bootstrap.password:}") String password) {
		return args -> {
			if (!enabled) {
				return;
			}

			validateRequiredAdminBootstrapValues(userId, email, password);

			if (userRepository.existsByUserId(userId)) {
				return;
			}

			if (userRepository.existsByEmail(email)) {
				throw new IllegalStateException("Admin bootstrap email already exists.");
			}

			User admin = new User(name, email, userId, passwordEncoder.encode(password), UserRole.ADMIN);

			userRepository.save(admin);
		};
	}

	private void validateRequiredAdminBootstrapValues(String userId, String email, String password) {
		if (userId == null || userId.isBlank()) {
			throw new IllegalStateException("ADMIN_USER_ID is required when admin bootstrap is enabled.");
		}

		if (email == null || email.isBlank()) {
			throw new IllegalStateException("ADMIN_EMAIL is required when admin bootstrap is enabled.");
		}

		if (password == null || password.isBlank()) {
			throw new IllegalStateException("ADMIN_PASSWORD is required when admin bootstrap is enabled.");
		}
	}
}
