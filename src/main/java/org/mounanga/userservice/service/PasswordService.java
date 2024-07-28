package org.mounanga.userservice.service;

import org.mounanga.userservice.dto.EmailRequestDTO;
import org.mounanga.userservice.dto.ResetPasswordRequestDTO;

public interface PasswordService {

    void sendResetCode(EmailRequestDTO request);
    void resetPassword(ResetPasswordRequestDTO request);
}
