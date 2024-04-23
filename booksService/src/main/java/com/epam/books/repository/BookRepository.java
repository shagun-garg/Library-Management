package com.epam.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epam.books.model.Book;

public interface BookRepository extends JpaRepository<Book, Integer>{

}
