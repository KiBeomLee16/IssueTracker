package com.example.issuetracker.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.issuetracker.controller.UserController;
import com.example.issuetracker.dto.UserCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.UserUpdateRequest;
import com.example.issuetracker.dto.response.UserResponse;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_success() throws Exception {
        // given
        String requestBody = """
                {
                  "name": "John Doe",
                  "email": "john@example.com",
                  "userId": "john01"
                }
                """;

        UserResponse response = createUserResponse(
                1L,
                "John Doe",
                "john@example.com",
                "john01"
        );

        when(userService.createUser(any(UserCreateRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User created successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"))
                .andExpect(jsonPath("$.data.userId").value("john01"));

        verify(userService).createUser(any(UserCreateRequest.class));
    }

    @Test
    void getUsers_success() throws Exception {
        // given
        UserResponse user1 = createUserResponse(
                1L,
                "John Doe",
                "john@example.com",
                "john01"
        );

        UserResponse user2 = createUserResponse(
                2L,
                "Jane Smith",
                "jane@example.com",
                "jane01"
        );

        when(userService.getUsers())
                .thenReturn(List.of(user1, user2));

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully."))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("John Doe"))
                .andExpect(jsonPath("$.data[0].email").value("john@example.com"))
                .andExpect(jsonPath("$.data[0].userId").value("john01"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$.data[1].email").value("jane@example.com"))
                .andExpect(jsonPath("$.data[1].userId").value("jane01"));

        verify(userService).getUsers();
    }

    @Test
    void getUser_success() throws Exception {
        // given
        UserResponse response = createUserResponse(
                1L,
                "John Doe",
                "john@example.com",
                "john01"
        );

        when(userService.getUser(1L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User retrieved successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"))
                .andExpect(jsonPath("$.data.userId").value("john01"));

        verify(userService).getUser(1L);
    }

    @Test
    void updateUser_success() throws Exception {
        // given
        String requestBody = """
                {
                  "name": "Updated User",
                  "email": "updated@example.com",
                  "userId": "updated01"
                }
                """;

        UserResponse response = createUserResponse(
                1L,
                "Updated User",
                "updated@example.com",
                "updated01"
        );

        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User updated successfully."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Updated User"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                .andExpect(jsonPath("$.data.userId").value("updated01"));

        verify(userService).updateUser(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    void deleteUser_success() throws Exception {
        // given
        doNothing().when(userService).deleteUser(1L);

        // when & then
        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully."));

        verify(userService).deleteUser(1L);
    }

    private UserResponse createUserResponse(
            Long id,
            String name,
            String email,
            String userId
    ) {
        User user = new User();

        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "name", name);
        ReflectionTestUtils.setField(user, "email", email);
        ReflectionTestUtils.setField(user, "userId", userId);

        return new UserResponse(user);
    }
}