package org.mounanga.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.mounanga.userservice.enums.Gender;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserRequestDTO {
    @NotBlank(message = "field 'pin' is mandatory: it cannot be blank")
    private String pin;

    @NotBlank(message = "field 'email' is mandatory: it cannot be blank")
    @Email(message = "field 'email' is not well formated")
    private String email;

    @NotBlank(message = "field 'firstname' is mandatory: it cannot be blank")
    private String firstname;

    @NotBlank(message = "field 'lastname' is mandatory: it cannot be blank")
    private String lastname;

    @NotNull(message = "field 'birthday' is mandatory: it cannot be null")
    private LocalDate birthday;

    @NotBlank(message = "field 'place of birth' is mandatory: it cannot be blank")
    private String placeOfBirth;

    @NotNull(message = "field 'gender' is mandatory: it cannot be null")
    private Gender gender;

    @NotBlank(message = "field 'nationality' is mandatory: it cannot be blank")
    private String nationality;

    @NotBlank(message = "field 'username' is mandatory: it cannot be blank")
    private String username;

    @NotBlank(message = "field 'password' is mandatory: it cannot be blank")
    private String password;
}
