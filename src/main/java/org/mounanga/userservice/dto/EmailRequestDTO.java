package org.mounanga.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequestDTO(
        @Email(message = "email is not well formated")
        @NotBlank(message = "email is mandatory : it can not be blank")
        String email) {
}
