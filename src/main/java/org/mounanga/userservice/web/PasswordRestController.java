package org.mounanga.userservice.web;

import jakarta.validation.Valid;
import org.mounanga.userservice.dto.EmailRequestDTO;
import org.mounanga.userservice.dto.ResetPasswordRequestDTO;
import org.mounanga.userservice.service.PasswordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pwd")
public class PasswordRestController {

    private final PasswordService passwordService;

    public PasswordRestController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping("/send")
    public void sendResetCode(@RequestBody @Valid EmailRequestDTO request){
        passwordService.sendResetCode(request);
    }

    @PostMapping("/reset")
    public void resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request){
        passwordService.resetPassword(request);
    }
}
