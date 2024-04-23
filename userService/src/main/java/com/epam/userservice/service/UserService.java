package com.epam.userservice.service;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.epam.userservice.dto.UserDTO;
import com.epam.userservice.model.User;
import com.epam.userservice.repo.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ModelMapper modelMapper;

	private static final String NOT_FOUND = "no user found with given username";

	public UserDTO addUser(UserDTO userDTO) {
		if (userRepository.existsByUsername(userDTO.getUsername())) {
			return UserDTO.builder().developerMessage("User is already present with given username")
					.timeStamp(new Date().toString()).httpStatus(HttpStatus.NO_CONTENT).build();
		}
		log.info("inside add user method with user : {}", userDTO);
		return modelMapper.map(userRepository.save(modelMapper.map(userDTO, User.class)), UserDTO.class);
	}

	public void removeUser(String username) {
		log.info("inside delete user method with username : {}", username);
		userRepository.deleteByUsername(username);
	}

	public List<UserDTO> getAllUsers() {
		log.info("inside getAllUsers method");
		return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
	}

	public UserDTO getUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.map(user -> UserDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail())
						.username(user.getUsername()).build())
				.orElseGet(() -> UserDTO.builder().developerMessage(NOT_FOUND)
						.timeStamp(new Date().toString()).httpStatus(HttpStatus.NO_CONTENT).build());
	}

	public UserDTO updateUser(String username, UserDTO userDTO) {
		return userRepository.findByUsername(username).map(user -> {
			user.setName(userDTO.getName());
			user.setEmail(userDTO.getEmail());
			user.setUsername(userDTO.getUsername());
			return UserDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail())
					.username(user.getUsername()).build();
		}).orElseGet(() -> UserDTO.builder().developerMessage(NOT_FOUND)
				.timeStamp(new Date().toString()).httpStatus(HttpStatus.NO_CONTENT).build());
	}
}
