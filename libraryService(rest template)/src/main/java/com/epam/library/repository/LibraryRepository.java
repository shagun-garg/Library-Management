package com.epam.library.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.epam.library.model.Library;

public interface LibraryRepository extends CrudRepository<Library, Integer>{
	
	void deleteDistinctByUsernameAndBookId(String username,int bookId);	
	void deleteByBookId(int id);
	void deleteByUsername(String username);
	List<Library> findByUsername(String username);
	int countByUsername(String username);
	boolean existsByUsernameAndBookId(String username,int bookId);
	
}
