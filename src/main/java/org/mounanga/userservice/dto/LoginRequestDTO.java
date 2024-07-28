package org.mounanga.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "username is mandatory: it can not be blank")
        String username,

        @NotBlank(message = "password is mandatory: it can not be blank")
        String password) {
}
