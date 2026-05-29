package com.example.issuetracker.security;

import com.example.issuetracker.entity.User;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public Long getCurrentUserId() {
        Authentication authentication = getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
        	 return userDetails.getId();
        }

        throw new IllegalStateException("Unsupported principal type.");
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Long userId = getCurrentUserId();

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public boolean isAdmin() {
        Authentication authentication = getAuthentication();

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("Unauthenticated user.");
        }

        return authentication;
    }
}