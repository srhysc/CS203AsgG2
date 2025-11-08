package com.cs203.grp2.Asg2.user;

import com.cs203.grp2.Asg2.models.User;
import com.cs203.grp2.Asg2.models.User.Role;
import com.cs203.grp2.Asg2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user123");
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setRole(Role.USER);

        // Set up security context with authenticated user
        var auth = new UsernamePasswordAuthenticationToken(testUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testGetProfile_ShouldReturnUserProfile() throws Exception {
        // Arrange
        when(userService.getUserById("user123")).thenReturn(testUser);

        // Act
        User result = userController.getProfile();

        // Assert
        assertNotNull(result);
        assertEquals("user123", result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
        verify(userService).getUserById("user123");
    }

    @Test
    void testGetUserRoles_ShouldReturnUserRole() throws Exception {
        // Arrange
        when(userService.getUserRoles("user123")).thenReturn(Role.USER);

        // Act
        String result = userController.getUserRoles();

        // Assert
        assertNotNull(result);
        assertEquals("USER", result);
        verify(userService).getUserRoles("user123");
    }

    @Test
    void testGetUserRoles_ForAdmin_ShouldReturnAdminRole() throws Exception {
        // Arrange
        testUser.setRole(Role.ADMIN);
        when(userService.getUserRoles("user123")).thenReturn(Role.ADMIN);

        // Act
        String result = userController.getUserRoles();

        // Assert
        assertEquals("ADMIN", result);
        verify(userService).getUserRoles("user123");
    }

    @Test
    void testUpdateRole_ShouldUpdateUserRole() throws Exception {
        // Arrange
        doNothing().when(userService).updateUserRole("user123", Role.ADMIN);

        // Act
        userController.updateRole(Role.ADMIN);

        // Assert
        verify(userService).updateUserRole("user123", Role.ADMIN);
    }

    @Test
    void testGetAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Arrange
        User user1 = new User();
        user1.setId("user1");
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId("user2");
        user2.setUsername("user2");

        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // Act
        List<User> result = userController.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userService).getAllUsers();
    }

    @Test
    void testUpdateRole_ToUser_ShouldWork() throws Exception {
        // Arrange
        doNothing().when(userService).updateUserRole("user123", Role.USER);

        // Act
        userController.updateRole(Role.USER);

        // Assert
        verify(userService).updateUserRole("user123", Role.USER);
    }

    @Test
    void testGetProfile_WithDifferentUser_ShouldReturnCorrectProfile() throws Exception {
        // Arrange
        User adminUser = new User();
        adminUser.setId("admin123");
        adminUser.setEmail("admin@example.com");
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);

        var auth = new UsernamePasswordAuthenticationToken(adminUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.getUserById("admin123")).thenReturn(adminUser);

        // Act
        User result = userController.getProfile();

        // Assert
        assertNotNull(result);
        assertEquals("admin123", result.getId());
        assertEquals("admin@example.com", result.getEmail());
        verify(userService).getUserById("admin123");
    }
}
