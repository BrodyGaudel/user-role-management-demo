package org.mounanga.userservice.util;

import org.mounanga.userservice.dto.*;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

public class Mappers {


    private Mappers(){
        super();
    }

    public static User fromUserRequest(final UserRequest request) {
        if(request == null){
            return null;
        }
        final User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPlaceOfBirth(request.getPlaceOfBirth());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setNationality(request.getNationality());
        user.setGender(request.getGender());
        user.setNip(request.getNip());
        user.setEnabled(Boolean.TRUE);
        user.setPasswordNeedsToBeChanged(true);
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }

    public static UserResponse fromUser(final User user) {
        if(user == null){
            return null;
        }
        final UserResponse userResponse = new UserResponse();
        userResponse.setFirstname(user.getFirstname());
        userResponse.setLastname(user.getLastname());
        userResponse.setPlaceOfBirth(user.getPlaceOfBirth());
        userResponse.setDateOfBirth(user.getDateOfBirth());
        userResponse.setNationality(user.getNationality());
        userResponse.setGender(user.getGender());
        userResponse.setEnabled(user.getEnabled());
        userResponse.setNip(user.getNip());
        userResponse.setUsername(user.getUsername());
        userResponse.setPhone(user.getPhone());
        userResponse.setEmail(user.getEmail());
        userResponse.setLastLogin(user.getLastLogin());
        userResponse.setCreatedBy(user.getCreatedBy());
        userResponse.setCreatedDate(user.getCreatedDate());
        userResponse.setLastModifiedBy(user.getLastModifiedBy());
        userResponse.setLastModifiedDate(user.getLastModifiedDate());
        userResponse.setRoles(rolesToStringList(user.getRoles()));
        return userResponse;
    }

    public static PageResponse<UserResponse> fromUserPage(final Page<User> userPage,final int page) {
        if(userPage == null){
            return null;
        }
        final PageResponse<UserResponse> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(userPage.getTotalElements());
        pageResponse.setTotalPages(userPage.getTotalPages());
        pageResponse.setNumbers(userPage.getNumber());
        pageResponse.setNumberOfElements(userPage.getNumberOfElements());
        pageResponse.setSize(userPage.getSize());
        pageResponse.setPage(page);
        pageResponse.setLast(userPage.isLast());
        pageResponse.setFirst(userPage.isFirst());
        pageResponse.setHasPrevious(userPage.hasPrevious());
        pageResponse.setHasNext(userPage.hasNext());
        pageResponse.setHasContent(userPage.hasContent());
        pageResponse.setContent(fromUserList(userPage.getContent()));
        return pageResponse;
    }

    public static List<UserResponse> fromUserList(final List<User> userList) {
        if(userList == null || userList.isEmpty()){
            return Collections.emptyList();
        }
        return userList.stream().map(Mappers::fromUser).toList();
    }


    public static Role fromRoleRequest(final RoleRequest request) {
        if(request == null){
            return null;
        }
        final Role role = new Role();
        role.setName(request.name());
        role.setDescription(request.description());
        return role;
    }

    public static RoleResponse fromRole(final Role role) {
       if(role == null){
           return null;
       }
        return new RoleResponse(
               role.getId(), role.getName(),
               role.getDescription(),
               role.getCreatedDate(),
               role.getCreatedBy(),
               role.getLastModifiedDate(),
               role.getLastModifiedBy()
       );
    }

    public static List<RoleResponse> fromRoleList(final List<Role> roleList) {
        if(roleList == null || roleList.isEmpty()){
            return Collections.emptyList();
        }
        return roleList.stream().map(Mappers::fromRole).toList();
    }

    public static PageResponse<RoleResponse> fromRolePage(final Page<Role> rolePage,final int page) {
        if(rolePage == null){
            return null;
        }
        final PageResponse<RoleResponse> pageResponse = new PageResponse<>();
        pageResponse.setTotalPages(rolePage.getTotalPages());
        pageResponse.setNumbers(rolePage.getNumber());
        pageResponse.setNumberOfElements(rolePage.getNumberOfElements());
        pageResponse.setTotalElements(rolePage.getTotalElements());
        pageResponse.setSize(rolePage.getSize());
        pageResponse.setPage(page);
        pageResponse.setFirst(rolePage.isFirst());
        pageResponse.setLast(rolePage.isLast());
        pageResponse.setHasPrevious(rolePage.hasPrevious());
        pageResponse.setHasNext(rolePage.hasNext());
        pageResponse.setHasContent(rolePage.hasContent());
        pageResponse.setContent(fromRoleList(rolePage.getContent()));
        return pageResponse;
    }

    private static List<String> rolesToStringList(final List<Role> roles) {
        if(roles == null || roles.isEmpty()){
            return Collections.emptyList();
        }
        return roles.stream().map(Role::getName).toList();
    }


}
