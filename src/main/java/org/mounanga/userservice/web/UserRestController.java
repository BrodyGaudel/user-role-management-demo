package org.mounanga.userservice.web;

import org.mounanga.userservice.dto.PageResponse;
import org.mounanga.userservice.dto.UserRequest;
import org.mounanga.userservice.dto.UserResponse;
import org.mounanga.userservice.dto.UserRoleRequest;
import org.mounanga.userservice.service.UserService;
import org.mounanga.userservice.util.SecurityInformation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public UserResponse createUser(@RequestBody UserRequest request){
        return userService.createUser(request);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PutMapping("/update/{id}")
    public UserResponse updateUser(@PathVariable String id, @RequestBody UserRequest request){
        return userService.updateUser(id, request);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void deleteUserById(@PathVariable String id){
        userService.deleteUserById(id);
    }

    @PreAuthorize("hasAnyAuthority('USER','MODERATOR','ADMIN','SUPER_ADMIN')")
    @GetMapping("/get/{id}")
    public UserResponse getUserById(@PathVariable String id){
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAnyAuthority('USER','MODERATOR','ADMIN','SUPER_ADMIN')")
    @GetMapping("/find")
    public UserResponse getUserByUsername(){
        String username = securityInformation.getCurrentUsername();
        return userService.getUserByUsername(username);
    }

    @PreAuthorize("hasAnyAuthority('USER','MODERATOR','ADMIN','SUPER_ADMIN')")
    @GetMapping("/list")
    public PageResponse<UserResponse> getAllUsers(@RequestParam(name = "page", defaultValue = "0") int page,
                                                  @RequestParam(name = "size", defaultValue = "10") int size){
        return userService.getAllUsers(page, size);
    }

    @PreAuthorize("hasAnyAuthority('USER','MODERATOR','ADMIN','SUPER_ADMIN')")
    @GetMapping("/search")
    public PageResponse<UserResponse> searchUsers(@RequestParam(name = "keyword", defaultValue = " ") String keyword,
                                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                                  @RequestParam(name = "size", defaultValue = "10") int size){
        return userService.searchUsers(keyword, page, size);
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR','ADMIN','SUPER_ADMIN')")
    @PutMapping("/add")
    public UserResponse addRoleToUser(@RequestBody UserRoleRequest request){
        return userService.addRoleToUser(request);
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR','ADMIN','SUPER_ADMIN')")
    @PutMapping("/remove")
    public UserResponse removeRoleFromUser(@RequestBody UserRoleRequest request){
        return userService.removeRoleFromUser(request);
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR','ADMIN','SUPER_ADMIN')")
    @GetMapping("/enable/{id}")
    public UserResponse enableOrDisableUser(@PathVariable String id){
        return userService.enableOrDisableUser(id);
    }

}
