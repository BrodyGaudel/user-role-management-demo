package org.mounanga.userservice.dto;


import java.time.LocalDateTime;

public record RoleResponse(Long id,
                           String name,
                           String description,
                           LocalDateTime createdDate,
                           String createdBy,
                           LocalDateTime lastModifiedDate,
                           String lastModifiedBy) {
}
