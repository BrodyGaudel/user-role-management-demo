package org.mounanga.userservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.userservice.dto.PageResponse;
import org.mounanga.userservice.dto.UserRequest;
import org.mounanga.userservice.dto.UserResponse;
import org.mounanga.userservice.dto.UserRoleRequest;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.exception.ItemAlreadyExistException;
import org.mounanga.userservice.exception.RoleNotFoundException;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.repository.RoleRepository;
import org.mounanga.userservice.repository.UserRepository;
import org.mounanga.userservice.service.UserService;
import org.mounanga.userservice.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public UserResponse createUser(UserRequest request) {
        log.info("In createUser()");
        User user = Mappers.fromUserRequest(request);
        validateUniqueFields(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("user saved with id '{}' at '{}' by '{}'.", savedUser.getId(), savedUser.getCreatedDate(), user.getCreatedBy());
        Role defaultRole = getDefaultRole();
        savedUser.addRole(defaultRole);
        User reSavedUser = userRepository.save(savedUser);
        log.info("default role at to user with id '{}' at '{}' by '{}'.", savedUser.getId(), savedUser.getLastModifiedDate(), user.getLastModifiedBy());
        return Mappers.fromUser(reSavedUser);
    }

    @Override
    public UserResponse updateUser(String id, @NotNull UserRequest request) {
        log.info("In updateUser()");
        User user = findUserById(id);
        updateUserItems(user, request);
        User updatedUser = userRepository.save(user);
        log.info("user updated with id {} updated at '{}' by '{}'.", updatedUser.getId(), updatedUser.getLastModifiedDate(), user.getLastModifiedBy());
        return Mappers.fromUser(updatedUser);
    }

    @Override
    public void deleteUserById(String id) {
        log.info("In deleteUserById()");
        userRepository.deleteById(id);
        log.info("user with id '{}' deleted.",id);
    }

    @Override
    public UserResponse getUserById(String id) {
        log.info("In getUserById()");
        User user = findUserById(id);
        log.info("user with id '{}' found.", id);
        return Mappers.fromUser(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        log.info("In getUserByUsername()");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        log.info("user with username '{}' found.", username);
        return Mappers.fromUser(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        log.info("In getAllUsers()");
        Page<User> userPage = userRepository.findAll(PageRequest.of(page,size));
        log.info("{} users found.", userPage.getTotalElements());
        return Mappers.fromUserPage(userPage, page);
    }

    @Override
    public PageResponse<UserResponse> searchUsers(String keyword, int page, int size) {
        log.info("In searchUsers()");
        Page<User> userPage = userRepository.search("%"+keyword+"%", PageRequest.of(page,size));
        log.info("{} users found", userPage.getTotalElements());
        return Mappers.fromUserPage(userPage, page);
    }

    @Transactional
    @Override
    public UserResponse addRoleToUser(@NotNull UserRoleRequest request) {
        log.info("In addRoleToUser()");
        User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new UserNotFoundException("User not found"));
        Role role = roleRepository.findByName(request.roleName()).orElseThrow(() -> new RoleNotFoundException("Role not found"));
        user.addRole(role);
        User updatedUser = userRepository.save(user);
        log.info("role '{}' added to user '{}' at '{}' by '{}'",request.roleName(), updatedUser.getId(), updatedUser.getLastModifiedDate(), user.getLastModifiedBy());
        return Mappers.fromUser(updatedUser);
    }

    @Transactional
    @Override
    public UserResponse removeRoleFromUser(@NotNull UserRoleRequest request) {
        log.info("In removeRoleFromUser()");
        User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new UserNotFoundException("User not found"));
        Role role = roleRepository.findByName(request.roleName()).orElseThrow(() -> new RoleNotFoundException("Role not found"));
        user.removeRole(role);
        User updatedUser = userRepository.save(user);
        log.info("role '{}' removed from user '{}' at '{}' by '{}'",request.roleName(), updatedUser.getId(), updatedUser.getLastModifiedDate(), user.getLastModifiedBy());
        return Mappers.fromUser(updatedUser);
    }

    @Transactional
    @Override
    public UserResponse enableOrDisableUser(String id) {
        log.info("In enableOrDisableUser()");
        User user = findUserById(id);
        if(user.isEnabled()){
            user.setEnabled(false);
            User updatedUser = userRepository.save(user);
            log.info("user with id '{}' disabled at '{}' by '{}'",id, updatedUser.getLastModifiedDate(),updatedUser.getLastModifiedBy());
            return Mappers.fromUser(updatedUser);
        }else{
            user.setEnabled(true);
            User updatedUser = userRepository.save(user);
            log.info("user with id '{}' enabled at '{}' by '{}'",id, updatedUser.getLastModifiedDate(),updatedUser.getLastModifiedBy());
            return Mappers.fromUser(updatedUser);
        }
    }

    private Role getDefaultRole() {
        return roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("No default role found"));
    }

    private User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user found with id '"+id+"'."));
    }



    private void validateUniqueFields(@NotNull User user) {
        if (userRepository.existsByNip(user.getNip())) {
            throw new ItemAlreadyExistException("NIP already exists");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new ItemAlreadyExistException("Phone number already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ItemAlreadyExistException("Email already exists");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ItemAlreadyExistException("Username already exists");
        }
    }

    private void updateUserItems(@NotNull User user, @NotNull UserRequest request) {
        if(!user.getNip().equals(request.getNip()) && userRepository.existsByNip(user.getNip())) {
            throw new ItemAlreadyExistException("NIP already exists");
        }
        if(!user.getPhone().equals(request.getPhone()) && userRepository.existsByPhone(user.getPhone())) {
            throw new ItemAlreadyExistException("Phone number already exists");
        }
        if(!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new ItemAlreadyExistException("Email already exists");
        }
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPlaceOfBirth(request.getPlaceOfBirth());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setNip(request.getNip());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setNationality(request.getNationality());
    }
}
