package com.epam.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import com.epam.userservice.dto.UserDTO;
import com.epam.userservice.model.User;
import com.epam.userservice.repo.UserRepository;
import com.epam.userservice.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private UserService userService;

	@Test
	void testGetAllUsers() {
		User user1 = User.builder().id(1).username("john").email("john@test.com").name("John").build();
		User user2 = User.builder().id(2).username("jane").email("jane@test.com").name("Jane").build();
		List<User> userList = List.of(user1, user2);
		UserDTO userDTO1 = UserDTO.builder().id(1).username("john").email("john@test.com").name("John").build();
		UserDTO userDTO2 = UserDTO.builder().id(2).username("jane").email("jane@test.com").name("Jane").build();
		List<UserDTO> expectedUserDTOList = List.of(userDTO1, userDTO2);

		Mockito.when(userRepository.findAll()).thenReturn(userList);
		Mockito.when(modelMapper.map(user1, UserDTO.class)).thenReturn(userDTO1);
		Mockito.when(modelMapper.map(user2, UserDTO.class)).thenReturn(userDTO2);

		List<UserDTO> actualUserDTOList = userService.getAllUsers();
		assertEquals(expectedUserDTOList, actualUserDTOList);
	}

	@Test
	void testAddUser() {
		UserDTO userDTO = UserDTO.builder().id(1).username("john").email("john@test.com").name("John").build();
		User user = User.builder().id(1).username("john").email("john@test.com").name("John").build();

		Mockito.when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
		Mockito.when(modelMapper.map(userDTO, User.class)).thenReturn(user);
		Mockito.when(userRepository.save(user)).thenReturn(user);
		Mockito.when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

		UserDTO actualUserDTO = userService.addUser(userDTO);
		assertEquals(userDTO, actualUserDTO);
	}

	@Test
	void testDeleteUser() {
		String username = "john123";
		userService.removeUser(username);
		verify(userRepository).deleteByUsername(username);
	}

	@Test
	void shouldGetUserByUsername() {
		String username = "johnDoe";
		User user = new User(1, username, "johndoe@example.com", "John Doe");
		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
		UserDTO result = userService.getUserByUsername(username);
		assertThat(result.getUsername()).isEqualTo(username);
		assertThat(result.getName()).isEqualTo(user.getName());
		assertThat(result.getEmail()).isEqualTo(user.getEmail());
	}

	@Test
	void shouldReturnNoContentWhenGetUserByUsernameFails() {
		String username = "nonExistingUser";
		when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
		UserDTO result = userService.getUserByUsername(username);
		assertThat(result.getDeveloperMessage()).isEqualTo("no user found with given username");
		assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldUpdateUser() {
		String username = "johnDoe";
		User user = new User(1, username, "johndoe@example.com", "John Doe");
		UserDTO userDTO = UserDTO.builder().id(user.getId()).username(user.getUsername()).email("new_email@example.com")
				.name("New Name").build();
		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
		UserDTO result = userService.updateUser(username, userDTO);
		assertThat(result.getUsername()).isEqualTo(userDTO.getUsername());
		assertThat(result.getName()).isEqualTo(userDTO.getName());
		assertThat(result.getEmail()).isEqualTo(userDTO.getEmail());
	}

	@Test
	void shouldReturnNoContentWhenUpdateUserFails() {
		String username = "nonExistingUser";
		UserDTO userDTO = UserDTO.builder().username(username).email("email@example.com").name("Name").build();
		when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
		UserDTO result = userService.updateUser(username, userDTO);
		assertThat(result.getDeveloperMessage()).isEqualTo("no user found with given username");
		assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void testAddUser_UserAlreadyExists() {
		UserDTO userDTO = UserDTO.builder().id(1).username("johndoe").email("johndoe@example.com").name("John Doe")
				.build();

		when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(true);
		UserDTO result = userService.addUser(userDTO);
		assertNotNull(result);
		assertEquals(HttpStatus.NO_CONTENT, result.getHttpStatus());
		assertEquals("User is already present with given username", result.getDeveloperMessage());
	}
}