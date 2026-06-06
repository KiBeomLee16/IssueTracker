package com.example.issuetracker;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.issuetracker.controller.UserController;
import com.example.issuetracker.dto.response.UserResponse;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.entity.UserRole;
import com.example.issuetracker.security.CustomUserDetailsService;
import com.example.issuetracker.security.JwtAuthenticationFilter;
import com.example.issuetracker.security.JwtTokenProvider;
import com.example.issuetracker.security.SecurityConfig;
import com.example.issuetracker.service.UserService;

@WebMvcTest(UserController.class)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
public class SecurityAuthorizationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private CustomUserDetailsService customUserDetailsService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@Test
	void protectedApi_withoutToken_returns401() throws Exception {
		mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Authentication is required."));
	}

	@Test
	void adminApi_withUserRole_returns403() throws Exception {
		mockMvc.perform(get("/api/users").with(user("user01").roles("USER"))).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.success").value(false)).andExpect(jsonPath("$.message").value("Access denied."));
	}

	@Test
	void adminApi_withAdminRole_returns200() throws Exception {
		// given
		UserResponse response = createUserResponse(1L, "Admin User", "admin@example.com", "admin01", UserRole.ADMIN);

		when(userService.getUsers()).thenReturn(List.of(response));

		// when & then
		mockMvc.perform(get("/api/users").with(user("admin01").roles("ADMIN"))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Users retrieved successfully."))
				.andExpect(jsonPath("$.data[0].id").value(1)).andExpect(jsonPath("$.data[0].userId").value("admin01"));
	}

	private UserResponse createUserResponse(Long id, String name, String email, String userId, UserRole role) {
		User user = new User(name, email, userId, "encodedPassword", role);

		ReflectionTestUtils.setField(user, "id", id);
		ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());

		return new UserResponse(user);
	}
}