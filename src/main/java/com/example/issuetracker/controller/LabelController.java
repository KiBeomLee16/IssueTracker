package com.example.issuetracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.issuetracker.dto.request.LabelCreateRequest;
import com.example.issuetracker.dto.response.LabelResponse;
import com.example.issuetracker.response.ApiResponse;
import com.example.issuetracker.service.LabelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LabelController {

	private final LabelService labelService;

	@PostMapping("/projects/{projectId}/labels")
	public ResponseEntity<ApiResponse<LabelResponse>> createLabel(@PathVariable Long projectId,
			@Valid @RequestBody LabelCreateRequest request) {
		LabelResponse response = labelService.createLabel(projectId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Label created successfully.", response));
	}

	@GetMapping("/projects/{projectId}/labels")
	public ResponseEntity<ApiResponse<List<LabelResponse>>> getLabelsByProject(@PathVariable Long projectId) {
		List<LabelResponse> response = labelService.getLabelsByProject(projectId);

		return ResponseEntity.ok(ApiResponse.success("Labels retrieved successfully.", response));
	}

	@DeleteMapping("/labels/{labelId}")
	public ResponseEntity<ApiResponse<Void>> deleteLabel(@PathVariable Long labelId) {
		labelService.deleteLabel(labelId);

		return ResponseEntity.ok(ApiResponse.success("Label deleted successfully."));
	}
}
