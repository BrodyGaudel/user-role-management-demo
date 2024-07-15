package org.mounanga.userservice.dto;

public record PwdUpdateRequest(String password, String confirmPassword) {
}
