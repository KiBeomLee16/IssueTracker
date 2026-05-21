	package com.example.issuetracker;
	
	import static org.junit.jupiter.api.Assertions.assertEquals;
	import static org.junit.jupiter.api.Assertions.assertThrows;
	import static org.mockito.ArgumentMatchers.any;
	import static org.mockito.Mockito.doNothing;
	import static org.mockito.Mockito.verify;
	import static org.mockito.Mockito.when;
	
	import java.util.List;
	import java.util.Optional;
	
	import org.junit.jupiter.api.BeforeEach;
	import org.junit.jupiter.api.Test;
	import org.junit.jupiter.api.extension.ExtendWith;
	import org.mockito.InjectMocks;
	import org.mockito.Mock;
	import org.mockito.junit.jupiter.MockitoExtension;
	import org.springframework.test.util.ReflectionTestUtils;

import com.example.issuetracker.dto.UpdateRequest.ProjectUpdateRequest;
import com.example.issuetracker.dto.request.ProjectCreateRequest;
import com.example.issuetracker.dto.response.ProjectResponse;
	import com.example.issuetracker.entity.Project;
	import com.example.issuetracker.exception.ResourceNotFoundException;
	import com.example.issuetracker.repository.ProjectRepository;
	import com.example.issuetracker.serviceImpl.ProjectServiceImpl;
	
	@ExtendWith(MockitoExtension.class)
	public class ProjectServiceImplTest {
	
	    @Mock
	    private ProjectRepository projectRepo;
	
	    @InjectMocks
	    private ProjectServiceImpl projectService;
	
	    private Project project;
	
	    @BeforeEach
	    void setUp() {
	        project = new Project();
	
	        ReflectionTestUtils.setField(project, "id", 1L);
	        ReflectionTestUtils.setField(project, "name", "Issue Tracker");
	        ReflectionTestUtils.setField(project, "description", "Issue tracker project");
	    }
	
	    @Test
	    void createProject_success() {
	        // given
	        ProjectCreateRequest request = new ProjectCreateRequest();
	        request.setName("Issue Tracker");
	        request.setDescription("Issue tracker project");
	
	        when(projectRepo.save(any(Project.class))).thenAnswer(invocation -> {
	            Project savedProject = invocation.getArgument(0);
	
	            ReflectionTestUtils.setField(savedProject, "id", 1L);
	
	            return savedProject;
	        });
	
	        // when
	        ProjectResponse response = projectService.createProject(request);
	
	        // then
	        assertEquals(1L, response.getId());
	        assertEquals("Issue Tracker", response.getName());
	        assertEquals("Issue tracker project", response.getDescription());
	
	        verify(projectRepo).save(any(Project.class));
	    }
	
	    @Test
	    void getProjects_success() {
	        // given
	        when(projectRepo.findAll()).thenReturn(List.of(project));
	
	        // when
	        List<ProjectResponse> response = projectService.getProjects();
	
	        // then
	        assertEquals(1, response.size());
	        assertEquals(1L, response.get(0).getId());
	        assertEquals("Issue Tracker", response.get(0).getName());
	
	        verify(projectRepo).findAll();
	    }
	
	    @Test
	    void getProjectById_success() {
	        // given
	        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
	
	        // when
	        ProjectResponse response = projectService.getProject(1L);
	
	        // then
	        assertEquals(1L, response.getId());
	        assertEquals("Issue Tracker", response.getName());
	        assertEquals("Issue tracker project", response.getDescription());
	
	        verify(projectRepo).findById(1L);
	    }
	
	    @Test
	    void getProjectById_notFound() {
	        // given
	        when(projectRepo.findById(999L)).thenReturn(Optional.empty());
	
	        // when & then
	        assertThrows(ResourceNotFoundException.class, () -> {
	            projectService.getProject(999L);
	        });
	
	        verify(projectRepo).findById(999L);
	    }
	
	    @Test
	    void updateProject_success() {
	        // given
	        ProjectUpdateRequest request = new ProjectUpdateRequest();
	        request.setName("Updated Project");
	        request.setDescription("Updated description");
	
	        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
	
	        when(projectRepo.save(any(Project.class))).thenAnswer(invocation -> {
	            return invocation.getArgument(0);
	        });
	
	        // when
	        ProjectResponse response = projectService.updateProject(1L, request);
	
	        // then
	        assertEquals(1L, response.getId());
	        assertEquals("Updated Project", response.getName());
	        assertEquals("Updated description", response.getDescription());
	
	        verify(projectRepo).findById(1L);
	        verify(projectRepo).save(any(Project.class));
	    }
	
	    @Test
	    void updateProject_notFound() {
	        // given
	        ProjectUpdateRequest request = new ProjectUpdateRequest();
	        request.setName("Updated Project");
	        request.setDescription("Updated description");
	
	        when(projectRepo.findById(999L)).thenReturn(Optional.empty());
	
	        // when & then
	        assertThrows(ResourceNotFoundException.class, () -> {
	            projectService.updateProject(999L, request);
	        });
	
	        verify(projectRepo).findById(999L);
	    }
	
	    @Test
	    void deleteProject_success() {
	        // given
	        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
	        doNothing().when(projectRepo).delete(project);
	
	        // when
	        projectService.deleteProject(1L);
	
	        // then
	        verify(projectRepo).findById(1L);
	        verify(projectRepo).delete(project);
	    }
	
	    @Test
	    void deleteProject_notFound() {
	        // given
	        when(projectRepo.findById(999L)).thenReturn(Optional.empty());
	
	        // when & then
	        assertThrows(ResourceNotFoundException.class, () -> {
	            projectService.deleteProject(999L);
	        });
	
	        verify(projectRepo).findById(999L);
	    }
	}