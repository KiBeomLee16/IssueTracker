package com.example.issuetracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.example.issuetracker.dto.response.LabelResponse;
import com.example.issuetracker.entity.Label;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.LabelRepository;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.serviceImpl.LabelServiceImpl;
import com.example.issuetracker.serviceImpl.ProjectAuthorizationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LabelServiceImplTest {

	@Mock
	private LabelRepository labelRepository;

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private ProjectAuthorizationService projectAuthorizationService;

	@InjectMocks
	private LabelServiceImpl labelService;

	private Project project;

	@BeforeEach
	void setUp() {
		project = new Project();
		ReflectionTestUtils.setField(project, "id", 1L);
	}

	@Test
	void getLabelsByProject_success() {
		// given
		Long projectId = 1L;

		Label label1 = new Label(project, "backend", "#2563eb");
		label1.setId(1L);

		Label label2 = new Label(project, "bug", "#dc2626");
		label2.setId(2L);

		when(labelRepository.findAllByProject_IdOrderByNameAsc(projectId))
				.thenReturn(List.of(label1, label2));

		// when
		List<LabelResponse> result = labelService.getLabelsByProject(projectId);

		// then
		assertEquals(2, result.size());

		assertEquals(1L, result.get(0).getId());
		assertEquals("backend", result.get(0).getName());
		assertEquals("#2563eb", result.get(0).getColor());

		assertEquals(2L, result.get(1).getId());
		assertEquals("bug", result.get(1).getName());
		assertEquals("#dc2626", result.get(1).getColor());

		verify(projectAuthorizationService).requireProjectMember(projectId);
		verify(labelRepository).findAllByProject_IdOrderByNameAsc(projectId);
	}

	@Test
	void deleteLabel_success() {
		// given
		Long labelId = 1L;
		Long projectId = 1L;

		Label label = new Label(project, "bug", "#dc2626");
		label.setId(labelId);

		when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));

		// when
		labelService.deleteLabel(labelId);

		// then
		verify(labelRepository).findById(labelId);
		verify(projectAuthorizationService).requireProjectOwner(projectId);
		verify(labelRepository).delete(label);
	}

	@Test
	void deleteLabel_fail_whenLabelNotFound() {
		// given
		Long labelId = 999L;

		when(labelRepository.findById(labelId)).thenReturn(Optional.empty());

		// when & then
		ResourceNotFoundException exception = assertThrows(
				ResourceNotFoundException.class,
				() -> labelService.deleteLabel(labelId)
		);

		assertEquals("Label not found. id=" + labelId, exception.getMessage());

		verify(labelRepository).findById(labelId);
		verify(projectAuthorizationService, never()).requireProjectOwner(anyLong());
		verify(labelRepository, never()).delete(any(Label.class));
	}
}