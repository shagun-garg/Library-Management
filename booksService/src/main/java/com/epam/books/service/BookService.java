package com.epam.books.service;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.epam.books.dto.BookDTO;
import com.epam.books.model.Book;
import com.epam.books.repository.BookRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@Service
public class BookService {

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	BookRepository bookRepository;

	public BookDTO addBook(BookDTO bookDTO) {
		log.info("Creating a book : {}", bookDTO);
		try {
			Book book = modelMapper.map(bookDTO, Book.class);
			return modelMapper.map(bookRepository.save(book), BookDTO.class);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException("Same Book Already exists");
		}
	}

	public void removeBook(int id) {
		log.info("Removing the book with id : {}", id);
		bookRepository.deleteById(id);
	}

	public List<BookDTO> viewAllBooks() {
		log.info("Viewing all books :");
		return bookRepository.findAll().stream().map(book -> modelMapper.map(book, BookDTO.class)).toList();
	}

	public BookDTO viewById(int id) {
		return bookRepository.findById(id)
				.map(book -> BookDTO.builder().name(book.getName()).author(book.getAuthor()).id(book.getId())
						.publisher(book.getPublisher()).build())
				.orElseGet(() -> BookDTO.builder().developerMessage("no book found with given id")
						.timeStamp(new Date().toString()).httpStatus(HttpStatus.NO_CONTENT).build());
	}

	public BookDTO modifyBook(int id, BookDTO bookDTO) {
		return bookRepository.findById(id).map(book -> {
			book.setName(bookDTO.getName());
			book.setAuthor(bookDTO.getAuthor());
			book.setPublisher(bookDTO.getPublisher());
			return BookDTO.builder().name(book.getName()).author(book.getAuthor()).id(book.getId())
					.publisher(book.getPublisher()).build();
		}).orElseGet(() -> BookDTO.builder().developerMessage("no book found with given id")
				.timeStamp(new Date().toString()).httpStatus(HttpStatus.NO_CONTENT).build());
	}
}
