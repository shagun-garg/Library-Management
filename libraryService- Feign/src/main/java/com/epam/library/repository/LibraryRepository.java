package com.epam.library.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.epam.library.model.Library;

import jakarta.transaction.Transactional;

public interface LibraryRepository extends CrudRepository<Library, Integer>{
	
	@Transactional
	void deleteByUsernameAndBookId(String username,int bookId);	
	@Transactional
	void deleteByBookId(int id);
	@Transactional
	void deleteByUsername(String username);
	List<Library> findByUsername(String username);
	int countByUsername(String username);
	boolean existsByUsernameAndBookId(String username,int bookId);
}
