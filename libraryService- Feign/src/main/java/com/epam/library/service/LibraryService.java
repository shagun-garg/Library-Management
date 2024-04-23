package com.epam.library.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.epam.library.dto.BookResponseDTO;
import com.epam.library.dto.LibraryDTO;
import com.epam.library.model.Library;
import com.epam.library.repository.BooksProxy;
import com.epam.library.repository.LibraryRepository;
import com.epam.library.repository.UserProxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LibraryService {

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	BooksProxy booksProxy;

	@Autowired
	UserProxy userProxy;

	@Autowired
	LibraryRepository libraryRepository;

	public LibraryDTO issueNewBookToUser(String username, int bookId) {
		LibraryDTO resultLibraryDTO;
		if (bookDoesNotExists(bookId) || userDoesNotExists(username)) {
			resultLibraryDTO = LibraryDTO.builder().developerMessage("Book and UserId does not match. Try again")
			.timeStamp(new Date().toString()).httpStatus(HttpStatus.NO_CONTENT).build();
		}
		else if (libraryRepository.existsByUsernameAndBookId(username, bookId)) {
			log.error("Same book already issued to user");
			resultLibraryDTO = LibraryDTO.builder().developerMessage("Same book already issued to user")
			.timeStamp(new Date().toString()).httpStatus(HttpStatus.NO_CONTENT).build();
		}
		else if (libraryRepository.countByUsername(username) == 3) {
			log.error("you already took 3 books");
			resultLibraryDTO = LibraryDTO.builder().developerMessage("you already took 3 books")
			.timeStamp(new Date().toString()).httpStatus(HttpStatus.NO_CONTENT).build();
		}
		else {
		Library library = Library.builder().username(username).bookId(bookId).build();
		log.info("Issuing book to user with username : {}  and bookId : {}", username, bookId);
		resultLibraryDTO= modelMapper.map(libraryRepository.save(library), LibraryDTO.class);
		}
		return resultLibraryDTO;
	}

	public void withdrawBookFromUser(String username, int bookId) {
		log.info("Deallocating book from user : {} and bookId : {}", username, bookId);
		libraryRepository.deleteByUsernameAndBookId(username, bookId);
	}

	public void deleteBookFromLibrary(int id) {
		log.info("Deleting book from Library with bookId : {}", id);
		libraryRepository.deleteByBookId(id);
	}

	public void deleteUserFromLibrary(String username) {
		log.info("Deleting user from Library with username : {}", username);
		libraryRepository.deleteByUsername(username);
	}
	
	public List<BookResponseDTO> getAllBooksAssociatedWithUser(String username) {
		log.info("getting all books from library");
		List<Integer> bookIdList = libraryRepository.findByUsername(username).stream().map(Library::getBookId).toList();
		Optional<List<BookResponseDTO>> allBooksList = Optional.of(booksProxy.displayAllBook().getBody());
		return allBooksList.get().stream().filter(book -> bookIdList.contains(book.getId())).toList();
	}

	private boolean userDoesNotExists(String username) {
		return Optional.of(userProxy.getUserByUsername(username).getBody()).get().getDeveloperMessage()!=null;
	}

	private boolean bookDoesNotExists(int bookId) {
		return Optional.of(booksProxy.displayBookById(bookId).getBody()).get().getDeveloperMessage()!=null;
	}
}
