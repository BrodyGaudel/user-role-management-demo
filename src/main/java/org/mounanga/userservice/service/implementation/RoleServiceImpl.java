package org.mounanga.userservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.userservice.dto.PageResponse;
import org.mounanga.userservice.dto.RoleRequest;
import org.mounanga.userservice.dto.RoleResponse;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.exception.ItemAlreadyExistException;
import org.mounanga.userservice.exception.RoleNotFoundException;
import org.mounanga.userservice.repository.RoleRepository;
import org.mounanga.userservice.service.RoleService;
import org.mounanga.userservice.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public PageResponse<RoleResponse> findAllRoles(int page, int size) {
        log.info("In findAllRoles()");
        Page<Role> roles = roleRepository.findAll(PageRequest.of(page, size));
        log.info("{} roles found", roles.getTotalElements());
        return Mappers.fromRolePage(roles, page);
    }

    @Override
    public PageResponse<RoleResponse> searchRoles(String keyword, int page, int size) {
        log.info("In searchRoles()");
        Page<Role> rolePage = roleRepository.search("%" + keyword + "%", PageRequest.of(page, size));
        log.info("{} roles found.", rolePage.getTotalElements());
        return Mappers.fromRolePage(rolePage, page);
    }

    @Override
    public RoleResponse findRoleById(Long id) {
        log.info("In findRoleById()");
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        log.info("{} role found", role);
        return Mappers.fromRole(role);
    }

    @Transactional
    @Override
    public RoleResponse createRole(@NotNull RoleRequest roleRequest) {
        log.info("In createRole()");
        if(roleRepository.existsByName(roleRequest.name())){
            throw new ItemAlreadyExistException("Role with name " + roleRequest.name() + " already exists");
        }
        Role role = Mappers.fromRoleRequest(roleRequest);
        Role savedRole = roleRepository.save(role);
        log.info("role created with id '{}' at '{}' by '{}'", savedRole.getId(), savedRole.getCreatedDate(), savedRole.getCreatedBy());
        return Mappers.fromRole(savedRole);
    }

    @Transactional
    @Override
    public RoleResponse updateRole(Long id, @NotNull RoleRequest roleRequest) {
        log.info("In updateRole()");
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        if(!role.getName().equals(roleRequest.name()) && roleRepository.existsByName(roleRequest.name())){
                throw new ItemAlreadyExistException("Role with name " + roleRequest.name() + " already exists");
        }
        role.setName(roleRequest.name());
        role.setDescription(roleRequest.description());
        Role savedRole = roleRepository.save(role);
        log.info("role with id '{}' updated at '{}' by '{}'", savedRole.getId(), savedRole.getLastModifiedDate(), savedRole.getLastModifiedBy());
        return Mappers.fromRole(savedRole);
    }

    @Override
    public void deleteRole(Long id) {
        log.info("In deleteRole()");
        roleRepository.deleteById(id);
        log.info("role with id '{}' deleted", id);
    }
}
