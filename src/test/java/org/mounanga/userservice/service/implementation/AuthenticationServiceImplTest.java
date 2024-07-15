package org.mounanga.userservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.userservice.dto.LoginRequest;
import org.mounanga.userservice.dto.LoginResponse;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.exception.UserNotAuthenticatedException;
import org.mounanga.userservice.exception.UserNotEnabledException;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.repository.UserRepository;
import org.mounanga.userservice.util.ApplicationProperties;
import org.mounanga.userservice.util.MailingService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    private User user;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationServiceImpl(
                authenticationManager, userRepository, properties, mailingService
        );
        Role role = Role.builder().name("USER").build();
        user = User.builder()
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .enabled(true)
                .roles(List.of(role))
                .build();
        loginRequest = new LoginRequest("testuser", "password");

        when(properties.getJwtSecret()).thenReturn("secret");
        when(properties.getJwtExpiration()).thenReturn(60000L);
    }

    @Test
    void authenticate_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        LoginResponse response = authenticationService.authenticate(loginRequest);

        assertNotNull(response);
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(mailingService, times(1)).sendMail(anyString(), anyString(), anyString());
    }

    @Test
    void authenticate_UserNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(loginRequest));
    }

    @Test
    void authenticate_UserNotEnabled() {
        user.setEnabled(false);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        assertThrows(UserNotEnabledException.class, () -> authenticationService.authenticate(loginRequest));
    }

    @Test
    void authenticate_UserNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        assertThrows(UserNotAuthenticatedException.class, () -> authenticationService.authenticate(loginRequest));
    }



}