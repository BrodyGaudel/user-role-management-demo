package org.mounanga.userservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.userservice.dto.EmailRequestDTO;
import org.mounanga.userservice.dto.ResetPasswordRequestDTO;
import org.mounanga.userservice.entity.Profile;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.entity.Verification;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.exception.VerificationExpiredException;
import org.mounanga.userservice.exception.VerificationNotFoundException;
import org.mounanga.userservice.repository.UserRepository;
import org.mounanga.userservice.repository.VerificationRepository;
import org.mounanga.userservice.util.MailingService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PasswordServiceImplTest {

    @Mock
    private VerificationRepository verificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MailingService mailingService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    private User user;
    private Verification verification;
    Profile profile;

    @BeforeEach
    public void setUp() {
        passwordService = new PasswordServiceImpl(verificationRepository, userRepository, mailingService, passwordEncoder);

        profile = Profile.builder().id("id").firstname("john").lastname("doe").nationality("world").birthday(LocalDate.now())
                .pin("pin").placeOfBirth("world").createdDate(LocalDateTime.now()).createBy("admin").build();

        user = new User();
        user.setProfile(profile);
        user.setEmail("test@example.com");

        verification = new Verification();
        verification.setUser(user);
        verification.setCode("123456");
        verification.setExpiryDate(LocalDateTime.now().plusMinutes(1));
    }

    @Test
    void testSendResetCodeUserNotFound() {
        EmailRequestDTO request = new EmailRequestDTO("test@example.com");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> passwordService.sendResetCode(request));
        verify(userRepository).findByEmail(request.email());
        verify(verificationRepository, never()).findByEmail(request.email());
    }

    @Test
    void testSendResetCodeSuccess() {
        EmailRequestDTO request = new EmailRequestDTO("test@example.com");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(verificationRepository.findByEmail(request.email())).thenReturn(Optional.of(verification));
        when(verificationRepository.save(any(Verification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        passwordService.sendResetCode(request);

        verify(verificationRepository).findByEmail(request.email());
        verify(verificationRepository).deleteById(verification.getId());
        verify(verificationRepository).save(any(Verification.class));
        verify(mailingService).sendMail(eq(user.getEmail()), anyString(), anyString());
    }

    @Test
    void testResetPasswordVerificationNotFound() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO("test@example.com", "123456", "newPass", "newPass");

        when(verificationRepository.findByEmailAndCode(request.email(), request.code())).thenReturn(Optional.empty());

        assertThrows(VerificationNotFoundException.class, () -> passwordService.resetPassword(request));
        verify(verificationRepository).findByEmailAndCode(request.email(), request.code());
    }

    @Test
    void testResetPassword_VerificationExpired() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO("test@example.com", "123456", "newPass12", "newPass12");
        Verification existingVerification = new Verification();
        existingVerification.setExpiryDate(LocalDateTime.now().minusDays(1));
        existingVerification.setCode("123456");
        existingVerification.setId("id");
        existingVerification.setUser(user);
        when(verificationRepository.findByEmailAndCode(request.email(), request.code())).thenReturn(Optional.of(existingVerification));
        when(verificationRepository.save(any(Verification.class))).thenReturn(existingVerification);

       assertThrows(VerificationExpiredException.class, () -> passwordService.resetPassword(request));
    }

    @Test
    void testResetPassword_Success() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO("test@example.com", "123456", "newPass123", "newPass123");

        when(verificationRepository.findByEmailAndCode(request.email(), request.code())).thenReturn(Optional.of(verification));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(verificationRepository.save(any(Verification.class))).thenReturn(verification);

        passwordService.resetPassword(request);

        verify(verificationRepository).findByEmailAndCode(request.email(), request.code());
        verify(userRepository).save(user);
        verify(mailingService).sendMail(eq(user.getEmail()), anyString(), anyString());
        verify(verificationRepository).deleteById(verification.getId());
    }

    @Test
    void testResetPasswordPasswordsDoNotMatch() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO("test@example.com", "123456", "newPass", "differentPass");

        when(verificationRepository.findByEmailAndCode(request.email(), request.code())).thenReturn(Optional.of(verification));

        assertThrows(IllegalArgumentException.class, () -> passwordService.resetPassword(request));
        verify(verificationRepository).findByEmailAndCode(request.email(), request.code());
    }

    @Test
    void testResetPasswordInvalidPassword() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO("test@example.com", "123456", "newPass", "newPass");

        verification.setCode("123456");
        when(verificationRepository.findByEmailAndCode(request.email(), request.code())).thenReturn(Optional.of(verification));

        // Assuming isValidPassword is accessible, we could mock it if it was public or part of another service.
        assertThrows(IllegalArgumentException.class, () -> passwordService.resetPassword(request));
        verify(verificationRepository).findByEmailAndCode(request.email(), request.code());
    }

}