package com.epam.userservice.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.userservice.dto.UserDTO;
import com.epam.userservice.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("users")
public class UserRestController {

	@Autowired
	UserService userService;

	@GetMapping
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		log.info("get request to get all questions");
		return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
	}

	@GetMapping("/{username}")
	public ResponseEntity<UserDTO> getUserByUsername(
			@PathVariable @Valid @NotBlank(message = "username cannot be empty") String username) {
		log.info("get request to get user by username: {}", username);
		return new ResponseEntity<>(userService.getUserByUsername(username), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<UserDTO> addUser(@RequestBody @Valid UserDTO userDTO) {
		log.info("post request to add user: {}", userDTO);
		UserDTO userDtoResponse=userService.addUser(userDTO);
		return new ResponseEntity<>(userDtoResponse, userDtoResponse.getDeveloperMessage() == null ? HttpStatus.CREATED : HttpStatus.OK);
	}

	@DeleteMapping("/{username}")
	public ResponseEntity<Void> deleteUser(
			@PathVariable @Valid @NotBlank(message = "username cannot be empty") String username) {
		log.info("delete request for user: {}", username);
		userService.removeUser(username);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("/{username}")
	public ResponseEntity<UserDTO> updateUser(
			@PathVariable @Valid @NotBlank(message = "username cannot be empty") String username,
			@RequestBody @Valid UserDTO userDTO) {
		log.info("put request for user: {}", userDTO);
		return new ResponseEntity<>(userService.updateUser(username, userDTO), HttpStatus.OK);
	}
}
