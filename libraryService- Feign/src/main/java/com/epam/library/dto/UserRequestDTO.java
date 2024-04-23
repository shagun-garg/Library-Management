package com.epam.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDTO {
	private int id;
	@NotBlank(message = "username cannot be empty")
	private String username;
	@NotBlank(message = "email cannot be empty")
	private String email;
	@NotBlank(message = "name cannot be empty")
	private String name;
	
	
	
}
