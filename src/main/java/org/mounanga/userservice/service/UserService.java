package org.mounanga.userservice.service;

import org.mounanga.userservice.dto.*;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO dto);
    UserResponseDTO updateUser(String id, UpdateEmailUsernameDTO dto);
    ProfileResponseDTO updateProfile(String id, UserRequestDTO dto);
    UserResponseDTO getUserById(String id);
    PageModel<UserResponseDTO> getAllUsers(int page, int size);
    PageModel<UserResponseDTO> searchUsers(String keyword, int page, int size);
    void deleteUserById(String id);
    void deleteAllUsersByIds(List<String> ids);
    UserResponseDTO addRoleToUser(UserRoleRequestDTO dto);
    UserResponseDTO removeRoleFromUser(UserRoleRequestDTO dto);
    UserResponseDTO getUserByUsername(String username);
}
