package com.example.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.issuetracker.entity.User;
import com.example.issuetracker.entity.UserRole;
import com.example.issuetracker.repository.UserRepository;
import com.example.issuetracker.security.CurrentUserProvider;
import com.example.issuetracker.security.CustomUserDetails;
@ExtendWith(MockitoExtension.class)
public class SecurityDtoTest {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CurrentUserProvider currentUserProvider;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void testUser() {
		User user = new User("John", "John@test.com", "john1", "password", UserRole.ADMIN);
		CustomUserDetails details = new CustomUserDetails(user);
		assertEquals("John@test.com", details.getEmail());
		assertEquals("john1", details.getUserId());
		assertEquals("password", details.getPassword());
		assertTrue(details.getAuthorities()
				.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
		assertFalse(details.getAuthorities()
				.contains(new SimpleGrantedAuthority("ROLE_USER")));
	}
	
	@Test
	void testUserNull() {
		User user = new User("John", "John@test.com", "john1", null, null);
		CustomUserDetails details = new CustomUserDetails(user);
		assertEquals("John@test.com", details.getEmail());
		
		assertEquals("", details.getPassword());
		assertTrue(details.getAuthorities()
				.contains(new SimpleGrantedAuthority("ROLE_USER")));
		assertFalse(details.getAuthorities()
				.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
		
	}
	
	@Test
	void getCurrentUserId_fail_whenAuthenticationIsNull() {
		// given
		SecurityContextHolder.clearContext();

		// when & then
		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> currentUserProvider.getCurrentUserId()
		);

		assertEquals("Unauthenticated user.", exception.getMessage());
	}

	@Test
	void getCurrentUserId_fail_whenPrincipalIsUnsupportedType() {
		// given
		TestingAuthenticationToken authentication =
				new TestingAuthenticationToken("plainPrincipal", null, "ROLE_USER");

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// when & then
		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> currentUserProvider.getCurrentUserId()
		);

		assertEquals("Unsupported principal type.", exception.getMessage());
	}


}
