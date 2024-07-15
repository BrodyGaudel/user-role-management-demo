package org.mounanga.userservice.service;

import org.mounanga.userservice.dto.PageResponse;
import org.mounanga.userservice.dto.UserRequest;
import org.mounanga.userservice.dto.UserResponse;
import org.mounanga.userservice.dto.UserRoleRequest;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(String id, UserRequest request);
    void deleteUserById(String id);
    UserResponse getUserById(String id);
    UserResponse getUserByUsername(String username);
    PageResponse<UserResponse> getAllUsers(int page, int size);
    PageResponse<UserResponse> searchUsers(String keyword, int page, int size);
    UserResponse addRoleToUser(UserRoleRequest request);
    UserResponse removeRoleFromUser(UserRoleRequest request);
    UserResponse enableOrDisableUser(String id);
}
