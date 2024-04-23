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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = Include.NON_NULL)
public class LibraryDTO {

	@JsonInclude(value = Include.NON_DEFAULT)
	private int id;

	@NotBlank(message = "username cannot be Empty")
	private String username;

	@JsonInclude(value = Include.NON_DEFAULT)
	@NotBlank(message = "bookId cannot be Empty")
	private int bookId;

	private String timeStamp;
	private String developerMessage;
	private HttpStatus httpStatus;

	public LibraryDTO(int id, String username, int bookId) {
		this.id = id;
		this.username = username;
		this.bookId = bookId;
	}

}