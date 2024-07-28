package org.mounanga.userservice.web;

import jakarta.validation.Valid;
import org.mounanga.userservice.dto.LoginRequestDTO;
import org.mounanga.userservice.dto.LoginResponseDTO;
import org.mounanga.userservice.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    public AuthenticationRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public LoginResponseDTO authenticate(@RequestBody @Valid LoginRequestDTO request){
        return authenticationService.authenticate(request);
    }
}
