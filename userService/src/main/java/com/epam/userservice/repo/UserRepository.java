package com.epam.userservice.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epam.userservice.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String username);
	void deleteByUsername(String username);
	boolean existsByUsername(String username);
}
