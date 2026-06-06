package com.example.issuetracker.security;

import com.example.issuetracker.entity.User;
import com.example.issuetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		User user = userRepo.findByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User not found. userId=" + userId));

		return new CustomUserDetails(user);
	}
}