package org.mounanga.userservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.userservice.dto.EmailRequestDTO;
import org.mounanga.userservice.dto.ResetPasswordRequestDTO;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.entity.Verification;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.exception.VerificationExpiredException;
import org.mounanga.userservice.exception.VerificationNotFoundException;
import org.mounanga.userservice.repository.UserRepository;
import org.mounanga.userservice.repository.VerificationRepository;
import org.mounanga.userservice.service.PasswordService;
import org.mounanga.userservice.util.MailingService;
import org.mounanga.userservice.util.VerificationCodeGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PasswordServiceImpl implements PasswordService {

    private final VerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final MailingService mailingService;
    private final PasswordEncoder passwordEncoder;

    public PasswordServiceImpl(VerificationRepository verificationRepository, UserRepository userRepository, MailingService mailingService, PasswordEncoder passwordEncoder) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.mailingService = mailingService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void sendResetCode(@NotNull EmailRequestDTO request) {
        log.info("In sendResetCode()");
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new UserNotFoundException("User not found"));
        verificationRepository.findByEmail(request.email()).ifPresent(verification -> verificationRepository.deleteById(verification.getId()));
        saveVerification(user);
    }

    @Transactional
    @Override
    public void resetPassword(@NotNull ResetPasswordRequestDTO request) {
        log.info("In resetPassword()");
        Verification verification = verificationRepository.findByEmailAndCode(request.email(), request.code())
                .orElseThrow(() -> new VerificationNotFoundException("User not found"));
        if(verification.isExpired()) {
            handleExpiredVerification(verification);
        }else{
            changePassword(verification, request.password(), request.confirmPassword());
        }
    }

    private void handleExpiredVerification(@NotNull Verification verification) {
        log.info("### {}", verification);
        verificationRepository.deleteById(verification.getId());
        saveVerification(verification.getUser());
        throw new VerificationExpiredException("verification code expired. a new verification's code has been created")    ;
    }

    private void changePassword(@NotNull Verification verification, String password, String confirmPassword) {
        passwordValidation(password, confirmPassword);
        User user = verification.getUser();
        user.setPassword(passwordEncoder.encode(password));
        User updatedUser = userRepository.save(user);
        log.info("password changed successfully");
        String body = String.format("""
                Hello !
                Your password has just been changed at %s.
                If you did not request this, please contact the administrator.
                """,updatedUser.getLastModifiedDate());
        mailingService.sendMail(user.getEmail(), "Password Changed", body);
        verificationRepository.deleteById(verification.getId());
    }


    //PRIVATE

    private @NotNull String getFullName(@NotNull Verification verification) {
        return verification.getUser().getProfile() != null ? verification.getUser().getProfile().getFullName() : "User";
    }

    private void saveVerification(@NotNull User user) {
        Verification verification = new Verification();
        verification.setUser(user);
        verification.setCode(VerificationCodeGenerator.generateCode(6));
        verification.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        Verification savedVerification = verificationRepository.save(verification);
        log.info("verification saved");
        String body = String.format("""
                Hello %s,
                Here is your verification code to change your password: %s.
                This code expires in 10 minutes.
                If you did not request this, please contact the administrator.
                """, getFullName(savedVerification), savedVerification.getCode());
        mailingService.sendMail(user.getEmail(), "Password Reset Code", body);
    }

    private void passwordValidation(@NotNull String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if(!isValidPassword(password)){
            throw new IllegalArgumentException("Invalid password. Must contain at least one uppercase letter, one lowercase letter, and one number.");
        }
    }

    private boolean isValidPassword(final String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        return password != null && password.matches(regex);
    }

}
