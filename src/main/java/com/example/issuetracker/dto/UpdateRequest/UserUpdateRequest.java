package com.example.issuetracker.dto.UpdateRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank(message = "Name is Required.")
    private String name;

    @NotBlank(message = "Email is Required.")
    @Email(message = "Please check your Email format.")
    private String email;
    
    @NotBlank(message = "User Id is Required.")
    private String userId;
}