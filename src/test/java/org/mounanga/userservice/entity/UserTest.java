package org.mounanga.userservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserTest {

    @InjectMocks
    private User user;

    @BeforeEach
    void setUp() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("SUPER_ADMIN").build());
        user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEnabled(true);
        user.setRoles(roles);
        user.setPasswordNeedsToBeChanged(false);
    }

    @Test
    void getFullName() {
        String fullName = user.getFullName();
        assertNotNull(fullName);
        assertEquals("John Doe", fullName);
    }

    @Test
    void addRole() {
        Role role = Role.builder()
                .name(UUID.randomUUID().toString())
                .build();
        user.addRole(role);
        List<Role> roles = user.getRoles();
        assertNotNull(roles);
        assertTrue(roles.contains(role));
    }

    @Test
    void removeRole() {
        Role role = Role.builder().name("SUPER_ADMIN").build();
        user.removeRole(role);
        List<Role> roles = user.getRoles();
        assertNotNull(roles);
        assertFalse(roles.contains(role));
    }

    @Test
    void isEnabled() {
        assertTrue(user.isEnabled());
    }

    @Test
    void markPasswordAsChanged() {
        assertFalse(user.isPasswordNeedsToBeChanged());
    }

    @Test
    void isPasswordNeedsToBeChanged() {
        assertFalse(user.isPasswordNeedsToBeChanged());
    }
}