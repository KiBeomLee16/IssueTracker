package com.example.issuetracker.security;

import com.example.issuetracker.entity.User;
import com.example.issuetracker.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

	private final User user;

	public CustomUserDetails(User user) {
		this.user = user;
	}

	public Long getId() {
		return user.getId();
	}

	public String getEmail() {
		return user.getEmail();
	}

	public String getUserId() {
		return user.getUserId();
	}

	public User getUser() {
		return user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		UserRole role = user.getRole() == null ? UserRole.USER : user.getRole();

		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public String getPassword() {
		return user.getPassword() == null ? "" : user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserId();
	}
}