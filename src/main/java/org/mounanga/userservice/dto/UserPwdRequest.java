package org.mounanga.userservice.dto;

public record UserPwdRequest(String email, String code, String password) {
}
