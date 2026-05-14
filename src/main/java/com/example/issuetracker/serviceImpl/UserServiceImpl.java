package com.example.issuetracker.serviceImpl;


import com.example.issuetracker.dto.UserCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.UserUpdateRequest;
import com.example.issuetracker.dto.response.UserResponse;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.repository.UserRepository;
import com.example.issuetracker.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private IssueRepository issueRepo;

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepo.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 ID입니다.");
        }

        User user = new User(request.getName(), request.getEmail(), request.getUserId());

        User savedUser = userRepo.save(user);

        return UserResponse.getUserResponse(savedUser);
    }

    @Override
    public List<UserResponse> getUsers() {
        return userRepo.findAll()
                .stream()
                .map(UserResponse::getUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found. id=" + id));

        return UserResponse.getUserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found. id=" + id));

        userRepo.findByEmail(request.getEmail()).ifPresent(foundUser -> {
            if (!foundUser.getId().equals(id)) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        });

        userRepo.findByUserId(request.getUserId()).ifPresent(foundUser -> {
            if (!foundUser.getId().equals(id)) {
                throw new IllegalArgumentException("이미 사용 중인 사용자 ID입니다.");
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
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found. id=" + id));

        long assignedIssueCount = issueRepo.countByAssignee_Id(id);

        if (assignedIssueCount > 0) {
            throw new IllegalArgumentException("담당자로 지정된 이슈가 있어 사용자를 삭제할 수 없습니다.");
        }

        userRepo.delete(user);
    }
}
