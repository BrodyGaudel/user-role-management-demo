package org.mounanga.userservice.dto;

import lombok.*;
import org.mounanga.userservice.enums.Gender;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProfileResponseDTO {
    private String id;
    private String firstname;
    private String lastname;
    private LocalDate birthday;
    private String placeOfBirth;
    private Gender gender;
    private String nationality;
    private String pin;
}
