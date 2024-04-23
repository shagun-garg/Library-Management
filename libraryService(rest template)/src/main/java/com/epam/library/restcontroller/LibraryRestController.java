package com.epam.library.restcontroller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.client.RestTemplate;

import com.epam.library.dto.BookDTO;
import com.epam.library.dto.LibraryDTO;
import com.epam.library.dto.UserDTO;
import com.epam.library.exceptions.LibraryException;
import com.epam.library.service.LibraryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/library")
public class LibraryRestController {

	@Autowired
	LibraryService libraryService;

	@Autowired
	RestTemplate restTemplate;

	@GetMapping("/books/{id}")
	public ResponseEntity<BookDTO> getBookById(@PathVariable @Valid @NotBlank(message = "id cannot be empty") int id) {
		String url = "http://localhost:8001/books/{id}";
		return restTemplate.getForEntity(url, BookDTO.class, id);
	}

	@PostMapping("/books")
	public ResponseEntity<BookDTO> addBook(@RequestBody @Valid BookDTO bookDTO) {
		String url = "http://localhost:8001/books";
		return restTemplate.postForEntity(url, bookDTO, BookDTO.class);
	}

	@PutMapping("/books/{id}")
	public ResponseEntity<BookDTO> modifyBook(@PathVariable @Valid @NotBlank(message = "id cannot be empty") int id,
			@RequestBody @Valid BookDTO bookDTO) {
		String url = "http://localhost:8001/books/" + id;
		return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(bookDTO), BookDTO.class);
	}

	@DeleteMapping("/books/{bookId}") //
	public ResponseEntity<Void> deleteBookById(
			@PathVariable @Valid @NotBlank(message = "bookId cannot be empty") int bookId) {
		libraryService.deleteBookFromLibrary(bookId);
		String url = "http://localhost:8001/books/{bookId}";
		restTemplate.delete(url, bookId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/books")
	public ResponseEntity<List<BookDTO>> getAllBooks() {
		String url = "http://localhost:8001/books";
		return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<BookDTO>>() {
		});
	}

	@GetMapping("/users/{username}") //
	public ResponseEntity<List<?>> getUserByUsername(
			@PathVariable @Valid @NotBlank(message = "username cannot be empty") String username) {
		String url = "http://localhost:8002/users/{username}";
		List<Integer> bookIdList = libraryService.getAllBooksByUser(username);
		List<BookDTO> bookDTOList = getAllBooks().getBody().stream().filter(book -> bookIdList.contains(book.getId()))
				.toList();
		ResponseEntity<UserDTO> responseEntity = restTemplate.getForEntity(url, UserDTO.class, username);
		return new ResponseEntity<>(List.of(responseEntity.getBody(), bookDTOList), HttpStatus.OK);
	}

	@PostMapping("/users")
	public ResponseEntity<UserDTO> addUser(@RequestBody @Valid UserDTO userDto) {
		String url = "http://localhost:8002/users";
		return restTemplate.postForEntity(url, userDto, UserDTO.class);
	}

	@PutMapping("/users/{username}")
	public ResponseEntity<UserDTO> modifyUsers(
			@PathVariable @Valid @NotBlank(message = "username cannot be empty") String username,
			@RequestBody @Valid UserDTO userDTO) {
		String url = "http://localhost:8002/users/" + username;
		return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(userDTO), UserDTO.class);
	}

	@DeleteMapping("/users/{username}")
	public ResponseEntity<Void> deleteUserByUsername(
			@PathVariable @Valid @NotBlank(message = "username invalid") String username) {
		libraryService.deleteUserFromLibrary(username);
		String url = "http://localhost:8002/users/{username}";
		restTemplate.delete(url, username);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		String url = "http://localhost:8002/users";
		return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<UserDTO>>() {
		});
	}

	@PostMapping("/users/{username}/books/{bookId}")
	public ResponseEntity<LibraryDTO> issueBookToUser(
			@PathVariable @Valid @NotBlank(message = "username cannot be blank") String username,
			@PathVariable @Valid @NotBlank(message = "bookId cannot be blank") int bookId) {
		Optional<BookDTO> optionalBookResponseEntity = Optional.of(getBookById(bookId).getBody());
		optionalBookResponseEntity.map(b -> {
			if (b.getId() !=0) {
				return b;
			}
			return null;
		}).orElseThrow(() -> new LibraryException("Book not present in database"));
		Optional<UserDTO> optionalUserDto = Optional.of((UserDTO) getUserByUsername(username).getBody().get(0));
		optionalUserDto.map(u -> {
			if (u.getUsername() != null)
				return u;
			return null;
		}).orElseThrow(() -> new LibraryException("User with username not present in database"));
		return new ResponseEntity<>(libraryService.issueNewBookToUser(username, bookId), HttpStatus.CREATED);
	}

	@DeleteMapping("/users/{username}/books/{bookId}")
	public ResponseEntity<Void> unIssueBooks(
			@PathVariable @Valid @NotBlank(message = "username cannot be blank") String username,
			@PathVariable @Valid @NotBlank(message = "bookId cannot be blank") int bookId) {
		libraryService.removeBookFromUser(username, bookId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
