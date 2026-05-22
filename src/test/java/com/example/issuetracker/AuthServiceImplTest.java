package com.example.issuetracker;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.issuetracker.dto.request.LoginRequest;
import com.example.issuetracker.dto.request.UserCreateRequest;
import com.example.issuetracker.dto.response.LoginResponse;
import com.example.issuetracker.dto.response.UserResponse;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.entity.UserRole;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.UserRepository;
import com.example.issuetracker.security.CustomUserDetails;
import com.example.issuetracker.security.JwtTokenProvider;
import com.example.issuetracker.serviceImpl.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void signup_success() {
        // given
        UserCreateRequest request = createUserCreateRequest(
                "John Doe",
                "john@example.com",
                "john01",
                "password1234"
        );

        when(userRepo.existsByEmail("john@example.com"))
                .thenReturn(false);
        when(userRepo.existsByUserId("john01"))
                .thenReturn(false);
        when(passwordEncoder.encode("password1234"))
                .thenReturn("encodedPassword");
        when(userRepo.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    ReflectionTestUtils.setField(user, "id", 1L);
                    return user;
                });

        // when
        UserResponse response = authService.signup(request);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getUserId()).isEqualTo("john01");

        verify(passwordEncoder).encode("password1234");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void signup_fail_whenEmailAlreadyExists() {
        // given
        UserCreateRequest request = createUserCreateRequest(
                "John Doe",
                "john@example.com",
                "john01",
                "password1234"
        );

        when(userRepo.existsByEmail("john@example.com"))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists.");

        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void signup_fail_whenUserIdAlreadyExists() {
        // given
        UserCreateRequest request = createUserCreateRequest(
                "John Doe",
                "john@example.com",
                "john01",
                "password1234"
        );

        when(userRepo.existsByEmail("john@example.com"))
                .thenReturn(false);
        when(userRepo.existsByUserId("john01"))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID already exists.");

        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void login_success() {
        // given
        LoginRequest request = createLoginRequest("john01", "password1234");

        User user = createUser(
                1L,
                "John Doe",
                "john@example.com",
                "john01",
                "encodedPassword",
                UserRole.USER
        );

        when(userRepo.findByUserId("john01"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password1234", "encodedPassword"))
                .thenReturn(true);
        when(jwtTokenProvider.generateToken(any(CustomUserDetails.class)))
                .thenReturn("test.jwt.token");

        // when
        LoginResponse response = authService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("test.jwt.token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void login_fail_whenUserNotFound() {
        // given
        LoginRequest request = createLoginRequest("unknown", "password1234");

        when(userRepo.findByUserId("unknown"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found.");
    }

    @Test
    void login_fail_whenPasswordDoesNotMatch() {
        // given
        LoginRequest request = createLoginRequest("john01", "wrongPassword");

        User user = createUser(
                1L,
                "John Doe",
                "john@example.com",
                "john01",
                "encodedPassword",
                UserRole.USER
        );

        when(userRepo.findByUserId("john01"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password.");

        verify(jwtTokenProvider, never()).generateToken(any(CustomUserDetails.class));
    }

    private UserCreateRequest createUserCreateRequest(
            String name,
            String email,
            String userId,
            String password
    ) {
        UserCreateRequest request = new UserCreateRequest();
        request.setName(name);
        request.setEmail(email);
        request.setUserId(userId);
        request.setPassword(password);
        return request;
    }

    private LoginRequest createLoginRequest(String userId, String password) {
        LoginRequest request = new LoginRequest();
        request.setUserId(userId);
        request.setPassword(password);
        return request;
    }

    private User createUser(
            Long id,
            String name,
            String email,
            String userId,
            String password,
            UserRole role
    ) {
        User user = new User(name, email, userId, password, role);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}