package org.mounanga.userservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.userservice.dto.UserPwdRequest;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.entity.Verification;
import org.mounanga.userservice.exception.CodeExpiredException;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.exception.VerificationNotFoundException;
import org.mounanga.userservice.repository.UserRepository;
import org.mounanga.userservice.repository.VerificationRepository;
import org.mounanga.userservice.util.MailingService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SecurityServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private MailingService mailingService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityServiceImpl securityService;

    private UserPwdRequest validRequest;
    private Verification validVerification;

    @BeforeEach
    void setUp() {
        securityService = new SecurityServiceImpl(passwordEncoder, verificationRepository, mailingService, userRepository);
        validRequest = new UserPwdRequest("test@example.com", "123456", "newpassword");
        validVerification = Verification.builder().email("test@example.com").code("123456")
                .expiryDateTime(LocalDateTime.now().plusMinutes(5))
                .id("id")
                .build();
    }

    @Test
    void updatePassword_Success() {
        User user = User.builder().id("id").email("test@example.com").password("oldpassword").build();
        User updatedUser = User.builder().id("id").email("test@example.com").password("encodedPassword").build();

        when(verificationRepository.findByEmailAndCode(anyString(), anyString())).thenReturn(Optional.of(validVerification));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doNothing().when(verificationRepository).deleteById(anyString());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        securityService.updatePassword(validRequest);

        verify(userRepository, times(1)).save(any(User.class));
        verify(verificationRepository, times(1)).deleteById(anyString());
        verify(mailingService, times(1)).sendMail(anyString(), anyString(), anyString());
    }

    @Test
    void updatePassword_CodeExpired() {
        validVerification.setExpiryDateTime(LocalDateTime.now().minusMinutes(1));
        when(verificationRepository.findByEmailAndCode(anyString(), anyString())).thenReturn(Optional.of(validVerification));
        doNothing().when(verificationRepository).deleteById(anyString());
        when(verificationRepository.save(any(Verification.class))).thenReturn(validVerification);

        assertThrows(CodeExpiredException.class, () -> securityService.updatePassword(validRequest));
        verify(verificationRepository, times(1)).deleteById(anyString());
        verify(verificationRepository, times(1)).save(any(Verification.class));
        verify(mailingService, times(1)).sendMail(anyString(), anyString(), anyString());
    }


    @Test
    void updatePassword_VerificationNotFound() {
        when(verificationRepository.findByEmailAndCode(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(VerificationNotFoundException.class, () -> securityService.updatePassword(validRequest));
    }

    @Test
    void updatePassword_UserNotFound() {
        when(verificationRepository.findByEmailAndCode(anyString(), anyString())).thenReturn(Optional.of(validVerification));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> securityService.updatePassword(validRequest));
    }

    @Test
    void requestToChangePassword_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        securityService.requestToChangePassword("test@example.com");

        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(verificationRepository, mailingService);
    }

    @Test
    void requestToChangePassword_UserFound() {
        User user = User.builder().email("test@example.com").build();
        Verification verification = Verification.builder()
                .email("test@example.com")
                .code("123456")
                .expiryDateTime(LocalDateTime.now().plusMinutes(5))
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(verificationRepository.save(any(Verification.class))).thenReturn(verification);

        securityService.requestToChangePassword("test@example.com");

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(verificationRepository, times(1)).save(any(Verification.class));
        verify(mailingService, times(1)).sendMail(anyString(), anyString(), anyString());
    }

    @Test
    void changePassword_PasswordsDoNotMatch() {
        assertThrows(IllegalArgumentException.class, () -> securityService.changePassword("test@example.com", "password1", "password2"));
    }

    @Test
    void changePassword_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> securityService.changePassword("test@example.com", "password", "password"));
    }

    @Test
    void changePassword_Success() {
        User user = User.builder().id("id").email("test@example.com").passwordNeedsToBeChanged(true).build();
        User updatedUser = User.builder().id("id").email("test@example.com").password("encodedPassword").passwordNeedsToBeChanged(false).build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        securityService.changePassword("test@example.com", "password", "password");

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(mailingService, times(1)).sendMail(anyString(), anyString(), anyString());
    }

    @Test
    void changePassword_NoPasswordChangeNeeded() {
        User user = User.builder().id("id").email("test@example.com").passwordNeedsToBeChanged(false).build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        securityService.changePassword("test@example.com", "password", "password");

        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(passwordEncoder, userRepository, mailingService);
    }
}