package org.mounanga.userservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.userservice.dto.*;
import org.mounanga.userservice.entity.Profile;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.exception.FieldValidationException;
import org.mounanga.userservice.exception.NotAuthorizedException;
import org.mounanga.userservice.exception.RoleNotFoundException;
import org.mounanga.userservice.exception.UserNotFoundException;
import org.mounanga.userservice.repository.ProfileRepository;
import org.mounanga.userservice.repository.RoleRepository;
import org.mounanga.userservice.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    UserRequestDTO dto;
    Profile profile;

    @BeforeEach
    void setUp() {
        this.userService = new UserServiceImpl(userRepository, profileRepository, roleRepository, passwordEncoder);
        dto = UserRequestDTO.builder().firstname("john").lastname("doe").email("johndoe2024@gmail.com").pin("pin")
                .password("password").nationality("world").birthday(LocalDate.now()).username("johndoe2024")
                .placeOfBirth("world").build();
        profile = Profile.builder().id("id").firstname("john").lastname("doe").nationality("world").birthday(LocalDate.now())
                .pin("pin").placeOfBirth("world").createdDate(LocalDateTime.now()).createBy("admin").build();
    }

    @Test
    void createUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(profileRepository.existsById(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(User.builder().email("johndoe2024@gmail.com").id("id")
                .username("johndoe2024").password("encoded_password").createdDate(LocalDateTime.now()).createBy("admin")
                .profile(profile).build()
        );
        UserResponseDTO response = userService.createUser(dto);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getProfile());
        assertEquals(dto.getFirstname(), response.getProfile().getFirstname());
        assertEquals(dto.getLastname(), response.getProfile().getLastname());
        assertEquals(dto.getPlaceOfBirth(), response.getProfile().getPlaceOfBirth());
        assertEquals(dto.getBirthday(), response.getProfile().getBirthday());
        assertEquals(dto.getPin(), response.getProfile().getPin());
        assertEquals(dto.getNationality(), response.getProfile().getNationality());
        assertEquals(dto.getUsername(), response.getUsername());
        assertEquals(dto.getGender(), response.getProfile().getGender());
        assertEquals(dto.getEmail(), response.getEmail());
    }

    @Test
    void createUserThrowsFieldValidationException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        when(profileRepository.existsById(anyString())).thenReturn(true);
        assertThrows(FieldValidationException.class, () -> userService.createUser(dto));
    }

    @Test
    void updateUser() {
        String id = "id";
        UpdateEmailUsernameDTO request = new UpdateEmailUsernameDTO("johndoe2024@gmail.com", "johndoe2024");
        User existingUser = User.builder().id(id).profile(profile).email("old@email.com").username("old_username").build();
        User updatedUser = User.builder().id(id).profile(profile).email("johndoe2024@gmail.com").username("johndoe2024").build();
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponseDTO response = userService.updateUser(id, request);
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(request.email(), response.getEmail());
        assertEquals(request.username(), response.getUsername());
    }

    @Test
    void updateProfile() {
        when(profileRepository.findById(anyString())).thenReturn(Optional.of(profile));
        when(profileRepository.existsByPin(anyString())).thenReturn(false);
        when(profileRepository.save(any(Profile.class))).thenReturn( Profile.builder().id("id").firstname("john_old").lastname("doe_old").nationality("world_old")
                .birthday(LocalDate.now().minusDays(1)).pin("pin_old").placeOfBirth("world_old").build());

        String id = "id";
        ProfileResponseDTO response = userService.updateProfile(id,dto);
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertNotEquals(response.getFirstname(), dto.getFirstname());
        assertNotEquals(response.getLastname(), dto.getLastname());
        assertNotEquals(response.getPlaceOfBirth(), dto.getPlaceOfBirth());
        assertNotEquals(response.getBirthday(), dto.getBirthday());
        assertNotEquals(response.getPin(), dto.getPin());
        assertNotEquals(response.getNationality(), dto.getNationality());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(
                User.builder().email("johndoe2024@gmail.com").id("id").username("johndoe2024").password("encoded_password")
                        .createdDate(LocalDateTime.now()).createBy("admin").profile(profile).build()
        ));
        String id = "id";
        UserResponseDTO response = userService.getUserById(id);
        assertNotNull(response);
        assertEquals(id, response.getId());
    }

    @Test
    void getUserByIdThrowsUserNotFoundException() {
        String id = "id";
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void getAllUsers() {
        int page =0;
        int size = 2;
        List<User> users = new ArrayList<>();
        users.add(User.builder().profile(profile).id("id1").build());
        users.add(User.builder().profile(profile).id("id2").build());
        Page<User> userPage = new PageImpl<>(users);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        PageModel<UserResponseDTO> userPageModel = userService.getAllUsers(page,size);
        assertNotNull(userPageModel);
        assertNotNull(userPageModel.getContent());
        assertFalse(userPageModel.getContent().isEmpty());
    }

    @Test
    void searchUsers() {
        int page =0;
        int size = 2;
        String keyword = "joh";
        List<User> users = new ArrayList<>();
        users.add(User.builder().profile(profile).id("id1").build());
        users.add(User.builder().profile(profile).id("id2").build());
        Page<User> userPage = new PageImpl<>(users);
        when(userRepository.search(anyString(),any(Pageable.class))).thenReturn(userPage);
        PageModel<UserResponseDTO> userPageModel = userService.searchUsers(keyword,page,size);
        assertNotNull(userPageModel);
        assertNotNull(userPageModel.getContent());
        assertFalse(userPageModel.getContent().isEmpty());
        assertTrue(userPageModel.getContent().getFirst().getProfile().getFirstname().contains(keyword) || userPageModel.getContent().getFirst().getProfile().getLastname().contains(keyword));
        assertTrue(userPageModel.getContent().getLast().getProfile().getFirstname().contains(keyword) || userPageModel.getContent().getLast().getProfile().getLastname().contains(keyword));
    }

    @Test
    void deleteUserById() {
        String id = "id";
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("USER").build());
        roles.add(Role.builder().name("ADMIN").build());
        User existingUser = User.builder().id(id).profile(profile).roles(roles).build();
        when(userRepository.findById(anyString())).thenReturn(Optional.of(existingUser));

        userService.deleteUserById(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteUserByIdThrowsUserNotFoundException() {
        String id = "id";
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(id));
    }

    @Test
    void deleteUserByIdThrowsNotAuthorizedException() {
        String id = "id";
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("USER").build());
        roles.add(Role.builder().name("ADMIN").build());
        roles.add(Role.builder().name("SUPER_ADMIN").build());
        User existingUser = User.builder().id(id).profile(profile).roles(roles).build();

        when(userRepository.findById(anyString())).thenReturn(Optional.of(existingUser));
        assertThrows(NotAuthorizedException.class, () -> userService.deleteUserById(id));
    }

    @Test
    void deleteAllUsersByIds() {
        List<String> ids = new ArrayList<>();
        ids.add("id1");
        ids.add("id2");

        List<Role> roles1 = new ArrayList<>();
        roles1.add(Role.builder().name("USER").build());
        roles1.add(Role.builder().name("ADMIN").build());
        roles1.add(Role.builder().name("SUPER_ADMIN").build());

        List<Role> roles2 = new ArrayList<>();
        roles2.add(Role.builder().name("USER").build());
        roles2.add(Role.builder().name("ADMIN").build());

        User existingUser1 = User.builder().id("id1").profile(new Profile()).roles(roles1).build();
        User existingUser2 = User.builder().id("id2").profile(new Profile()).roles(roles2).build();
        List<User> users = new ArrayList<>();
        users.add(existingUser1);
        users.add(existingUser2);

        when(userRepository.findAllById(anyList())).thenReturn(users);
        userService.deleteAllUsersByIds(ids);
        verify(userRepository, times(1)).deleteAllById(ids);

    }

    @Test
    void addRoleToUser() {
        // Arrange
        UserRoleRequestDTO requestDTO = new UserRoleRequestDTO("ADMIN", "userId");
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("USER").build());
        User existingUser = User.builder().id("userId").profile(profile).roles(roles).build();
        List<Role> newRoles = List.of(Role.builder().name("USER").build(), Role.builder().name("ADMIN").build());
        User updatedUser = User.builder().id("userId").profile(profile).roles(newRoles).build();

        when(userRepository.findById(anyString())).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(Role.builder().name("ADMIN").build()));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponseDTO response = userService.addRoleToUser(requestDTO);
        assertNotNull(response);
        assertEquals(response.getId(), requestDTO.userId());
        assertNotNull(response.getRoles());
        assertEquals(2, response.getRoles().size());
    }

    @Test
    void addRoleToUserThrowsUserNotFoundException() {
        UserRoleRequestDTO requestDTO = new UserRoleRequestDTO("ADMIN", "userId");
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.addRoleToUser(requestDTO));
    }

    @Test
    void addRoleToUserThrowsRoleNotFoundException() {
        UserRoleRequestDTO requestDTO = new UserRoleRequestDTO("ADMIN", "userId");
        when(userRepository.findById(anyString())).thenReturn(Optional.of(User.builder().profile(profile).build()));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThrows(RoleNotFoundException.class, () -> userService.addRoleToUser(requestDTO));
    }

    @Test
    void addRoleToUserThrowsNotAuthorizedException() {
        UserRoleRequestDTO requestDTO = new UserRoleRequestDTO("SUPER_ADMIN", "userId");
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("ADMIN").build());
        Role existingRole = Role.builder().name("SUPER_ADMIN").build();

        when(userRepository.findById(anyString())).thenReturn(Optional.of(User.builder().roles(roles).profile(profile).build()));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(existingRole));
        assertThrows(NotAuthorizedException.class, () -> userService.addRoleToUser(requestDTO));
    }

    @Test
    void removeRoleFromUser() {
        UserRoleRequestDTO requestDTO = new UserRoleRequestDTO("ADMIN", "userId");
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("USER").build());
        roles.add(Role.builder().name("ADMIN").build());
        User existingUser = User.builder().id("userId").profile(profile).roles(roles).build();
        List<Role> newRoles = List.of(Role.builder().name("USER").build());
        User updatedUser = User.builder().id("userId").profile(profile).roles(newRoles).build();

        when(userRepository.findById(anyString())).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(Role.builder().name("ADMIN").build()));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponseDTO response = userService.removeRoleFromUser(requestDTO);
        assertNotNull(response);
        assertEquals(response.getId(), requestDTO.userId());
        assertNotNull(response.getRoles());
        assertEquals(1, response.getRoles().size());
        assertEquals("USER", response.getRoles().getFirst());
    }

    @Test
    void removeRoleFromUserThrowsUserNotFoundException() {
        UserRoleRequestDTO requestDTO = new UserRoleRequestDTO("ADMIN", "userId");
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.removeRoleFromUser(requestDTO));
    }

    @Test
    void removeRoleFromUserThrowsNotAuthorizedException() {
        UserRoleRequestDTO requestDTO = new UserRoleRequestDTO("ADMIN", "userId");
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("USER").build());
        roles.add(Role.builder().name("SUPER_ADMIN").build());
        User existingUser = User.builder().id("userId").profile(profile).roles(roles).build();
        when(userRepository.findById(anyString())).thenReturn(Optional.of(existingUser));
        assertThrows(NotAuthorizedException.class, () -> userService.removeRoleFromUser(requestDTO));
    }

    @Test
    void removeRoleFromUserThrowsRoleNotFoundException() {
        UserRoleRequestDTO requestDTO = new UserRoleRequestDTO("MODERATOR", "userId");
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("USER").build());
        User existingUser = User.builder().id("userId").profile(profile).roles(roles).build();
        when(userRepository.findById(anyString())).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThrows(RoleNotFoundException.class, () -> userService.removeRoleFromUser(requestDTO));
    }

}