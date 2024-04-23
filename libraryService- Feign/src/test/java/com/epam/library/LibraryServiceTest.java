package com.epam.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.epam.library.dto.BookResponseDTO;
import com.epam.library.dto.LibraryDTO;
import com.epam.library.dto.UserResponseDTO;
import com.epam.library.model.Library;
import com.epam.library.repository.BooksProxy;
import com.epam.library.repository.LibraryRepository;
import com.epam.library.repository.UserProxy;
import com.epam.library.service.LibraryService;

@SpringBootTest
class LibraryServiceTest {

	@Mock
	private LibraryRepository libraryRepository;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private BooksProxy booksProxy;

	@Mock
	private UserProxy userProxy;

	@InjectMocks
	private LibraryService libraryService;

	@Test
	void testWithdrawBookFromUser() {
		String username = "aditya";
		int bookId = 123;
		libraryService.withdrawBookFromUser(username, bookId);
		verify(libraryRepository).deleteByUsernameAndBookId(username, bookId);
	}

	@Test
	void testDeleteUserFromLibrary() {
		String username = "aditya";
		libraryService.deleteUserFromLibrary(username);
		verify(libraryRepository).deleteByUsername(username);
	}

	@Test
	void testGetAllBooksAssociatedWithUser() {
		List<Library> libraryList = new ArrayList<>();
		List<BookResponseDTO> bookDTOList = new ArrayList<>();
		when(libraryRepository.findByUsername("testuser")).thenReturn(libraryList);
		when(booksProxy.displayAllBook()).thenReturn(ResponseEntity.ok(bookDTOList));
		List<BookResponseDTO> resultList = libraryService.getAllBooksAssociatedWithUser("testuser");
		assertEquals(bookDTOList, resultList);
		verify(libraryRepository).findByUsername("testuser");
		verify(booksProxy).displayAllBook();
	}

	@Test
	void issueNewBookToUser_shouldReturnInvalidInputs_whenBookOrUserDoesNotExist() {
		String username = "test_user";
		int bookId = 123;
		when(userProxy.getUserByUsername(username))
				.thenReturn(ResponseEntity.ok(UserResponseDTO.builder().developerMessage("User not found").build()));
		when(booksProxy.displayBookById(bookId))
				.thenReturn(ResponseEntity.ok(BookResponseDTO.builder().developerMessage("Book not found").build()));
		LibraryDTO result = libraryService.issueNewBookToUser(username, bookId);
		assertNotNull(result);
		assertEquals(HttpStatus.NO_CONTENT, result.getHttpStatus());
		assertEquals("invalid inputs", result.getDeveloperMessage());
	}

	@Test
	void issueNewBookToUser_shouldReturnSameBookAlreadyIssued_whenBookAlreadyIssuedToUser() {
		String username = "test_user";
		int bookId = 123;
		when(userProxy.getUserByUsername(username))
				.thenReturn(ResponseEntity.ok(UserResponseDTO.builder().id(1).username(username).build()));
		when(booksProxy.displayBookById(bookId))
				.thenReturn(ResponseEntity.ok(BookResponseDTO.builder().id(bookId).build()));
		when(libraryRepository.existsByUsernameAndBookId(username, bookId)).thenReturn(true);
		LibraryDTO result = libraryService.issueNewBookToUser(username, bookId);
		assertNotNull(result);
		assertEquals(HttpStatus.NO_CONTENT, result.getHttpStatus());
		assertEquals("Same book already issued to user", result.getDeveloperMessage());
	}

	@Test
	void issueNewBookToUser_shouldReturnMaxBooksExceeded_whenUserAlreadyIssued3Books() {
		String username = "test_user";
		int bookId = 123;
		when(userProxy.getUserByUsername(username))
				.thenReturn(ResponseEntity.ok(UserResponseDTO.builder().id(1).username(username).build()));
		when(booksProxy.displayBookById(bookId))
				.thenReturn(ResponseEntity.ok(BookResponseDTO.builder().id(bookId).build()));
		when(libraryRepository.existsByUsernameAndBookId(username, bookId)).thenReturn(false);
		when(libraryRepository.countByUsername(username)).thenReturn(3);
		LibraryDTO result = libraryService.issueNewBookToUser(username, bookId);
		assertNotNull(result);
		assertEquals(HttpStatus.NO_CONTENT, result.getHttpStatus());
		assertEquals("you already took 3 books", result.getDeveloperMessage());
	}

	@Test
	void testIssueNewBookToUser() {
		String username = "john";
		int bookId = 1;
		BookResponseDTO bookDTO = BookResponseDTO.builder().id(bookId).name("The Great Gatsby")
				.author("F. Scott Fitzgerald").build();
		UserResponseDTO userDTO = UserResponseDTO.builder().username(username).email("john@example.com").build();
		Library library = Library.builder().username(username).bookId(bookId).build();
		LibraryDTO libraryDTO = LibraryDTO.builder().id(1).username(username).bookId(bookId).build();
		when(booksProxy.displayBookById(bookId)).thenReturn(ResponseEntity.ok(bookDTO));
		when(userProxy.getUserByUsername(username)).thenReturn(ResponseEntity.ok(userDTO));
		when(libraryRepository.existsByUsernameAndBookId(username, bookId)).thenReturn(false);
		when(libraryRepository.countByUsername(username)).thenReturn(2);
		when(libraryRepository.save(any(Library.class))).thenReturn(library);
		when(modelMapper.map(library, eq(LibraryDTO.class))).thenReturn(libraryDTO);
		LibraryDTO result = libraryService.issueNewBookToUser(username, bookId);
		verify(booksProxy).displayBookById(bookId);
		verify(userProxy).getUserByUsername(username);
		verify(libraryRepository).existsByUsernameAndBookId(username, bookId);
		verify(libraryRepository).countByUsername(username);
		verify(libraryRepository).save(any(Library.class));
		verify(modelMapper).map(library, eq(LibraryDTO.class));
		assertEquals(libraryDTO, result);
	}

	@Test
	void deleteBookFromLibrary_DeletesBookFromLibrary() {
		int bookId = 123;
		libraryService.deleteBookFromLibrary(bookId);
		verify(libraryRepository, times(1)).deleteByBookId(bookId);
	}

}
