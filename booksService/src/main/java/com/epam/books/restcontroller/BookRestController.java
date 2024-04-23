package com.epam.books.restcontroller;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.books.dto.BookDTO;
import com.epam.books.service.BookService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("books")
public class BookRestController {

	@Autowired
	BookService bookService;
	
//	@Autowired
//	Environment env;

	@PostMapping
	public ResponseEntity<BookDTO> createBook(@RequestBody @Valid BookDTO bookDTO) {
		log.info("inside createBook controller with book : {}", bookDTO);
		return new ResponseEntity<>(bookService.addBook(bookDTO), HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(
			@PathVariable @Range(min = 1, message = "id should be greater than 1") int id) {
		log.info("inside deleteBook controller with book id: {}", id);
		bookService.removeBook(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping
	public ResponseEntity<List<BookDTO>> displayAllBook() {
		log.info("inside displayAllBook controller");
		return new ResponseEntity<>(bookService.viewAllBooks(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<BookDTO> displayBookById(
			@PathVariable @Valid @Range(min = 1, message = "id should be greater than 1") int id) {
		log.info("inside displayBookById controller with id: {}", id);
		BookDTO book=bookService.viewById(id);
//		book.setPort(env.getProperty("local.server.port"));
		return new ResponseEntity<>(book, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<BookDTO> modifyBookById(
			@PathVariable @Valid @Range(min = 1, message = "id should be greater than 1") int id,
			@RequestBody @Valid BookDTO bookDTO) {
		log.info("inside modifyBookById controller with id : {} and Book : {}", id, bookDTO);
		return new ResponseEntity<>(bookService.modifyBook(id, bookDTO), HttpStatus.OK);
	}
}
