package com.epam.library.repository;

import java.util.List;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.epam.library.dto.UserRequestDTO;
import com.epam.library.dto.UserResponseDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@FeignClient(name = "users-service", fallback =UserProxyImpl.class)
@LoadBalancerClient(name = "users-service")
public interface UserProxy {

	@GetMapping("users")
	public ResponseEntity<List<UserResponseDTO>> getAllUsers();

	@GetMapping("users/{username}")
	public ResponseEntity<UserResponseDTO> getUserByUsername(
			@PathVariable @Valid @NotBlank(message = "username cannot be empty") String username);

	@PostMapping("users")
	public ResponseEntity<UserResponseDTO> addUser(@RequestBody @Valid UserRequestDTO userDTO);

	@DeleteMapping("users/{username}")
	public ResponseEntity<Void> deleteUser(
			@PathVariable @Valid @NotBlank(message = "username cannot be empty") String username);

	@PutMapping("users/{username}")
	public ResponseEntity<UserResponseDTO> updateUser(
			@PathVariable @Valid @NotBlank(message = "username cannot be empty") String username,
			@RequestBody @Valid UserRequestDTO userDTO);

}
