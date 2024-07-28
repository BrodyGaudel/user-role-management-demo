package org.mounanga.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @Email(message = "email is not well formated")
        @NotBlank(message = "field 'email' is mandatory: it can not be blank")
        String email,

        @NotBlank(message = "field 'code' is mandatory: it can not be blank")
        @Size(min = 6, max = 6, message = "field 'code' must have size equal to 6")
        String code,

        @NotBlank(message = "field 'password' is mandatory: it can not be blank")
        @Size(min =8, message = "field 'password' must have at least 8 alphanumeric characters")
        String password,

        @NotBlank(message = "field 'confirmPassword' is mandatory: it can not be blank")
        @Size(min =8, message = "field 'confirmPassword' must have at least 8 alphanumeric characters")
        String confirmPassword) {
}
