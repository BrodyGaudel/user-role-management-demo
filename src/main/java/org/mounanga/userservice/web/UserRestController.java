package org.mounanga.userservice.web;

import jakarta.validation.Valid;
import org.mounanga.userservice.dto.*;
import org.mounanga.userservice.security.SecurityInformation;
import org.mounanga.userservice.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;
    private final SecurityInformation securityInformation;

    public UserRestController(UserService userService, SecurityInformation securityInformation) {
        this.userService = userService;
        this.securityInformation = securityInformation;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PostMapping("/create")
    public UserResponseDTO createUser(@RequestBody @Valid UserRequestDTO dto) {
        return userService.createUser(dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PutMapping("/update/{id}")
    public UserResponseDTO updateUser(@PathVariable String id, @Valid @RequestBody UpdateEmailUsernameDTO dto) {
        return userService.updateUser(id, dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PutMapping("/update-profile/{id}")
    public ProfileResponseDTO updateProfile(@PathVariable String id, @Valid @RequestBody UserRequestDTO dto) {
        return userService.updateProfile(id, dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN', 'USER')")
    @GetMapping("/get/{id}")
    public UserResponseDTO getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN', 'USER')")
    @GetMapping("/list")
    public PageModel<UserResponseDTO> getAllUsers(@RequestParam(defaultValue = "0", name = "page")  int page,
                                                  @RequestParam(defaultValue = "9", name = "size")  int size) {
        return userService.getAllUsers(page, size);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN', 'USER')")
    @GetMapping("/search")
    public PageModel<UserResponseDTO> searchUsers(@RequestParam(defaultValue = "", name = "keyword") String keyword,
                                                  @RequestParam(defaultValue = "0", name = "page")  int page,
                                                  @RequestParam(defaultValue = "9", name = "size")  int size) {
        return userService.searchUsers(keyword, page, size);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void deleteUserById(@PathVariable String id) {
        userService.deleteUserById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete-all")
    public void deleteAllUsersByIds(@RequestBody List<String> ids) {
        userService.deleteAllUsersByIds(ids);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PostMapping("/add-role")
    public UserResponseDTO addRoleToUser(@RequestBody @Valid UserRoleRequestDTO dto) {
        return userService.addRoleToUser(dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PostMapping("/remove-role")
    public UserResponseDTO removeRoleFromUser(@RequestBody @Valid UserRoleRequestDTO dto) {
        return userService.removeRoleFromUser(dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN', 'USER')")
    @GetMapping("/profile")
    public UserResponseDTO getCurrentUser() {
        return userService.getUserByUsername(
                securityInformation.getCurrentUsername()
        );
    }
}
