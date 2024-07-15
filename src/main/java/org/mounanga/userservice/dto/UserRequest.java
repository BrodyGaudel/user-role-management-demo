package org.mounanga.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.mounanga.userservice.enums.Gender;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
@ToString
public class UserRequest {
    @NotBlank(message = "field 'firstname' is mandatory : it can not be blank")
    private String firstname;

    @NotBlank(message = "field 'lastname' is mandatory : it can not be blank")
    private String lastname;

    @NotBlank(message = "field 'place of birth' is mandatory : it can not be blank")
    private String placeOfBirth;

    @NotNull(message = "field 'date of birth' is mandatory : it can not be null")
    private LocalDate dateOfBirth;

    @NotNull(message = "field 'gender' is mandatory : it can not be null")
    private Gender gender;

    @NotBlank(message = "field 'nationality' is mandatory : it can not be blank")
    private String nationality;

    @NotBlank(message = "field 'nip' is mandatory : it can not be blank")
    @Size(min = 16, max = 32,message = "field 'nip' must be at least 16 alphanumeric characters long.")
    private String nip;

    @NotBlank(message = "field 'phone' is mandatory : it can not be blank")
    private String phone;

    @NotBlank(message = "field 'email' is mandatory : it can not be blank")
    @Email(message = "field 'email' is not well formed")
    private String email;

    @NotBlank(message = "field 'username' is mandatory : it can not be blank")
    @Size(min = 4, message = "field 'username' must be at least 4 alphanumeric characters long.")
    private String username;

    @NotBlank(message = "field 'password' is mandatory : it can not be blank")
    @Size(min = 8, message = "field 'password' must be at least 8 alphanumeric characters long.")
    private String password;
}
