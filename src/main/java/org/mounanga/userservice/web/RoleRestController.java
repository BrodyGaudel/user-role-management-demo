package org.mounanga.userservice.web;

import org.mounanga.userservice.dto.PageResponse;
import org.mounanga.userservice.dto.RoleRequest;
import org.mounanga.userservice.dto.RoleResponse;
import org.mounanga.userservice.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleRestController {

    private final RoleService roleService;

    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR','ADMIN','SUPER_ADMIN')")
    @GetMapping("/list")
    public PageResponse<RoleResponse> findAllRoles(@RequestParam(name = "page", defaultValue = "0") int page,
                                                   @RequestParam(name = "size", defaultValue = "10") int size){
        return roleService.findAllRoles(page, size);
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR','ADMIN','SUPER_ADMIN')")
    @GetMapping("/search")
    public PageResponse<RoleResponse> searchRoles(@RequestParam(name = "keyword", defaultValue = " ") String keyword,
                                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                                  @RequestParam(name = "size", defaultValue = "10") int size){
        return roleService.searchRoles(keyword, page, size);
    }

    @PreAuthorize("hasAnyAuthority('MODERATOR','ADMIN','SUPER_ADMIN')")
    @GetMapping("/get/{id}")
    public RoleResponse findRoleById(@PathVariable Long id){
        return roleService.findRoleById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PostMapping("/create")
    public RoleResponse createRole(@RequestBody RoleRequest roleRequest){
        return roleService.createRole(roleRequest);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PutMapping("/update/{id}")
    public RoleResponse updateRole(@PathVariable Long id, @RequestBody RoleRequest roleRequest){
        return roleService.updateRole(id, roleRequest);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void deleteRole(@PathVariable Long id){
        roleService.deleteRole(id);
    }
}
