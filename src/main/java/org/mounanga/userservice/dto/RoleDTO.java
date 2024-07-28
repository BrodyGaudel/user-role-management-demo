package org.mounanga.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleDTO(Long id,
                      @NotBlank(message = "field 'name' is mandatory: it cannot be blank")
                      String name,

                      @NotBlank(message = "field 'description' is mandatory: it cannot be blank")
                      String description) {
}
