package com.epam.library.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryDTO {

	private int id;

	@NotBlank(message = "username cannot be Empty")
	private String username;

	@NotBlank(message = "bookId cannot be Empty")
	private int bookId;

	public LibraryDTO(String username, int bookId) {
		this.username = username;
		this.bookId = bookId;
	}

}
