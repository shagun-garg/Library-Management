package com.epam.books.dto;

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
public class BookDTO {

	private int id;

	@NotBlank(message = "Book name cannot be Empty")
	private String name;

	@NotBlank(message = "Publisher name cannot be Empty")
	private String publisher;

	@NotBlank(message = "Author name cannot be Empty")
	private String author;

	private String timeStamp;
	private String developerMessage;
	private HttpStatus httpStatus;
//	private String port;
	
	public BookDTO(int id, String name, String publisher, String author) {
		this.id = id;
		this.name = name;
		this.publisher = publisher;
		this.author = author;
	}

}
