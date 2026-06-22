package com.example.issuetracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.issuetracker.dto.response.ProjectResponse;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.serviceImpl.ProjectCacheService;

@ExtendWith(MockitoExtension.class)
class ProjectCacheServiceTest {

	@Mock
	private ProjectRepository projectRepository;

	@InjectMocks
	private ProjectCacheService projectCacheService;

	private Project project;

	@BeforeEach
	void setUp() {
		project = new Project("Issue Tracker", "Issue tracker project");
		ReflectionTestUtils.setField(project, "id", 1L);
	}

	@Test
	void getProject_success() {
		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

		ProjectResponse response = projectCacheService.getProject(1L);

		assertEquals(1L, response.getId());
		assertEquals("Issue Tracker", response.getName());
		verify(projectRepository).findById(1L);
	}

	@Test
	void getProject_notFound() {
		when(projectRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> projectCacheService.getProject(999L));
		verify(projectRepository).findById(999L);
	}
}
