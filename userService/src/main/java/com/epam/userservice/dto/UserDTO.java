package com.epam.userservice.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(value = Include.NON_NULL)
public class UserDTO {
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

	public UserDTO(int id, String username, String email, String name) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.name = name;
	}

}
