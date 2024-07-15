package org.mounanga.userservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.userservice.dto.UserPwdRequest;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.entity.Verification;
import org.mounanga.userservice.exception.CodeExpiredException;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.exception.VerificationNotFoundException;
import org.mounanga.userservice.repository.UserRepository;
import org.mounanga.userservice.repository.VerificationRepository;
import org.mounanga.userservice.service.SecurityService;
import org.mounanga.userservice.util.MailingService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Transactional
@Slf4j
@Service
public class SecurityServiceImpl implements SecurityService {

    private final PasswordEncoder passwordEncoder;
    private final VerificationRepository verificationRepository;
    private final MailingService mailingService;
    private final UserRepository userRepository;

    public SecurityServiceImpl(PasswordEncoder passwordEncoder, VerificationRepository verificationRepository, MailingService mailingService, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.verificationRepository = verificationRepository;
        this.mailingService = mailingService;
        this.userRepository = userRepository;
    }

    @Override
    public void updatePassword(@NotNull UserPwdRequest request) {
        log.info("Inside updatePassword method");
        Verification verification = verificationRepository.findByEmailAndCode(request.email(), request.code())
                .orElseThrow( () -> new VerificationNotFoundException("code not found"));
        if (verification.isExpired()) {
            handleExpiredVerification(verification, request.email());
        } else {
            handleValidVerification(verification, request.password());
        }
    }

    @Override
    public void requestToChangePassword(String email) {
        log.info("Inside requestToChangePassword method");
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null){
            log.warn("User with email '{}' not found",email);
        }else{
            Verification verification = Verification.builder().email(email).code(generateCode())
                    .expiryDateTime(LocalDateTime.now().plusMinutes(5))
                    .build();
            Verification savedVerification = verificationRepository.save(verification);
            log.info("Saved verification with email '{}' ", email);
            sendVerificationEmail(savedVerification);
        }
    }

    @Override
    public void changePassword(String username, @NotNull String password, String confirmPassword) {
        log.info("Inside changePassword method");
        if(!password.equals(confirmPassword)){
            throw new IllegalArgumentException("Passwords do not match");
        }
        User user = userRepository.findByEmail(username)
                .orElseThrow( () -> new UserNotFoundException("user not found"));

        if(user.isPasswordNeedsToBeChanged()){
            user.setPassword(passwordEncoder.encode(password));
            user.markPasswordAsChanged();
            User updatedUser = userRepository.save(user);
            log.info("Updated password for user with id '{}' at '{}' by '{}' ", user.getId(), updatedUser.getLastModifiedDate(), updatedUser.getLastModifiedBy());
            sendPasswordUpdatedNotification(updatedUser.getEmail(), updatedUser.getLastModifiedDate());
        }else{
            log.warn("password does not need to be changed");
        }
    }

    private void handleExpiredVerification(@NotNull Verification verification, String email) {
        verificationRepository.deleteById(verification.getId());
        Verification newVerification = new Verification();
        newVerification.setEmail(email);
        newVerification.setCode(generateCode());
        newVerification.setExpiryDateTime(LocalDateTime.now().plusMinutes(5));
        Verification savedVerification = verificationRepository.save(newVerification);
        log.info("Saved new verification with id: {}", savedVerification.getId());
        sendVerificationEmail(savedVerification);
        throw new CodeExpiredException("This verification code has expired: we have sent you a new code.");
    }

    private void handleValidVerification(@NotNull Verification verification, String newPassword) {
        User user = userRepository.findByEmail(verification.getEmail())
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);
        log.info("Password of user with id '{}' has been updated at '{}'", updatedUser.getId(), updatedUser.getLastModifiedDate());
        verificationRepository.deleteById(verification.getId());
        log.info("Deleted verification with id: {}", verification.getId());
        sendPasswordUpdatedNotification(updatedUser.getEmail(), updatedUser.getLastModifiedDate());
    }


    private @NotNull String generateCode(){
        SecureRandom secureRandom = new SecureRandom();
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendVerificationEmail(@NotNull Verification verification){
        String body = """
        Bonjour Monsieur/Madame,
        
        Voici votre nouveau code de verification %s.
        
        Ce code expire dans 5 minutes
        
        Si vous n'êtes pas à l'origine de cette connexion, veuillez contacter l'administrateur.
        """.formatted(verification.getCode());
        mailingService.sendMail(verification.getEmail(), "CODE DE VERIFICATION", body);
    }

    private void sendPasswordUpdatedNotification(String email, LocalDateTime lastModifiedDate) {
        String body = """
        Bonjour Monsieur/Madame,
        
        Vous avez modifiez votre mot de passe à %s.
        
        Si vous n'êtes pas à l'origine de cette connexion, veuillez contacter l'administrateur.
        """.formatted(lastModifiedDate);
        mailingService.sendMail(email, "URGENT : MODIFICATION DU MOT DE PASSE", body);
    }
}
