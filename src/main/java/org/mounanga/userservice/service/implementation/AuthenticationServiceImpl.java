package org.mounanga.userservice.service.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.userservice.dto.LoginRequest;
import org.mounanga.userservice.dto.LoginResponse;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.exception.UserNotAuthenticatedException;
import org.mounanga.userservice.exception.UserNotEnabledException;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.repository.UserRepository;
import org.mounanga.userservice.service.AuthenticationService;
import org.mounanga.userservice.util.ApplicationProperties;
import org.mounanga.userservice.util.MailingService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ApplicationProperties properties;
    private final MailingService mailingService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, ApplicationProperties properties, MailingService mailingService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.properties = properties;
        this.mailingService = mailingService;
    }

    @Override
    public LoginResponse authenticate(@NotNull LoginRequest request) {
        log.info("In authenticate");
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authenticateResponse = authenticationManager.authenticate(authenticationRequest);
        if (authenticateResponse.isAuthenticated()) {
            User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new UserNotFoundException("User not found"));
            if(user.isEnabled()){
                LocalDateTime loginDateTime = LocalDateTime.now();
                sendNotification(user.getEmail(), user.getFullName(), loginDateTime);
                updateLastLoginDate(user, loginDateTime);
                log.info("User logged in");
                return new LoginResponse(generateToken(user), user.isPasswordNeedsToBeChanged());
            }
            throw new UserNotEnabledException("User not enabled");
        }else{
            throw new UserNotAuthenticatedException("User not authenticated");
        }
    }


    private String generateToken(@NotNull User user) {
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        Algorithm algorithm = Algorithm.HMAC256(properties.getJwtSecret());
        Date expiration = new Date(System.currentTimeMillis() + properties.getJwtExpiration());
        return JWT.create()
                .withSubject(user.getUsername())
                .withArrayClaim("roles", roles.toArray(new String[0]))
                .withClaim("fullName", user.getFullName())
                .withExpiresAt(expiration)
                .sign(algorithm);
    }


    private void sendNotification(String email, String fullName, @NotNull LocalDateTime loginDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm:ss");
        String formattedDate = loginDate.format(formatter);
        String body = """
        Bonjour Monsieur %s,
        
        Vous venez de vous connecter à %s.
        
        Si vous n'êtes pas à l'origine de cette connexion, veuillez modifier votre mot de passe.
        """.formatted(fullName, formattedDate);
        mailingService.sendMail(email, "Notification de connexion", body);
    }

    private void updateLastLoginDate(@NotNull User user, LocalDateTime loginDateTime) {
        user.setLastLogin(loginDateTime);
        userRepository.save(user);
        log.info("Last login date updated");
    }

}
