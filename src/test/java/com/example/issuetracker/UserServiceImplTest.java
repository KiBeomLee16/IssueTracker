package com.example.issuetracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.issuetracker.dto.UpdateRequest.UserUpdateRequest;
import com.example.issuetracker.dto.request.UserCreateRequest;
import com.example.issuetracker.dto.response.UserResponse;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.entity.UserRole;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.UserRepository;
import com.example.issuetracker.serviceImpl.UserServiceImpl;
import com.example.issuetracker.repository.IssueRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	@Mock
	private UserRepository userRepo;

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private IssueRepository issueRepo;

	@Mock
	private PasswordEncoder passwordEncoder;

	private User user;

	@BeforeEach
	void setUp() {
		user = createUserEntity();

		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(user, "name", "John Doe");
		ReflectionTestUtils.setField(user, "email", "john@example.com");
	}

	@Test
	void createUser_success() {
		// given
		UserCreateRequest request = new UserCreateRequest();
		request.setName("John Doe");
		request.setEmail("john@example.com");
		request.setUserId("john01");
		request.setPassword("1234");

		when(userRepo.existsByEmail("john@example.com")).thenReturn(false);

		when(userRepo.existsByUserId("john01")).thenReturn(false);

		when(passwordEncoder.encode("1234")).thenReturn("encodedPassword");

		User savedUser = new User(

				"John Doe", "john@example.com", "john01", "encodedPassword", UserRole.USER);

		when(userRepo.save(any(User.class))).thenReturn(savedUser);

		// when
		UserResponse response = userService.createUser(request);

		// then

		assertEquals("John Doe", response.getName());
		assertEquals("john@example.com", response.getEmail());
		assertEquals("john01", response.getUserId());

		verify(passwordEncoder).encode("1234");
		verify(userRepo).save(any(User.class));
	}

	@Test
	void getUsers_success() {
		// given
		when(userRepo.findAll()).thenReturn(List.of(user));

		// when
		List<UserResponse> response = userService.getUsers();

		// then
		assertEquals(1, response.size());
		assertEquals(1L, response.get(0).getId());
		assertEquals("John Doe", response.get(0).getName());
		assertEquals("john@example.com", response.get(0).getEmail());

		verify(userRepo).findAll();
	}

	@Test
	void getUserById_success() {
		// given
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));

		// when
		UserResponse response = userService.getUser(1L);

		// then
		assertEquals(1L, response.getId());
		assertEquals("John Doe", response.getName());
		assertEquals("john@example.com", response.getEmail());

		verify(userRepo).findById(1L);
	}

	@Test
	void getUserById_notFound() {
		// given
		when(userRepo.findById(999L)).thenReturn(Optional.empty());

		// when & then
		assertThrows(ResourceNotFoundException.class, () -> {
			userService.getUser(999L);
		});

		verify(userRepo).findById(999L);
	}

	@Test
	void updateUser_success() {
		// given
		UserUpdateRequest request = new UserUpdateRequest();
		ReflectionTestUtils.setField(request, "name", "John Updated");
		ReflectionTestUtils.setField(request, "email", "john.updated@example.com");

		when(userRepo.findById(1L)).thenReturn(Optional.of(user));

		when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
			return invocation.getArgument(0);
		});

		// when
		UserResponse response = userService.updateUser(1L, request);

		// then
		assertEquals(1L, response.getId());
		assertEquals("John Updated", response.getName());
		assertEquals("john.updated@example.com", response.getEmail());

		verify(userRepo).findById(1L);
		verify(userRepo).save(any(User.class));
	}

	@Test
	void updateUser_notFound() {
		// given
		UserUpdateRequest request = new UserUpdateRequest();
		ReflectionTestUtils.setField(request, "name", "John Updated");
		ReflectionTestUtils.setField(request, "email", "john.updated@example.com");

		when(userRepo.findById(999L)).thenReturn(Optional.empty());

		// when & then
		assertThrows(ResourceNotFoundException.class, () -> {
			userService.updateUser(999L, request);
		});

		verify(userRepo).findById(999L);
	}

	@Test
	void deleteUser_success() {
		// given
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));
		doNothing().when(userRepo).delete(user);

		// when
		userService.deleteUser(1L);

		// then
		verify(userRepo).findById(1L);
		verify(userRepo).delete(user);
	}

	@Test
	void deleteUser_notFound() {
		// given
		when(userRepo.findById(999L)).thenReturn(Optional.empty());

		// when & then
		assertThrows(ResourceNotFoundException.class, () -> {
			userService.deleteUser(999L);
		});

		verify(userRepo).findById(999L);
	}

	private User createUserEntity() {
		try {
			Constructor<User> constructor = User.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}