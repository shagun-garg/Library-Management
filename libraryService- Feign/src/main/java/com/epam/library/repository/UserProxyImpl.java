package com.epam.library.repository;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.epam.library.dto.UserRequestDTO;
import com.epam.library.dto.UserResponseDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Service
public class UserProxyImpl implements UserProxy{

	private static final String SERVICE_DOWN="Service is Down";
	
	@Override
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		UserResponseDTO user = UserResponseDTO.builder().developerMessage(SERVICE_DOWN).build();
		return new ResponseEntity<>(Arrays.asList(user), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<UserResponseDTO> getUserByUsername(
			@Valid @NotBlank(message = "username cannot be empty") String username) {
		UserResponseDTO user = UserResponseDTO.builder().developerMessage(SERVICE_DOWN).build();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<UserResponseDTO> addUser(@Valid UserRequestDTO userDTO) {
		UserResponseDTO user = UserResponseDTO.builder().developerMessage(SERVICE_DOWN).build();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> deleteUser(@Valid @NotBlank(message = "username cannot be empty") String username) {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<UserResponseDTO> updateUser(
			@Valid @NotBlank(message = "username cannot be empty") String username, @Valid UserRequestDTO userDTO) {
		UserResponseDTO user = UserResponseDTO.builder().developerMessage(SERVICE_DOWN).build();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

}
