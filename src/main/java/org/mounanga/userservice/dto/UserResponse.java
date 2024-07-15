package org.mounanga.userservice.dto;

import lombok.*;
import org.mounanga.userservice.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserResponse {
    private String id;
    private String firstname;
    private String lastname;
    private String placeOfBirth;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String nationality;
    private String nip;
    private String phone;
    private String email;
    private String username;
    private Boolean enabled;
    private LocalDateTime lastLogin;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
    private boolean passwordNeedsToBeChanged;
    private List<String> roles;
}
