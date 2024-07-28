package org.mounanga.userservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.userservice.configuration.ApplicationProperties;
import org.mounanga.userservice.dto.LoginRequestDTO;
import org.mounanga.userservice.dto.LoginResponseDTO;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.exception.UserNotAuthenticatedException;
import org.mounanga.userservice.exception.UserNotEnabledException;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.repository.UserRepository;
import org.mounanga.userservice.util.MailingService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationProperties properties;

    @Mock
    private MailingService mailingService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationServiceImpl(authenticationManager, userRepository, properties, mailingService);
    }

    @Test
    void authenticateSuccess() {
        LoginRequestDTO request = new LoginRequestDTO("testUser", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("USER").build());
        User user = new User();
        user.setUsername("testUser");
        user.setRoles(roles);
        user.setEnabled(true);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(properties.getJwtSecret()).thenReturn("secret");
        when(properties.getJwtExpiration()).thenReturn(3600000L);

        // Act
        LoginResponseDTO response = authenticationService.authenticate(request);

        // Assert
        assertNotNull(response);
        verify(mailingService).sendMail(eq(user.getEmail()), anyString(), anyString());
        verify(userRepository).save(user);
    }

    @Test
    void authenticateThrowsUserNotFoundException() {
        LoginRequestDTO request = new LoginRequestDTO("testUser", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void authenticateThrowsUserNotEnabledException() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("testUser", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        User user = new User();
        user.setUsername("testUser");
        user.setEnabled(false);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UserNotEnabledException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void authenticateThrowsUserNotAuthenticatedException() {
        LoginRequestDTO request = new LoginRequestDTO("testUser", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotAuthenticatedException.class, () -> authenticationService.authenticate(request));
    }
}