package com.epam.library.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class UserResponseDTO {
	
	@JsonInclude(value = Include.NON_DEFAULT)
	private int id;
	@NotBlank(message = "username cannot be empty")
	private String username;
	@NotBlank(message = "email cannot be empty")
	private String email;
	@NotBlank(message = "name cannot be empty")
	private String name;

	private String timeStamp;
	private String developerMessage;
	private HttpStatus httpStatus;

	public UserResponseDTO(int id, String username, String email, String name) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.name = name;
	}

}
