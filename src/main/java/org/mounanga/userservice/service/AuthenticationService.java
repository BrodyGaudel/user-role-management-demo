package org.mounanga.userservice.service;

import org.mounanga.userservice.dto.LoginRequestDTO;
import org.mounanga.userservice.dto.LoginResponseDTO;

public interface AuthenticationService {

    LoginResponseDTO authenticate(LoginRequestDTO request);
}
