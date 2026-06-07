package com.example.issuetracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LabelCreateRequest {

	@NotBlank
	@Size(max = 50)
	private String name;

	@NotBlank
	@Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a hex color like #2563eb.")
	private String color;
}
