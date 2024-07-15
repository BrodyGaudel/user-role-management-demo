package org.mounanga.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleRequest(
        @NotBlank(message = "field 'role's name' is mandatory : it can not be blank")
        String name,
        @NotBlank(message = "field 'role's description' is mandatory : it can not be blank")
        @Size(min = 2, max = 256, message = "The size of the 'description' field is between 3 and 256.")
        String description) {
}
