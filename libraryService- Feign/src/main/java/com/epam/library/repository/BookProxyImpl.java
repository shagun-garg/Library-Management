package com.epam.library.repository;

import java.util.Arrays;
import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.epam.library.dto.BookRequestDTO;
import com.epam.library.dto.BookResponseDTO;

import jakarta.validation.Valid;

@Service
public class BookProxyImpl implements BooksProxy{

	private static final String SERVICE_DOWN="Service is Down";
	
	@Override
	public ResponseEntity<BookResponseDTO> createBook(@Valid BookRequestDTO bookDTO) {
		BookResponseDTO book= BookResponseDTO.builder().developerMessage(SERVICE_DOWN).build();
		return new ResponseEntity<>(book,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> deleteBook(@Range(min = 1, message = "id should be greater than 1") int id) {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<List<BookResponseDTO>> displayAllBook() {
		BookResponseDTO book= BookResponseDTO.builder().developerMessage(SERVICE_DOWN).build();
		return new ResponseEntity<>(Arrays.asList(book),HttpStatus.OK);
	}

	@Override
	public ResponseEntity<BookResponseDTO> displayBookById(
			@Valid @Range(min = 1, message = "id should be greater than 1") int id) {
		BookResponseDTO book= BookResponseDTO.builder().developerMessage(SERVICE_DOWN).build();
		return new ResponseEntity<>(book,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<BookResponseDTO> modifyBookById(
			@Valid @Range(min = 1, message = "id should be greater than 1") int id, @Valid BookRequestDTO bookDTO) {
		BookResponseDTO book= BookResponseDTO.builder().developerMessage(SERVICE_DOWN).build();
		return new ResponseEntity<>(book,HttpStatus.OK);
	}

}
