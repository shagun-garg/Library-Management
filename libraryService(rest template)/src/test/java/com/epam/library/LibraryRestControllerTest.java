package com.epam.library;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.epam.library.dto.BookDTO;
import com.epam.library.dto.UserDTO;
import com.epam.library.restcontroller.LibraryRestController;
import com.epam.library.service.LibraryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class LibraryRestControllerTest {

	private MockMvc mockMvc;

	@Mock
	private LibraryService libraryService;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private LibraryRestController libraryRestController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(libraryRestController).build();
	}

	@Test
	void testGetBookById() throws Exception {
		int bookId = 1;
		BookDTO bookDTO = BookDTO.builder().id(bookId).name("Book 1").publisher("Publisher 1").author("Author 1")
				.build();
		when(restTemplate.getForEntity(anyString(), eq(BookDTO.class), eq(bookId)))
				.thenReturn(ResponseEntity.ok(bookDTO));

		mockMvc.perform(get("/library/books/{id}", bookId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(bookId))).andExpect(jsonPath("$.name", is(bookDTO.getName())))
				.andExpect(jsonPath("$.publisher", is(bookDTO.getPublisher())))
				.andExpect(jsonPath("$.author", is(bookDTO.getAuthor())));
	}

	@Test
	void testAddBook() throws Exception {
		BookDTO bookDTO = BookDTO.builder().name("Book 1").publisher("Publisher 1").author("Author 1").build();
		when(restTemplate.postForEntity(anyString(), eq(bookDTO), eq(BookDTO.class)))
				.thenReturn(ResponseEntity.ok(bookDTO));

		mockMvc.perform(post("/library/books").contentType(MediaType.APPLICATION_JSON).content(asJsonString(bookDTO)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name", is(bookDTO.getName())))
				.andExpect(jsonPath("$.publisher", is(bookDTO.getPublisher())))
				.andExpect(jsonPath("$.author", is(bookDTO.getAuthor())));
	}

	@Test
	void testModifyBook() throws Exception {
		int bookId = 1;
		BookDTO bookDTO = BookDTO.builder().id(bookId).name("Book 1").publisher("Publisher 1").author("Author 1")
				.build();
		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(BookDTO.class)))
				.thenReturn(ResponseEntity.ok(bookDTO));

		mockMvc.perform(put("/library/books/{id}", bookId).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(bookDTO))).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(bookId)))
				.andExpect(jsonPath("$.name", is(bookDTO.getName())))
				.andExpect(jsonPath("$.publisher", is(bookDTO.getPublisher())))
				.andExpect(jsonPath("$.author", is(bookDTO.getAuthor())));
	}

	@Test
	void testDeleteBookById() throws Exception {
		int bookId = 1;
		doNothing().when(libraryService).deleteBookFromLibrary(bookId);

		mockMvc.perform(delete("/library/books/{bookId}", bookId)).andExpect(status().isNoContent());

		verify(restTemplate, times(1)).delete(anyString(), eq(bookId));
	}

	private String asJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	@Test
	void testGetAllBooks() throws Exception {
		List<BookDTO> bookDTOList = Arrays.asList(new BookDTO(1, "Book1", "Publisher1", "Author1"),
				new BookDTO(2, "Book2", "Publisher2", "Author2"));

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.any(ParameterizedTypeReference.class)))
				.thenReturn(new ResponseEntity<>(bookDTOList, HttpStatus.OK));

		mockMvc.perform(MockMvcRequestBuilders.get("/library/books")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(bookDTOList.size()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value(bookDTOList.get(0).getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value(bookDTOList.get(1).getName()));
	}

	@Test
	void testGetUserByUsername() throws Exception {
		String username = "user1";
		List<Integer> bookIdList = Arrays.asList(1, 2);
		List<BookDTO> bookDTOList = Arrays.asList(new BookDTO(1, "Book1", "Publisher1", "Author1"),
				new BookDTO(2, "Book2", "Publisher2", "Author2"));

		Mockito.when(libraryService.getAllBooksByUser(Mockito.anyString())).thenReturn(bookIdList);

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(UserDTO.class), Mockito.eq(username)))
				.thenReturn(
						new ResponseEntity<>(new UserDTO(1, "user1", "user1@example.com", "User One"), HttpStatus.OK));

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.isNull(),
				Mockito.any(ParameterizedTypeReference.class)))
				.thenReturn(new ResponseEntity<>(bookDTOList, HttpStatus.OK));

		mockMvc.perform(MockMvcRequestBuilders.get("/library/users/{username}", username))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
				.andExpect(MockMvcResultMatchers.jsonPath("$.[0].username").value(username))
				.andExpect(MockMvcResultMatchers.jsonPath("$.[1].size()").value(bookDTOList.size()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.[1].[0].name").value(bookDTOList.get(0).getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.[1].[1].name").value(bookDTOList.get(1).getName()));
	}

	@Test
	void testAddUser() throws Exception {
		UserDTO userDTO = new UserDTO(1, "user1", "user1@example.com", "User One");

		Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.eq(userDTO), Mockito.eq(UserDTO.class)))
				.thenReturn(new ResponseEntity<>(userDTO, HttpStatus.CREATED));

		mockMvc.perform(MockMvcRequestBuilders.post("/library/users").contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(userDTO))).andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	void testModifyUsers() throws Exception {
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername("testuser");
		userDTO.setEmail("testuser@example.com");
		userDTO.setName("Test User");

		when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(UserDTO.class)))
				.thenReturn(ResponseEntity.ok(userDTO));

		mockMvc.perform(put("/library/users/testuser").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(userDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(userDTO.getUsername())))
				.andExpect(jsonPath("$.email", is(userDTO.getEmail())))
				.andExpect(jsonPath("$.name", is(userDTO.getName())));
	}

	@Test
	void testDeleteUserByUsername() throws Exception {
		String username = "testuser";

		mockMvc.perform(delete("/library/users/" + username).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void testGetAllUsers() throws Exception {
		UserDTO userDTO1 = new UserDTO();
		userDTO1.setId(1);
		userDTO1.setUsername("testuser1");
		userDTO1.setEmail("testuser1@example.com");
		userDTO1.setName("Test User 1");

		UserDTO userDTO2 = new UserDTO();
		userDTO2.setId(2);
		userDTO2.setUsername("testuser2");
		userDTO2.setEmail("testuser2@example.com");
		userDTO2.setName("Test User 2");

		List<UserDTO> userDTOList = List.of(userDTO1, userDTO2);

		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(),
				eq(new ParameterizedTypeReference<List<UserDTO>>() {
				}))).thenReturn(ResponseEntity.ok(userDTOList));

		mockMvc.perform(get("/library/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.[0].id", is(userDTO1.getId())))
				.andExpect(jsonPath("$.[0].username", is(userDTO1.getUsername())))
				.andExpect(jsonPath("$.[0].email", is(userDTO1.getEmail())))
				.andExpect(jsonPath("$.[0].name", is(userDTO1.getName())))
				.andExpect(jsonPath("$.[1].id", is(userDTO2.getId())))
				.andExpect(jsonPath("$.[1].username", is(userDTO2.getUsername())))
				.andExpect(jsonPath("$.[1].email", is(userDTO2.getEmail())))
				.andExpect(jsonPath("$.[1].name", is(userDTO2.getName())));
	}

	@Test
	void testWithdrawBookFromUser() throws Exception {
		String username = "testUser";
		int bookId = 1;
		mockMvc.perform(MockMvcRequestBuilders.delete("/library/users/" + username + "/books/" + bookId))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}
}
