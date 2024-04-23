package com.epam.library.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.epam.library.dto.BookRequestDTO;
import com.epam.library.dto.BookResponseDTO;
import com.epam.library.dto.LibraryDTO;
import com.epam.library.dto.UserRequestDTO;
import com.epam.library.dto.UserResponseDTO;
import com.epam.library.repository.BooksProxy;
import com.epam.library.repository.UserProxy;
import com.epam.library.service.LibraryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/library")
public class LibraryRestController {

	@Autowired
	LibraryService libraryService;

	@Autowired
	BooksProxy booksProxy;

	@Autowired
	UserProxy userProxy;

	@GetMapping("/books/{id}")
	public ResponseEntity<BookResponseDTO> getBookById(@PathVariable @Valid @NotBlank(message = "id cannot be empty") int id) {
		log.info("Get request to getBookById controller");
		return booksProxy.displayBookById(id);
	}

	@PostMapping("/books")
	public ResponseEntity<BookResponseDTO> addBook(@RequestBody @Valid BookRequestDTO bookDTO) {
		log.info("Post request to addBook controller");
		return booksProxy.createBook(bookDTO);
	}

	@PutMapping("/books/{id}")
	public ResponseEntity<BookResponseDTO> modifyBook(@PathVariable @Valid @NotBlank(message = "id cannot be empty") int id,
			@RequestBody @Valid BookRequestDTO bookDTO) {
		log.info("Put request to modifyBook controller");
		return booksProxy.modifyBookById(id, bookDTO);
	}

	@DeleteMapping("/books/{bookId}")
	public ResponseEntity<Void> deleteBookById(
			@PathVariable @Valid @NotBlank(message = "bookId cannot be empty") int bookId) {
		log.info("Delete request to deleteBookById controller");
		libraryService.deleteBookFromLibrary(bookId);
		booksProxy.deleteBook(bookId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/books")
	public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
		log.info("Get request to getAllBooks controller");
		return booksProxy.displayAllBook();
	}

	@GetMapping("/users/{username}")
	public ResponseEntity<List<Object>> getUserByUsername(
			@PathVariable @Valid @NotBlank(message = "username cannot be empty") String username) {
		log.info("Get request to getUserByUsername controller");
		return new ResponseEntity<>(List.of(userProxy.getUserByUsername(username).getBody(),
				libraryService.getAllBooksAssociatedWithUser(username)), HttpStatus.OK);
	}

	@PostMapping("/users")
	public ResponseEntity<UserResponseDTO> addUser(@RequestBody @Valid UserRequestDTO userDto) {
		log.info("Post request to addUser controller");
		return userProxy.addUser(userDto);
	}

	@PutMapping("/users/{username}")
	public ResponseEntity<UserResponseDTO> modifyUsers(
			@PathVariable @Valid @NotBlank(message = "username cannot be empty") String username,
			@RequestBody @Valid UserRequestDTO userDTO) {
		log.info("Put request to modifyUser controller");
		return userProxy.updateUser(username, userDTO);
	}

	@DeleteMapping("/users/{username}")
	public ResponseEntity<Void> deleteUserByUsername(
			@PathVariable @Valid @NotBlank(message = "username invalid") String username) {
		log.info("Delete request to deleteUserByUsername controller");
		libraryService.deleteUserFromLibrary(username);
		userProxy.deleteUser(username);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		log.info("Get request to getAllUsers controller");
		return userProxy.getAllUsers();
	}

	@PostMapping("/users/{username}/books/{bookId}")
	public ResponseEntity<LibraryDTO> issueBookToUser(
			@PathVariable @Valid @NotBlank(message = "username cannot be blank") String username,
			@PathVariable @Valid @NotBlank(message = "bookId cannot be blank") int bookId) {
		log.info("Post request to issueBookToUser controller");
		LibraryDTO libraryDTO = libraryService.issueNewBookToUser(username, bookId);
		return new ResponseEntity<>(libraryDTO, libraryDTO.getDeveloperMessage() == null ? HttpStatus.CREATED : HttpStatus.OK);
	}

	@DeleteMapping("/users/{username}/books/{bookId}")
	public ResponseEntity<Void> unIssueBooks(
			@PathVariable @Valid @NotBlank(message = "username cannot be blank") String username,
			@PathVariable @Valid @NotBlank(message = "bookId cannot be blank") int bookId) {
		log.info("Delete request to unIssueBooks controller");
		libraryService.withdrawBookFromUser(username, bookId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
