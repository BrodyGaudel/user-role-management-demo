package org.mounanga.userservice.web;

import jakarta.validation.Valid;
import org.mounanga.userservice.dto.PageModel;
import org.mounanga.userservice.dto.RoleDTO;
import org.mounanga.userservice.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleRestController {

    private final RoleService roleService;

    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PostMapping("/create")
    public RoleDTO createRole(@RequestBody @Valid RoleDTO dto) {
        return roleService.createRole(dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PutMapping("/update/{id}")
    public RoleDTO updateRole(@PathVariable Long id, @RequestBody @Valid RoleDTO dto) {
        return roleService.updateRole(id, dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN','USER')")
    @GetMapping("/get/{id}")
    public RoleDTO getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN','USER')")
    @GetMapping("/list")
    public PageModel<RoleDTO> getAllRoles(@RequestParam(defaultValue = "0", name = "page")  int page,
                                          @RequestParam(defaultValue = "9", name = "size")  int size) {
        return roleService.getAllRoles(page, size);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN','USER')")
    @GetMapping("/search")
    public PageModel<RoleDTO> searchRoles(@RequestParam(defaultValue = "", name = "keyword") String keyword,
                                          @RequestParam(defaultValue = "0", name = "page")  int page,
                                          @RequestParam(defaultValue = "9", name = "size")  int size) {
        return roleService.searchRoles(keyword, page, size);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void deleteRoleById(@PathVariable Long id) {
        roleService.deleteRoleById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete-all")
    public void deleteAllRolesByIds(@RequestBody List<Long> ids) {
        roleService.deleteAllRolesByIds(ids);
    }
}
