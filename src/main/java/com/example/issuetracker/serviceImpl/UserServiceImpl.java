package com.example.issuetracker.serviceImpl;

import com.example.issuetracker.dto.UpdateRequest.UserUpdateRequest;
import com.example.issuetracker.dto.request.UserCreateRequest;
import com.example.issuetracker.dto.response.UserResponse;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.entity.UserRole;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.repository.UserRepository;
import com.example.issuetracker.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.authentication.PasswordEncoderParser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private IssueRepository issueRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserResponse createUser(UserCreateRequest request) {
		if (userRepo.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("this email is already registered");
		}

		if (userRepo.existsByUserId(request.getUserId())) {
			throw new IllegalArgumentException("this ID is already registered");
		}
		String encryptedPasswrod = passwordEncoder.encode(request.getPassword());
		User user = new User(request.getName(), request.getEmail(), request.getUserId(), encryptedPasswrod,
				UserRole.USER);

		User savedUser = userRepo.save(user);

		return UserResponse.getUserResponse(savedUser);
	}

	@Override
	public UserResponse createAdmin(UserCreateRequest request) {
		if (userRepo.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("this email is already registered");
		}

		if (userRepo.existsByUserId(request.getUserId())) {
			throw new IllegalArgumentException("this ID is already registered");
		}
		String encryptedPasswrod = passwordEncoder.encode(request.getPassword());
		User user = new User(request.getName(), request.getEmail(), request.getUserId(), encryptedPasswrod,
				UserRole.ADMIN);

		User savedUser = userRepo.save(user);

		return UserResponse.getUserResponse(savedUser);
	}

	@Override
	public List<UserResponse> getUsers() {
		return userRepo.findAll().stream().map(UserResponse::getUserResponse).toList();
	}

	@Override
	public UserResponse getUser(Long id) {
		User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found. id=" + id));

		return UserResponse.getUserResponse(user);
	}

	@Override
	public UserResponse updateUser(Long id, UserUpdateRequest request) {
		User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found. id=" + id));

		userRepo.findByEmail(request.getEmail()).ifPresent(foundUser -> {
			if (!foundUser.getId().equals(id)) {
				throw new IllegalArgumentException("this email is already registered");
			}
		});

		userRepo.findByUserId(request.getUserId()).ifPresent(foundUser -> {
			if (!foundUser.getId().equals(id)) {
				throw new IllegalArgumentException("this ID is already registered");
			}
		});

		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setUserId(request.getUserId());

		User savedUser = userRepo.save(user);

		return UserResponse.getUserResponse(savedUser);
	}

	@Override
	public void deleteUser(Long id) {
		User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found. id=" + id));

		long assignedIssueCount = issueRepo.countByAssignee_Id(id);

		if (assignedIssueCount > 0) {
			throw new IllegalArgumentException("Please contact Admin for delete user");
		}

		userRepo.delete(user);
	}
}
