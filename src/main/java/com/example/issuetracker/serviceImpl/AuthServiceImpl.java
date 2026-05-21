package com.example.issuetracker.serviceImpl;

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
import com.example.issuetracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponse signup(UserCreateRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        if (userRepo.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("User ID already exists.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getUserId(),
                encodedPassword,
                UserRole.USER
        );

        User savedUser = userRepo.save(user);

        return UserResponse.getUserResponse(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepo.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        String accessToken = jwtTokenProvider.generateToken(new CustomUserDetails(user));

        return new LoginResponse(accessToken, "Bearer");
    }


}