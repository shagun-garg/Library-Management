package com.epam.library.repository;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.epam.library.dto.BookRequestDTO;
import com.epam.library.dto.BookResponseDTO;

import jakarta.validation.Valid;

@FeignClient(name = "books-service" , fallback = BookProxyImpl.class)
@LoadBalancerClient(name="books-service")
public interface BooksProxy {

	@PostMapping("books")
	public ResponseEntity<BookResponseDTO> createBook(@RequestBody @Valid BookRequestDTO bookDTO);
	
	@DeleteMapping("books/{id}")
	public ResponseEntity<Void> deleteBook(
			@PathVariable @Range(min = 1, message = "id should be greater than 1") int id);
	
	@GetMapping("books")
	public ResponseEntity<List<BookResponseDTO>> displayAllBook();
	
	@GetMapping("books/{id}")
	public ResponseEntity<BookResponseDTO> displayBookById(
			@PathVariable @Valid @Range(min = 1, message = "id should be greater than 1") int id);
	
	@PutMapping("books/{id}")
	public ResponseEntity<BookResponseDTO> modifyBookById(
			@PathVariable @Valid @Range(min = 1, message = "id should be greater than 1") int id,
			@RequestBody @Valid BookRequestDTO bookDTO);
}
