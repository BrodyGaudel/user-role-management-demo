package org.mounanga.userservice.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
@Getter
public class ApplicationProperties {

    @Value("${application.security.jwt.secret}")
    private String jwtSecret;

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;

    @Value("${application.mail.email-system}")
    private String emailSystem;

    @Value("${application.mail.super-user}")
    private String superUserEmail;

}
