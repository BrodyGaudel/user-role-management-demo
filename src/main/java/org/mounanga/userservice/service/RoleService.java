package org.mounanga.userservice.service;

import org.mounanga.userservice.dto.PageModel;
import org.mounanga.userservice.dto.RoleDTO;

import java.util.List;

public interface RoleService {
    RoleDTO createRole(RoleDTO dto);
    RoleDTO updateRole(Long id, RoleDTO dto);
    RoleDTO getRoleById(Long id);
    PageModel<RoleDTO> getAllRoles(int page, int size);
    PageModel<RoleDTO> searchRoles(String keyword, int page, int size);
    void deleteRoleById(Long id);
    void deleteAllRolesByIds(List<Long> ids);
}
