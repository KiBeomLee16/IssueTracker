package com.example.issuetracker.serviceImpl;

import com.example.issuetracker.dto.request.LoginRequest;
import com.example.issuetracker.dto.request.RefreshTokenRequest;
import com.example.issuetracker.dto.request.UserCreateRequest;
import com.example.issuetracker.dto.response.LoginResponse;
import com.example.issuetracker.dto.response.UserResponse;
import com.example.issuetracker.entity.RefreshToken;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.entity.UserRole;

import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.RefreshTokenRepository;
import com.example.issuetracker.repository.UserRepository;
import com.example.issuetracker.security.CustomUserDetails;
import com.example.issuetracker.security.JwtTokenProvider;
import com.example.issuetracker.service.AuthService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RefreshTokenRepository refreshTokenRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Value("${jwt.refresh-expiration-ms:1209600000}")
	private long refreshExpirationMs;

	@Override
	public UserResponse signup(UserCreateRequest request) {
		if (userRepo.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email already exists.");
		}

		if (userRepo.existsByUserId(request.getUserId())) {
			throw new IllegalArgumentException("User ID already exists.");
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = new User(request.getName(), request.getEmail(), request.getUserId(), encodedPassword,
				UserRole.USER);

		User savedUser = userRepo.save(user);

		return UserResponse.getUserResponse(savedUser);
	}

	@Override
	@Transactional
	public LoginResponse login(LoginRequest request) {
		User user = userRepo.findByUserId(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found."));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Invalid email or password.");
		}

		String accessToken = jwtTokenProvider.generateToken(new CustomUserDetails(user));
		String refreshToken = createRefreshToken(user);

		return new LoginResponse(accessToken, refreshToken, "Bearer");
	}

	@Override
	@Transactional
	public LoginResponse refresh(RefreshTokenRequest request) {
		RefreshToken refreshToken = refreshTokenRepo.findByTokenHash(hashToken(request.getRefreshToken()))
				.orElseThrow(() -> new BadCredentialsException("Invalid refresh token."));

		if (refreshToken.isExpired()) {
			refreshTokenRepo.delete(refreshToken);
			throw new BadCredentialsException("Invalid refresh token.");
		}

		User user = refreshToken.getUser();
		String accessToken = jwtTokenProvider.generateToken(new CustomUserDetails(user));

		return new LoginResponse(accessToken, request.getRefreshToken(), "Bearer");
	}

	@Override
	@Transactional
	public void logout(RefreshTokenRequest request) {
		refreshTokenRepo.deleteByTokenHash(hashToken(request.getRefreshToken()));
	}

	private String createRefreshToken(User user) {
		refreshTokenRepo.deleteByUser_Id(user.getId());

		String token = UUID.randomUUID().toString();
		LocalDateTime expiresAt = LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000);

		refreshTokenRepo.save(new RefreshToken(hashToken(token), user, expiresAt));

		return token;
	}

	private String hashToken(String token) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 algorithm is not available.", e);
		}
	}

}
