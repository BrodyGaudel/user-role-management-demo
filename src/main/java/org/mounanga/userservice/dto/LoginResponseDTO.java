package org.mounanga.userservice.dto;

public record LoginResponseDTO(String jwt, boolean passwordNeedsToBeChanged) {
}
