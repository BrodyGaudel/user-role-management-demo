package org.mounanga.userservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.userservice.dto.RoleDTO;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.exception.ResourceAlreadyExistException;
import org.mounanga.userservice.exception.RoleNotFoundException;
import org.mounanga.userservice.repository.RoleRepository;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(roleRepository);
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.setDescription("Administrator role");

        roleDTO = new RoleDTO(null, "ADMIN","ROLE_ADMIN");
    }

    @Test
    void createRoleRoleAlreadyExistsThrowsResourceAlreadyExistException() {
        when(roleRepository.existsByName(role.getName())).thenReturn(true);

        ResourceAlreadyExistException exception = assertThrows(ResourceAlreadyExistException.class, () -> roleService.createRole(roleDTO));

        assertEquals("Role with name ADMIN already exists", exception.getMessage());
    }

    @Test
    void createRoleSuccess() {
        when(roleRepository.existsByName(role.getName())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDTO result = roleService.createRole(roleDTO);

        assertNotNull(result);
        assertEquals("ADMIN", result.name());
    }

    @Test
    void updateRoleRoleNotFoundThrowsRoleNotFoundException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> roleService.updateRole(1L, roleDTO));

        assertEquals("Role with id 1 not found", exception.getMessage());
    }

    @Test
    void updateRoleRoleAlreadyExistsThrowsResourceAlreadyExistException() {
        Role existingRole = new Role();
        existingRole.setId(1L);
        existingRole.setName("SUPER_ADMIN");
        existingRole.setDescription("Administrator role");

        Long id = 1L;
        RoleDTO request = new RoleDTO(null, "ADMIN","ROLE_ADMIN");

        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(existingRole));
        when(roleRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class, () -> roleService.updateRole(id, request));
    }

    @Test
    void updateRoleSuccess() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.existsByName(roleDTO.name())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDTO result = roleService.updateRole(1L, roleDTO);

        assertNotNull(result);
        assertEquals("ADMIN", result.name());
    }

    @Test
    void getRoleByIdRoleNotFoundThrowsRoleNotFoundException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> roleService.getRoleById(1L));

        assertEquals("Role with id 1 not found", exception.getMessage());
    }

    @Test
    void getRoleByIdSuccess() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        RoleDTO result = roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals("ADMIN", result.name());
    }

    @Test
    void deleteRoleByIdRoleNotFoundThrowsRoleNotFoundException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> roleService.deleteRoleById(1L));

        assertEquals("Role with id 1 not found", exception.getMessage());
    }

    @Test
    void deleteRoleByIdSuccess() {
        role.setName("OTHER");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        roleService.deleteRoleById(1L);

        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void deleteAllRolesByIdsSuccess() {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(roleRepository.findAllById(ids)).thenReturn(List.of(role));

        roleService.deleteAllRolesByIds(ids);

        verify(roleRepository, times(1)).deleteAll(anyList());
    }
}