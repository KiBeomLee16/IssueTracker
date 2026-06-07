package com.example.issuetracker.dto.response;

import com.example.issuetracker.entity.Label;

import lombok.Getter;

@Getter
public class LabelResponse {

	private final Long id;
	private final Long projectId;
	private final String name;
	private final String color;

	public LabelResponse(Label label) {
		this.id = label.getId();
		this.projectId = label.getProject().getId();
		this.name = label.getName();
		this.color = label.getColor();
	}
}
