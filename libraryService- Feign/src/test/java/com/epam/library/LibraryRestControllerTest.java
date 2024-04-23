package com.epam.library;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.epam.library.dto.BookRequestDTO;
import com.epam.library.dto.BookResponseDTO;
import com.epam.library.dto.LibraryDTO;
import com.epam.library.dto.UserRequestDTO;
import com.epam.library.dto.UserResponseDTO;
import com.epam.library.exceptions.LibraryException;
import com.epam.library.repository.BooksProxy;
import com.epam.library.repository.UserProxy;
import com.epam.library.restcontroller.LibraryRestController;
import com.epam.library.service.LibraryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LibraryRestController.class)
class LibraryRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LibraryService libraryService;

	@MockBean
	BooksProxy bookClient;

	@MockBean
	UserProxy userClient;

	@Test
	void testGetBookById() throws Exception {
		BookRequestDTO book = BookRequestDTO.builder().id(1).name("Book1").author("Author1").publisher("Publisher1").build();
		BookResponseDTO bookRes = BookResponseDTO.builder().id(1).name("Book1").author("Author1").publisher("Publisher1").build();
		when(bookClient.displayBookById(1)).thenReturn(ResponseEntity.ok(bookRes));
		mockMvc.perform(get("/library/books/1", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(book.getName())))
				.andExpect(jsonPath("$.publisher", is(book.getPublisher())))
				.andExpect(jsonPath("$.author", is(book.getAuthor()))).andDo(print());
	}

	@Test
	void testAddBook() throws Exception {
		BookRequestDTO bookDTO = BookRequestDTO.builder().name("Book 1").publisher("Publisher 1").author("Author 1").build();
		BookResponseDTO bookRes = BookResponseDTO.builder().id(1).name("Book 1").author("Author 1").publisher("Publisher 1").build();
		when(bookClient.createBook(bookDTO)).thenReturn(ResponseEntity.ok(bookRes));

		mockMvc.perform(post("/library/books").contentType(MediaType.APPLICATION_JSON).content( new ObjectMapper().writeValueAsString(bookDTO)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name", is(bookDTO.getName())))
				.andExpect(jsonPath("$.publisher", is(bookDTO.getPublisher())))
				.andExpect(jsonPath("$.author", is(bookDTO.getAuthor())));
	}

	@Test
	void testDeleteBookById() throws Exception {
		int bookId = 1;
		doNothing().when(libraryService).deleteBookFromLibrary(bookId);
		mockMvc.perform(delete("/library/books/{bookId}", bookId)).andExpect(status().isNoContent());
	}

	@Test
	void testGetAllBooks() throws Exception {
		BookResponseDTO bookRes = BookResponseDTO.builder().id(1).name("Book1").author("Author1").publisher("Publisher1").build();
		List<BookResponseDTO> bookDTOList = Arrays.asList(bookRes);

		Mockito.when(bookClient.displayAllBook()).thenReturn(new ResponseEntity<>(bookDTOList, HttpStatus.OK));

		mockMvc.perform(get("/library/books")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(bookDTOList.size()))
				.andExpect(jsonPath("$.[0].name").value(bookDTOList.get(0).getName()));
	}

	@Test
	void testGetUserByUsername() throws Exception {
		String username = "user1";
		BookResponseDTO book1 = BookResponseDTO.builder().id(1).name("Book1").author("Author1").publisher("Publisher1").build();
		List<BookResponseDTO> bookDTOList = Arrays.asList(book1);
		Mockito.when(userClient.getUserByUsername(username)).thenReturn(
				new ResponseEntity<>(new UserResponseDTO(1, "user1", "user1@example.com", "User One"), HttpStatus.OK));
		Mockito.when(libraryService.getAllBooksAssociatedWithUser(username)).thenReturn(bookDTOList);
		mockMvc.perform(get("/library/users/{username}", username)).andExpect(status().isOk())
				.andExpect(jsonPath("$.[0].username").value(username))
				.andExpect(jsonPath("$.[1].[0].name").value(bookDTOList.get(0).getName()));
	}

	@Test
	void testIssueBooks() throws Exception {
		int bookId = 123;
		String username = "john.doe";
		LibraryDTO libraryDTO = LibraryDTO.builder().username(username).bookId(bookId).build();
		when(libraryService.issueNewBookToUser(username, bookId)).thenReturn(libraryDTO);
		mockMvc.perform(post("/library/users/{username}/books/{bookId}", username, bookId))
				.andExpect(status().isCreated());
		verify(libraryService, times(1)).issueNewBookToUser(anyString(), anyInt());
	}

	@Test
	void testAddUse() throws Exception {
		UserRequestDTO userDTO = new UserRequestDTO(1, "user1", "user1@example.com", "User One");
		UserResponseDTO userResponseDTO = new UserResponseDTO(1, "user1", "user1@example.com", "User One");
		Mockito.when(userClient.addUser(userDTO)).thenReturn(new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED));
		mockMvc.perform(post("/library/users").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userDTO)))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	void testDeleteUserByUsername() throws Exception {
		String username = "testuser";

		mockMvc.perform(delete("/library/users/" + username).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void testGetAllUsers() throws Exception {
		UserRequestDTO userDTO1 = new UserRequestDTO(1, "testuser1", "testuser1@example.com", "Test User 1");
		UserResponseDTO userDTO = new UserResponseDTO(1, "testuser1", "testuser1@example.com", "Test User 1");
		List<UserResponseDTO> userDTOList = List.of(userDTO);
		when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok(userDTOList));
		mockMvc.perform(get("/library/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.[0].username", is(userDTO1.getUsername())))
				.andExpect(jsonPath("$.[0].email", is(userDTO1.getEmail())))
				.andExpect(jsonPath("$.[0].name", is(userDTO1.getName())));
	}

	@Test
	void testUnIssueBooks() throws Exception {
		String username = "testuser";
		int bookId = 123;
		doNothing().when(libraryService).withdrawBookFromUser(username, bookId);
		mockMvc.perform(delete("/library/users/{username}/books/{bookId}", username, bookId)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
	}

	@Test
	void testUnIssueBooksThorw() throws Exception {
		String username = "testuser";
		int bookId = 123;
		doThrow(RuntimeException.class).when(libraryService).withdrawBookFromUser(username, bookId);
		mockMvc.perform(delete("/library/users/{username}/books/{bookId}", username, bookId)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
	}

	@Test
	void testUnIssueBooksThorwLibrary() throws Exception {
		String username = "testuser";
		int bookId = 123;
		doThrow(LibraryException.class).when(libraryService).withdrawBookFromUser(username, bookId);
		mockMvc.perform(delete("/library/users/{username}/books/{bookId}", username, bookId)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	void modifyBook_ShouldReturnOk() throws Exception {
		int bookId = 1;
		BookRequestDTO bookDTO = new BookRequestDTO(1, "Book Name", "Publisher", "Author");
		BookResponseDTO bookDTORes = new BookResponseDTO(1, "Book Name", "Publisher", "Author");
		Mockito.when(bookClient.modifyBookById(bookId, bookDTO)).thenReturn(ResponseEntity.ok(bookDTORes));
		mockMvc.perform(put("/library/books/{id}", bookId).contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(bookDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(bookDTO.getId()))).andExpect(jsonPath("$.name", is(bookDTO.getName())))
				.andExpect(jsonPath("$.publisher", is(bookDTO.getPublisher())))
				.andExpect(jsonPath("$.author", is(bookDTO.getAuthor())));
	}

	@Test
	void modifyUsers_ShouldReturnOk() throws Exception {
		String username = "john";
		UserRequestDTO userDTO = new UserRequestDTO(1, "john", "john@example.com", "John Doe");
		UserResponseDTO userDTORes = new UserResponseDTO(1, "john", "john@example.com", "John Doe");
		Mockito.when(userClient.updateUser(username, userDTO)).thenReturn(ResponseEntity.ok(userDTORes));
		mockMvc.perform(put("/library/users/{username}", username).contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(userDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(userDTO.getId())))
				.andExpect(jsonPath("$.username", is(userDTO.getUsername())))
				.andExpect(jsonPath("$.email", is(userDTO.getEmail())))
				.andExpect(jsonPath("$.name", is(userDTO.getName())));
	}

}
