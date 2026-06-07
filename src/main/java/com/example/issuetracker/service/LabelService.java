package com.example.issuetracker.service;

import java.util.List;

import com.example.issuetracker.dto.request.LabelCreateRequest;
import com.example.issuetracker.dto.response.LabelResponse;

public interface LabelService {

	LabelResponse createLabel(Long projectId, LabelCreateRequest request);

	List<LabelResponse> getLabelsByProject(Long projectId);

	void deleteLabel(Long labelId);
}
