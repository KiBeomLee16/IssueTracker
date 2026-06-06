package com.example.issuetracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration-ms}")
	private long jwtExpirationMs;

	public String generateToken(CustomUserDetails userDetails) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder().subject(userDetails.getUsername()).claim("role", userDetails.getUser().getRole().name())
				.issuedAt(now).expiration(expiration).signWith(getSigningKey()).compact();
	}

	public String getUserIdFromToken(String token) {
		Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();

		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}
}