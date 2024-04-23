package com.epam.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDTO {
	
	private int id;
	
	@NotBlank(message = "Book name cannot be Empty")
	private String name;

	@NotBlank(message = "Publisher name cannot be Empty")
	private String publisher;
	
	@NotBlank(message = "Author name cannot be Empty")
	private String author;

}
