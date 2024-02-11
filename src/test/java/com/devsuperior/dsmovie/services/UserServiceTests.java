package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil userUtil;

	private String existingUsername;
	private String nonExistingUsername;
	private UserEntity user;
	private List<UserDetailsProjection> userDetails;

	@BeforeEach
	void setUp() {
		existingUsername = "maria@gmail.com";
		nonExistingUsername = "user@gmail.com";

		user = UserFactory.createUserEntity();
		userDetails = UserDetailsFactory.createCustomClientUser(existingUsername);

		when(repository.searchUserAndRolesByUsername(existingUsername)).thenReturn(userDetails);
		when(repository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(new ArrayList<>());

		when(repository.findByUsername(existingUsername)).thenReturn(Optional.of(user));
		when(repository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		when(userUtil.getLoggedUsername()).thenReturn(existingUsername);

		UserEntity result = service.authenticated();

		assertNotNull(result);
		assertEquals(result.getUsername(), existingUsername);
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();

		assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();
		});
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		UserDetails result = service.loadUserByUsername(existingUsername);

		assertNotNull(result);
		assertEquals(result.getUsername(), existingUsername);
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(nonExistingUsername);
		});
	}
}
