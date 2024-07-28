package org.mounanga.userservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserResponseDTO {
    private String id;
    private String email;
    private String username;
    private Boolean enabled;
    private LocalDateTime lastLogin;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createBy;
    private String lastModifiedBy;
    private ProfileResponseDTO profile;
    private List<String> roles;
}
