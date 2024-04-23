package com.epam.library.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class BookResponseDTO {

	@JsonInclude(value = Include.NON_DEFAULT)
	private int id;

	private String name;

	private String publisher;

	private String author;
	private String port;

	private String timeStamp;
	private String developerMessage;
	private HttpStatus httpStatus;

	public BookResponseDTO(int id, String name, String publisher, String author) {
		this.id = id;
		this.name = name;
		this.publisher = publisher;
		this.author = author;
	}

}
