package org.mounanga.userservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.userservice.dto.UserRequest;
import org.mounanga.userservice.dto.UserResponse;
import org.mounanga.userservice.dto.UserRoleRequest;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.exception.ItemAlreadyExistException;
import org.mounanga.userservice.exception.RoleNotFoundException;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.repository.RoleRepository;
import org.mounanga.userservice.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequest userRequest;
    private Role role;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);
        user = new User();
        user.setId("1");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPhone("123456789");
        user.setNip("12345");

        userRequest = new UserRequest();
        userRequest.setUsername("testuser");
        userRequest.setEmail("test@example.com");
        userRequest.setPhone("123456789");
        userRequest.setNip("12345");

        role = new Role();
        role.setName("USER");
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByNip(any())).thenReturn(false);
        when(userRepository.existsByPhone(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        UserResponse response = userService.createUser(userRequest);

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void createUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(any())).thenReturn(true);

        assertThrows(ItemAlreadyExistException.class, () -> userService.createUser(userRequest));
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.updateUser("1", userRequest);

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
    }

    @Test
    void updateUser_UserNotFound() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser("1", userRequest));
    }

    @Test
    void deleteUserById_Success() {
        doNothing().when(userRepository).deleteById(anyString());

        userService.deleteUserById("1");

        verify(userRepository, times(1)).deleteById("1");
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById("1");

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
    }

    @Test
    void getUserById_UserNotFound() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById("1"));
    }

    @Test
    void addRoleToUser_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRoleRequest request = new UserRoleRequest("testuser", "USER");
        UserResponse response = userService.addRoleToUser(request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void addRoleToUser_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserRoleRequest request = new UserRoleRequest("testuser", "USER");

        assertThrows(UserNotFoundException.class, () -> userService.addRoleToUser(request));
    }

    @Test
    void addRoleToUser_RoleNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        UserRoleRequest request = new UserRoleRequest("testuser", "USER");

        assertThrows(RoleNotFoundException.class, () -> userService.addRoleToUser(request));
    }

    @Test
    void enableOrDisableUser_Success_Enable() {
        user.setEnabled(false);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.enableOrDisableUser("1");

        assertNotNull(response);
        assertTrue(response.getEnabled());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void enableOrDisableUser_Success_Disable() {
        user.setEnabled(true);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.enableOrDisableUser("1");

        assertNotNull(response);
        assertFalse(response.getEnabled());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void enableOrDisableUser_UserNotFound() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.enableOrDisableUser("1"));
    }
}