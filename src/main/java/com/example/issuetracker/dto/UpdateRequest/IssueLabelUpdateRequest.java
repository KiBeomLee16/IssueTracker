package com.example.issuetracker.dto.UpdateRequest;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IssueLabelUpdateRequest {

	@NotNull
	private Set<Long> labelIds = new LinkedHashSet<>();
}
