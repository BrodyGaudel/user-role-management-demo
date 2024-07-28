package org.mounanga.userservice.service.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.userservice.configuration.ApplicationProperties;
import org.mounanga.userservice.dto.LoginRequestDTO;
import org.mounanga.userservice.dto.LoginResponseDTO;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.exception.UserNotAuthenticatedException;
import org.mounanga.userservice.exception.UserNotEnabledException;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.repository.UserRepository;
import org.mounanga.userservice.service.AuthenticationService;
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
    public LoginResponseDTO authenticate(@NotNull LoginRequestDTO request) {
        log.info("In authenticate()");
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authenticateResponse = authenticationManager.authenticate(authenticationRequest);
        if (authenticateResponse.isAuthenticated()) {
            User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new UserNotFoundException("User not found"));
            if(user.isEnabled()){
                LocalDateTime loginDateTime = LocalDateTime.now();
                sendNotification(user, loginDateTime);
                updateLastLoginDate(user, loginDateTime);
                log.info("User with id '{}' authenticated successfully at {}", user.getId(),loginDateTime);
                return new LoginResponseDTO(generateToken(user), user.isPasswordNeedsToBeChanged());
            }
            throw new UserNotEnabledException("Your are not enabled");
        }
        throw new UserNotAuthenticatedException("User not authenticated");
    }

    private void updateLastLoginDate(@NotNull User user, LocalDateTime loginDateTime) {
        user.setLastLogin(loginDateTime);
        userRepository.save(user);
    }

    private String generateToken(@NotNull User user) {
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        Algorithm algorithm = Algorithm.HMAC256(properties.getJwtSecret());
        Date expiration = new Date(System.currentTimeMillis() + properties.getJwtExpiration());
        return JWT.create()
                .withSubject(user.getUsername())
                .withArrayClaim("roles", roles.toArray(new String[0]))
                .withClaim("fullName", getFullName(user))
                .withExpiresAt(expiration)
                .sign(algorithm);
    }

    private void sendNotification(@NotNull User user, LocalDateTime loginDateTime) {
        String body = """
                Hello Madame/Monsieur %s.
                
                You have just connected on %s.
                
                If you are not the source of this manoeuvre: please change your password or contact the administrator..
                """.formatted(getFullName(user), formatedDateTime(loginDateTime));
        mailingService.sendMail(user.getEmail(), "Login Notification",body);
    }

    private @NotNull String formatedDateTime(@NotNull LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss");
        return dateTime.format(formatter);
    }

    private @NotNull String getFullName(@NotNull User user) {
        if(user.getProfile() == null){
            return "";
        }
        return user.getProfile().getFullName();
    }


}
