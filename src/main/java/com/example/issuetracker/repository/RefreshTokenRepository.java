package com.example.issuetracker.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.issuetracker.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByTokenHash(String tokenHash);

	void deleteByTokenHash(String tokenHash);

	void deleteByUser_Id(Long userId);

	void deleteByExpiresAtBefore(LocalDateTime now);
}
