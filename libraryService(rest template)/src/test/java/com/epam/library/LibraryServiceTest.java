package com.epam.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import com.epam.library.dto.LibraryDTO;
import com.epam.library.exceptions.LibraryException;
import com.epam.library.model.Library;
import com.epam.library.repository.LibraryRepository;
import com.epam.library.service.LibraryService;

@SpringBootTest
class LibraryServiceTest {

	@Mock
	private LibraryRepository libraryRepository;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private LibraryService libraryService;

	@Test
	void testIssueBookToUser() {
		String username = "aditya";
		int bookId = 123;
		Library library = new Library();
		library.setUsername(username);
		library.setBookId(bookId);
		when(libraryRepository.countByUsername(username)).thenReturn(2);
		when(libraryRepository.save(library)).thenReturn(library);
		when(modelMapper.map(library, LibraryDTO.class)).thenReturn(new LibraryDTO(username, bookId));
		LibraryDTO result = libraryService.issueNewBookToUser(username, bookId);
		assertNotNull(result);
		assertEquals(result.getUsername(), username);
		assertEquals(result.getBookId(), bookId);
	}

	@Test
	void testIssueBookToUser_exceedLimit() {
		String username = "aditya";
		int bookId = 123;
		when(libraryRepository.countByUsername(username)).thenReturn(3);
		assertThrows(LibraryException.class, () -> libraryService.issueNewBookToUser(username, bookId));
	}

	@Test
	void testWithdrawBookFromUser() {
		String username = "aditya";
		int bookId = 123;
		libraryService.removeBookFromUser(username, bookId);
		verify(libraryRepository).deleteDistinctByUsernameAndBookId(username, bookId);
	}

	@Test
	void testDeleteBookFromLibrary() {
		int id = 123;
		libraryService.deleteBookFromLibrary(id);
		verify(libraryRepository).deleteByBookId(id);
	}

	@Test
	void testDeleteUserFromLibrary() {
		String username = "aditya";
		libraryService.deleteUserFromLibrary(username);
		verify(libraryRepository).deleteByUsername(username);
	}

	@Test
	void testGetAllBookIdsByUsername() {
		String username = "aditya";
		List<Library> libraries = Arrays.asList(Library.builder().username(username).bookId(123).build(),
				Library.builder().username(username).bookId(456).build());
		List<Integer> bookIds = libraries.stream().map(Library::getBookId).toList();
		when(libraryRepository.findByUsername(username)).thenReturn(libraries);
		List<Integer> result = libraryService.getAllBooksByUser(username);
		assertNotNull(result);
		assertEquals(result, bookIds);
	}

	@Test
	void shouldThrowLibraryExceptionWhenSameBookIsAlreadyIssuedToUser() {
		String username = "aditya.doe";
		int bookId = 123;
		when(libraryRepository.existsByUsernameAndBookId(username, bookId)).thenReturn(true);
		assertThrows(LibraryException.class, () -> {
			libraryService.issueNewBookToUser(username, bookId);
		});
	}
}
