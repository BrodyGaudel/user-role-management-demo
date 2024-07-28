package org.mounanga.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRoleRequestDTO(
        @NotBlank(message = "field 'roleName' is mandatory: it cannot be blank")
        String roleName,

        @NotBlank(message = "field 'userId' is mandatory: it cannot be blank")
        String userId) {
}
