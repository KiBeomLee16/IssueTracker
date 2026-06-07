package com.example.issuetracker;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.issuetracker.repository.RefreshTokenRepository;
import com.example.issuetracker.serviceImpl.RefreshTokenCleanupScheduler;

@ExtendWith(MockitoExtension.class)
class RefreshTokenCleanupSchedulerTest {

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@InjectMocks
	private RefreshTokenCleanupScheduler refreshTokenCleanupScheduler;

	@Test
	void deleteExpiredRefreshTokens_deletesExpiredTokens() {
		refreshTokenCleanupScheduler.deleteExpiredRefreshTokens();

		verify(refreshTokenRepository).deleteByExpiresAtBefore(any(LocalDateTime.class));
	}
}
