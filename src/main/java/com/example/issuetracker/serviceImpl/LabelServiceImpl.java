package com.example.issuetracker.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.issuetracker.dto.request.LabelCreateRequest;
import com.example.issuetracker.dto.response.LabelResponse;
import com.example.issuetracker.entity.Label;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.LabelRepository;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.service.LabelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabelServiceImpl implements LabelService {

	private final LabelRepository labelRepository;
	private final ProjectRepository projectRepository;
	private final ProjectAuthorizationService projectAuthorizationService;

	@Override
	@Transactional
	public LabelResponse createLabel(Long projectId, LabelCreateRequest request) {
		projectAuthorizationService.requireProjectOwner(projectId);
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));
		String name = request.getName().trim();

		if (labelRepository.existsByProject_IdAndName(projectId, name)) {
			throw new IllegalArgumentException("Label name already exists in this project.");
		}

		Label label = labelRepository.save(new Label(project, name, request.getColor()));
		return new LabelResponse(label);
	}

	@Override
	public List<LabelResponse> getLabelsByProject(Long projectId) {
		projectAuthorizationService.requireProjectMember(projectId);
		return labelRepository.findAllByProject_IdOrderByNameAsc(projectId).stream().map(LabelResponse::new).toList();
	}

	@Override
	@Transactional
	public void deleteLabel(Long labelId) {
		Label label = labelRepository.findById(labelId)
				.orElseThrow(() -> new ResourceNotFoundException("Label not found. id=" + labelId));
		projectAuthorizationService.requireProjectOwner(label.getProject().getId());
		labelRepository.delete(label);
	}
}
