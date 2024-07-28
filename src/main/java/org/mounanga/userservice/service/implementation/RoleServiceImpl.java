package org.mounanga.userservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.userservice.dto.PageModel;
import org.mounanga.userservice.dto.RoleDTO;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.exception.NotAuthorizedException;
import org.mounanga.userservice.exception.ResourceAlreadyExistException;
import org.mounanga.userservice.exception.RoleNotFoundException;
import org.mounanga.userservice.repository.RoleRepository;
import org.mounanga.userservice.service.RoleService;
import org.mounanga.userservice.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Override
    public RoleDTO createRole(RoleDTO dto) {
        log.info("In createRole()");
        Role role = Mappers.fromRoleDTO(dto);
        if(roleRepository.existsByName(role.getName())) {
            throw new ResourceAlreadyExistException("Role with name " + role.getName() + " already exists");
        }
        Role savedRole = roleRepository.save(role);
        log.info("Role with name '{}' created at '{}' by '{}'", role.getName(), savedRole.getCreatedDate(), savedRole.getCreateBy());
        return Mappers.fromRole(savedRole);
    }

    @Transactional
    @Override
    public RoleDTO updateRole(Long id, @NotNull RoleDTO dto) {
        log.info("In updateRole()");
        Role role = findRoleById(id);
        if(!role.getName().equals(dto.name()) && roleRepository.existsByName(dto.name())) {
                throw new ResourceAlreadyExistException("Role with name " + dto.name() + " already exists");
        }
        role.setName(dto.name());
        role.setDescription(dto.description());
        Role updatedRole = roleRepository.save(role);
        log.info("Role with name '{}' updated at '{}' by '{}'", updatedRole.getName(), role.getLastModifiedDate(), updatedRole.getLastModifiedBy());
        return Mappers.fromRole(updatedRole);
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        log.info("In getRoleById()");
        Role role = findRoleById(id);
        log.info("Role with name '{}' found", role.getName());
        return Mappers.fromRole(role);
    }

    @Override
    public PageModel<RoleDTO> getAllRoles(int page, int size) {
        log.info("In getAllRoles()");
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> roles = roleRepository.findAll(pageable);
        log.info("{} roles found", roles.getTotalElements());
        return Mappers.fromPageOfRoles(roles,page);
    }

    @Override
    public PageModel<RoleDTO> searchRoles(String keyword, int page, int size) {
        log.info("In searchRoles()");
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> roles = roleRepository.findByNameOrDescription("%" + keyword + "%", pageable);
        log.info("{} role(s) found", roles.getTotalElements());
        return Mappers.fromPageOfRoles(roles,page);
    }

    @Transactional
    @Override
    public void deleteRoleById(Long id) {
        log.info("In deleteRoleById()");
        Role role = findRoleById(id);
        if(role.isDefaultRole()){
            throw new NotAuthorizedException("You cannot delete system roles");
        }
        roleRepository.delete(role);
        log.info("Role with name '{}' deleted", role.getName());
    }

    @Transactional
    @Override
    public void deleteAllRolesByIds(List<Long> ids) {
        log.info("In deleteAllRolesByIds()");
        List<Role> roles = roleRepository.findAllById(ids).stream()
                .filter(role -> !role.isDefaultRole())
                .toList();
        roleRepository.deleteAll(roles);
        log.info("{} role(s) deleted", roles.size());
    }

    private Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow( () -> new RoleNotFoundException("Role with id " + id + " not found"));
    }
}
