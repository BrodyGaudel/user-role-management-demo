package org.mounanga.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailUsernameDTO(
        @Email(message = "field 'email' is not well formed")
        String email,
        @NotBlank(message = "field 'username' is mandatory: it can not be blank or null")
        String username) {
}
