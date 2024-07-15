package org.mounanga.userservice.service;

import org.mounanga.userservice.dto.PageResponse;
import org.mounanga.userservice.dto.RoleRequest;
import org.mounanga.userservice.dto.RoleResponse;


public interface RoleService {

    PageResponse<RoleResponse> findAllRoles(int page, int size);
    PageResponse<RoleResponse> searchRoles(String keyword, int page, int size);
    RoleResponse findRoleById(Long id);
    RoleResponse createRole(RoleRequest roleRequest);
    RoleResponse updateRole(Long id, RoleRequest roleRequest);
    void deleteRole(Long id);
}
