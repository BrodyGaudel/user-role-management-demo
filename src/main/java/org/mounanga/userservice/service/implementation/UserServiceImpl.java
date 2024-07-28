package org.mounanga.userservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.userservice.dto.*;
import org.mounanga.userservice.entity.Profile;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.exception.*;
import org.mounanga.userservice.repository.ProfileRepository;
import org.mounanga.userservice.repository.RoleRepository;
import org.mounanga.userservice.repository.UserRepository;
import org.mounanga.userservice.service.UserService;
import org.mounanga.userservice.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ProfileRepository profileRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    @Override
    public UserResponseDTO createUser(@NotNull UserRequestDTO dto) {
        log.info("In createUser()");
        validationBeforeSaved(dto.getEmail(), dto.getUsername(), dto.getPin());
        User user = Mappers.fromUserRequestDTO(dto);
        Profile profile = Mappers.fromUserProfileRequestDTO(dto);
        user.setProfile(profile);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordNeedsToBeChanged(true);
        user.setEnabled(Boolean.FALSE);
        user.setLastLogin(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        log.info("User saved with id '{}' at '{}' by '{}'", savedUser.getId(), savedUser.getCreatedDate(), savedUser.getCreateBy());
        return Mappers.fromUser(savedUser);
    }

    @Transactional
    @Override
    public UserResponseDTO updateUser(String id, @NotNull UpdateEmailUsernameDTO dto) {
        log.info("In updateUser()");
        User existingUser = findUserById(id);
        validationBeforeUpdate(existingUser, dto.email(), dto.username());
        existingUser.setUsername(dto.username());
        existingUser.setEmail(dto.email());
        User updatedUser = userRepository.save(existingUser);
        log.info("user with id '{}' updated at '{}' by '{}'", updatedUser.getId(),updatedUser.getLastModifiedDate(), updatedUser.getLastModifiedBy());
        return Mappers.fromUser(updatedUser);
    }

    @Transactional
    @Override
    public ProfileResponseDTO updateProfile(String id, @NotNull UserRequestDTO dto) {
        log.info("In updateProfile()");
        Profile profile = profileRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        profile.setFirstname(dto.getFirstname());
        profile.setLastname(dto.getLastname());
        profile.setBirthday(dto.getBirthday());
        profile.setGender(dto.getGender());
        profile.setPlaceOfBirth(dto.getPlaceOfBirth());
        profile.setNationality(dto.getNationality());
        if(!profile.getPin().equals(dto.getPin()) && profileRepository.existsByPin(dto.getPin())) {
            throw new ResourceAlreadyExistException("there is already a profile with the same pin");
        }
        profile.setPin(dto.getPin());
        Profile updatedProfile = profileRepository.save(profile);
        log.info("Profile with id '{}' updated at '{}' by '{}'", id, updatedProfile.getLastModifiedDate(), updatedProfile.getLastModifiedBy());
        return Mappers.fromUserProfile(updatedProfile);
    }

    @Override
    public UserResponseDTO getUserById(String id) {
        log.info("In getUserById()");
        User user = findUserById(id);
        log.info("User with id '{}' found", user.getId());
        return Mappers.fromUser(user);
    }

    @Override
    public PageModel<UserResponseDTO> getAllUsers(int page, int size) {
        log.info("In getAllUsers()");
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        log.info("'{}' Users found", users.getTotalElements());
        return Mappers.fromPageOfUsers(users, page);
    }

    @Override
    public PageModel<UserResponseDTO> searchUsers(String keyword, int page, int size) {
        log.info("In searchUsers()");
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.search("%"+keyword+"%", pageable);
        log.info("'{}' Users found.", users.getTotalElements());
        return Mappers.fromPageOfUsers(users, page);
    }

    @Transactional
    @Override
    public void deleteUserById(String id) {
        log.info("In deleteUserById()");
        User user = findUserById(id);
        if(user.isSuperAdmin()){
            throw new NotAuthorizedException("You cannot delete a super administrator.");
        }
        userRepository.deleteById(id);
        log.info("User with id {} deleted.", id);
    }

    @Transactional
    @Override
    public void deleteAllUsersByIds(List<String> ids) {
        log.info("In deleteAllUsersByIds()");
        List<User> users = userRepository.findAllById(ids).stream()
                .filter(user -> !user.isSuperAdmin())
                .toList();
        userRepository.deleteAll(users);
        log.info(" {} users deleted.",users.size());
    }

    @Transactional
    @Override
    public UserResponseDTO addRoleToUser(@NotNull UserRoleRequestDTO dto) {
        log.info("In addRoleToUser()");
        User user = findUserById(dto.userId());
        Role role = findRoleByName(dto.roleName());
        if(role.isSuperAdminRole()){
            throw new NotAuthorizedException("You cannot add a new super administrator.");
        }
        user.addRole(role);
        User updatedUser = userRepository.save(user);
        log.info("role '{}' has been added to user with id '{}', at '{}', by '{}'.", dto.roleName(), updatedUser.getId(), updatedUser.getLastModifiedDate(), updatedUser.getLastModifiedBy());
        return Mappers.fromUser(updatedUser);
    }

    @Transactional
    @Override
    public UserResponseDTO removeRoleFromUser(@NotNull UserRoleRequestDTO dto) {
        log.info("In removeRoleFromUser()");
        User user = findUserById(dto.userId());
        if(user.isSuperAdmin()){
            throw new NotAuthorizedException("You cannot remove a role to a super administrator.");
        }
        Role role = findRoleByName(dto.roleName());
        user.removeRole(role);
        User updatedUser = userRepository.save(user);
        log.info("role '{}' has been removed from user with id '{}', at '{}', by '{}'.", dto.roleName(), updatedUser.getId(), updatedUser.getLastModifiedDate(), updatedUser.getLastModifiedBy());
        return Mappers.fromUser(updatedUser);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        log.info("In getUserByUsername()");
        User user = userRepository.findByUsername(username)
                .orElseThrow( () -> new UsernameNotFoundException("User not found"));
        log.info("user found with id '{}'.", user.getId());
        return Mappers.fromUser(user);
    }

    private User findUserById(String id){
        return userRepository.findById(id)
                .orElseThrow( () -> new UserNotFoundException("user with id '"+id+"' not found."));
    }

    private Role findRoleByName(String roleName){
        return roleRepository.findByName(roleName)
                .orElseThrow( () -> new RoleNotFoundException("Role not found."));
    }

    private void validationBeforeSaved(String email, String username, String pin){
        List<FieldError> fieldErrors = new ArrayList<>();
        if(userRepository.existsByEmail(email)){
            fieldErrors.add(new FieldError("email","Email already exists"));
        }
        if(userRepository.existsByUsername(username)){
            fieldErrors.add(new FieldError("username","Username already exists"));
        }
        if(profileRepository.existsByPin(pin)){
            fieldErrors.add(new FieldError("pin","Pin already exists"));
        }
        if (!fieldErrors.isEmpty()) {
            throw new FieldValidationException("Validation error", fieldErrors);
        }
    }

    private void validationBeforeUpdate(@NotNull User existingUser, String email, String username) {
        List<FieldError> fieldErrors = new ArrayList<>();
        if(!existingUser.getEmail().equals(email) && userRepository.existsByEmail(email)){
                fieldErrors.add(new FieldError("email","Email already exists"));
        }
        if(!existingUser.getUsername().equals(username) && userRepository.existsByUsername(username)){
            fieldErrors.add(new FieldError("username","Username already exists"));
        }
        if (!fieldErrors.isEmpty()) {
            throw new FieldValidationException("Validation error", fieldErrors);
        }
    }
}
