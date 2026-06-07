package com.example.issuetracker.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.issuetracker.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	@Scheduled(fixedDelayString = "${jwt.refresh-cleanup-interval-ms:3600000}")
	public void deleteExpiredRefreshTokens() {
		refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
	}
}
