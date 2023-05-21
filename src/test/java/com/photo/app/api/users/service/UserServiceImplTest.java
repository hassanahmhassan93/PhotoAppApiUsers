package com.photo.app.api.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.photo.app.api.users.data.UserEntity;
import com.photo.app.api.users.data.UserRepository;
import com.photo.app.api.users.shared.UserDto;

class UserServiceImplTest {

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private String encryptedPassword = "hjfgjdbsf";
	private String userId = "dshgsjdfs";

	private UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {

		MockitoAnnotations.openMocks(this);

		userEntity = new UserEntity();
		userEntity.setEmail("test@test.com");
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(encryptedPassword));
		userEntity.setFirstName("hassan");
		userEntity.setId(1L);
		userEntity.setLastName("sfgksjdb");
		userEntity.setUserId(userId);
	}

	@Test
	void testGetUserDetailsByEmail_existingUser() {
				
		// Arrange				
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		
		// Act
		UserDto userDto = userServiceImpl.getUserDetailsByEmail("test@test.com");
		
		
		// Assert
		assertNotNull(userDto);
		assertEquals("hassan", userDto.getFirstName());
		
		verify(userRepository, times(1)).findByEmail("test@test.com");
	}

	@Test
	final void testGetUserDetailsByEmail_nonExistingUser() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		
		assertThrows(UsernameNotFoundException.class, () -> {
			
			userServiceImpl.getUserDetailsByEmail("test@test.com");
		});
	}

	@Test
	final void testCreateUser_Success() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		
		UserDto userDto = userServiceImpl.createUser(new UserDto());
		
		assertNotNull(userDto);
		assertEquals(userEntity.getEncryptedPassword(), userDto.getEncryptedPassword());
		
		verify(bCryptPasswordEncoder, times(1)).encode(encryptedPassword);
		verify(userRepository, times(1)).save(any(UserEntity.class));
	}

	@Test
	final void testCreateUser_RecordAlreadyExists() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = new UserDto();
		userDto.setEmail("dgjdbgsdj");
		
		assertThrows(RuntimeException.class, () -> {
			
			userServiceImpl.createUser(userDto);
		});
	}
}
