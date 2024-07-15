package org.mounanga.userservice.service;

import org.mounanga.userservice.dto.UserPwdRequest;

public interface SecurityService {

    void updatePassword(UserPwdRequest request);
    void requestToChangePassword(String email);
    void changePassword(String username, String password, String confirmPassword);

}
