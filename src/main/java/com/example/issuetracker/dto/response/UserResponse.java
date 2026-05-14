package com.example.issuetracker.dto.response;

import java.time.LocalDateTime;

import com.example.issuetracker.entity.User;

import lombok.Getter;


@Getter
public class UserResponse {
	
	    private Long id;
	    private String name;
	    private String email;
	    private String userId;
	    private LocalDateTime createdAt;
	    private LocalDateTime updatedAt;
	
	    
	    
	    
	public UserResponse(User user) {
			super();
			this.id = user.getId();
			this.name = user.getName();
			this.email = user.getEmail();
			this.userId = user.getUserId();
			this.createdAt = user.getCreatedAt();
			this.updatedAt = user.getUpdatedAt();
		}




	public static UserResponse getUserResponse(User user){
		return new UserResponse(user);
	}
}
