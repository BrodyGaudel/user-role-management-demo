package org.mounanga.userservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.userservice.dto.PageResponse;
import org.mounanga.userservice.dto.RoleRequest;
import org.mounanga.userservice.dto.RoleResponse;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.exception.ItemAlreadyExistException;
import org.mounanga.userservice.exception.RoleNotFoundException;
import org.mounanga.userservice.repository.RoleRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
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
    private RoleRequest roleRequest;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("USER");
        role.setDescription("Standard user role");

        roleRequest = new RoleRequest("ADMIN", "Administrator role");
    }

    @Test
    void findAllRoles_Success() {
        Page<Role> roles = new PageImpl<>(Collections.singletonList(role));
        when(roleRepository.findAll(any(PageRequest.class))).thenReturn(roles);

        PageResponse<RoleResponse> response = roleService.findAllRoles(0, 10);

        assertNotNull(response);
        verify(roleRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void searchRoles_Success() {
        Page<Role> roles = new PageImpl<>(Collections.singletonList(role));
        when(roleRepository.search(anyString(), any(PageRequest.class))).thenReturn(roles);

        PageResponse<RoleResponse> response = roleService.searchRoles("user", 0, 10);

        assertNotNull(response);
        verify(roleRepository, times(1)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void findRoleById_Success() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));

        RoleResponse response = roleService.findRoleById(1L);

        assertNotNull(response);
        assertEquals(role.getName(), response.name());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void findRoleById_RoleNotFound() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> roleService.findRoleById(1L));
    }

    @Test
    void createRole_Success() {
        when(roleRepository.existsByName(anyString())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleResponse response = roleService.createRole(roleRequest);

        assertNotNull(response);
        assertEquals(role.getName(), response.name());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void createRole_RoleAlreadyExists() {
        when(roleRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(ItemAlreadyExistException.class, () -> roleService.createRole(roleRequest));
    }

    @Test
    void updateRole_Success() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));
        when(roleRepository.existsByName(anyString())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleResponse response = roleService.updateRole(1L, roleRequest);

        assertNotNull(response);
        assertEquals(roleRequest.name(), response.name());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void updateRole_RoleNotFound() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> roleService.updateRole(1L, roleRequest));
    }

    @Test
    void updateRole_RoleAlreadyExists() {
        Long id = 1L;
        RoleRequest request = new RoleRequest("ADMIN", "Administrator role");
        Role existingRole = Role.builder().name("MODERATOR").description("old").id(id).build();
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(existingRole));
        when(roleRepository.existsByName(anyString())).thenReturn(true);
        assertThrows(ItemAlreadyExistException.class, () -> roleService.updateRole(id, request));
    }

    @Test
    void deleteRole_Success() {
        doNothing().when(roleRepository).deleteById(anyLong());

        roleService.deleteRole(1L);

        verify(roleRepository, times(1)).deleteById(1L);
    }

}