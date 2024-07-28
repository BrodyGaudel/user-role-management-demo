package org.mounanga.userservice.util;

import org.jetbrains.annotations.NotNull;
import org.mounanga.userservice.dto.*;
import org.mounanga.userservice.entity.Profile;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public class Mappers {

    private Mappers() {
        super();
    }

    public static @NotNull User fromUserRequestDTO(final @NotNull UserRequestDTO userResponseDTO) {
        final User user = new User();
        user.setUsername(userResponseDTO.getUsername());
        user.setPassword(userResponseDTO.getPassword());
        user.setEmail(userResponseDTO.getEmail());
        user.setEnabled(Boolean.FALSE);
        return user;
    }

    public static UserResponseDTO fromUser(final User user) {
        if(user == null) {
            return null;
        }
        final UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setEnabled(user.getEnabled());
        userDTO.setCreateBy(user.getCreateBy());
        userDTO.setCreatedDate(user.getCreatedDate());
        userDTO.setLastModifiedBy(user.getLastModifiedBy());
        userDTO.setLastModifiedDate(user.getLastModifiedDate());
        userDTO.setLastLogin(user.getLastLogin());
        userDTO.setRoles(rolesToStringList(user.getRoles()));
        userDTO.setProfile(fromProfile(user.getProfile()));
        return userDTO;
    }

    public static List<UserResponseDTO> fromUserList(final List<User> userList) {
        if(userList == null || userList.isEmpty()) {
            return List.of();
        }
        return userList.stream().map(Mappers::fromUser).toList();
    }

    public static @NotNull Profile fromUserProfileRequestDTO(final @NotNull UserRequestDTO userRequestDTO) {
        final Profile profile = new Profile();
        profile.setLastname(userRequestDTO.getLastname());
        profile.setPlaceOfBirth(userRequestDTO.getPlaceOfBirth());
        profile.setNationality(userRequestDTO.getNationality());
        profile.setPin(userRequestDTO.getPin());
        profile.setBirthday(userRequestDTO.getBirthday());
        profile.setFirstname(userRequestDTO.getFirstname());
        profile.setGender(userRequestDTO.getGender());
        return profile;
    }

    public static @NotNull ProfileResponseDTO fromUserProfile(final @NotNull Profile profile) {
        final ProfileResponseDTO userProfileDTO = new ProfileResponseDTO();
        userProfileDTO.setLastname(profile.getLastname());
        userProfileDTO.setBirthday(profile.getBirthday());
        userProfileDTO.setFirstname(profile.getFirstname());
        userProfileDTO.setGender(profile.getGender());
        userProfileDTO.setPlaceOfBirth(profile.getPlaceOfBirth());
        userProfileDTO.setNationality(profile.getNationality());
        userProfileDTO.setPin(profile.getPin());
        userProfileDTO.setId(profile.getId());
        userProfileDTO.setPin(profile.getPin());
        return userProfileDTO;
    }

    public static RoleDTO fromRole(final Role role){
        if(role == null){
            return null;
        }
        return new RoleDTO(role.getId(), role.getName(), role.getDescription());
    }

    public static List<RoleDTO> fromListOfRole(final List<Role> roles){
        if(roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream().map(Mappers::fromRole).toList();
    }

    public static @NotNull Role fromRoleDTO(final @NotNull RoleDTO roleDTO) {
        final Role role = new Role();
        role.setName(roleDTO.name());
        role.setDescription(roleDTO.description());
        return role;
    }

    public static PageModel<UserResponseDTO> fromPageOfUsers(final Page<User> entityPage, final int page) {
        if(entityPage == null) {
            return null;
        }
        final PageModel<UserResponseDTO> pageModel = new PageModel<>();
        pageModel.setTotalElements(entityPage.getTotalElements());
        pageModel.setHasNext(entityPage.hasNext());
        pageModel.setHasPrevious(entityPage.hasPrevious());
        pageModel.setHasContent(entityPage.hasContent());
        pageModel.setNumbers(entityPage.getNumber());
        pageModel.setNumberOfElements(entityPage.getNumberOfElements());
        pageModel.setPage(page);
        pageModel.setTotalPages(entityPage.getTotalPages());
        pageModel.setSize(entityPage.getSize());
        pageModel.setLast(entityPage.isLast());
        pageModel.setFirst(entityPage.isFirst());
        pageModel.setContent(fromUserList(entityPage.getContent()));
        return pageModel;
    }

    public static PageModel<RoleDTO> fromPageOfRoles(final Page<Role> entityPage, final int page) {
        if(entityPage == null) {
            return null;
        }
        final PageModel<RoleDTO> pageModel = new PageModel<>();
        pageModel.setTotalElements(entityPage.getTotalElements());
        pageModel.setTotalPages(entityPage.getTotalPages());
        pageModel.setSize(entityPage.getSize());
        pageModel.setLast(entityPage.isLast());
        pageModel.setFirst(entityPage.isFirst());
        pageModel.setHasNext(entityPage.hasNext());
        pageModel.setHasPrevious(entityPage.hasPrevious());
        pageModel.setHasContent(entityPage.hasContent());
        pageModel.setNumbers(entityPage.getNumber());
        pageModel.setNumberOfElements(entityPage.getNumberOfElements());
        pageModel.setPage(page);
        pageModel.setContent(fromListOfRole(entityPage.getContent()));
        return pageModel;
    }

    private static List<String> rolesToStringList(final List<Role> roles) {
        if(roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream().map(Role::getName).toList();
    }

    private static ProfileResponseDTO fromProfile(final Profile profile) {
        if(profile == null) {
            return null;
        }
        final ProfileResponseDTO profileDTO = new ProfileResponseDTO();
        profileDTO.setId(profile.getId());
        profileDTO.setFirstname(profile.getFirstname());
        profileDTO.setLastname(profile.getLastname());
        profileDTO.setBirthday(profile.getBirthday());
        profileDTO.setGender(profile.getGender());
        profileDTO.setPlaceOfBirth(profile.getPlaceOfBirth());
        profileDTO.setNationality(profile.getNationality());
        profileDTO.setPin(profile.getPin());
        return profileDTO;
    }
}
