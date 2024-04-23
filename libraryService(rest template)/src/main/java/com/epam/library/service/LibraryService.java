package com.epam.library.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.library.dto.LibraryDTO;
import com.epam.library.exceptions.LibraryException;
import com.epam.library.model.Library;
import com.epam.library.repository.LibraryRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@Service
public class LibraryService {

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	LibraryRepository libraryRepository;

	public LibraryDTO issueNewBookToUser(String username, int bookId) {
		Library library = new Library(username, bookId);
		if (libraryRepository.existsByUsernameAndBookId(username, bookId)) {
			log.error("Same book already issued to user");
			throw new LibraryException("Same book already issued to user");
		}
		if (libraryRepository.countByUsername(username) == 3) {
			log.error("you already took 3 books");
			throw new LibraryException("you already took 3 books");
		}
		
		log.info("Issuing book to user with username : {}  and bookId : {}", username, bookId);
		library = libraryRepository.save(library);
		return modelMapper.map(library, LibraryDTO.class);
	}

	public void removeBookFromUser(String username, int bookId) {
		log.info("Deallocating book from user : {} and bookId : {}", username, bookId);
		libraryRepository.deleteDistinctByUsernameAndBookId(username, bookId);
	}

	public List<Integer> getAllBooksByUser(String username) {
		log.info("getting all books from library");
		return libraryRepository.findByUsername(username).stream().map(Library::getBookId).toList();
	}

	public void deleteBookFromLibrary(int id) {
		log.info("Deleting book from Library with bookId : {}", id);
		libraryRepository.deleteByBookId(id);
	}

	public void deleteUserFromLibrary(String username) {
		log.info("Deleting user from Library with username : {}", username);
		libraryRepository.deleteByUsername(username);
	}

}
