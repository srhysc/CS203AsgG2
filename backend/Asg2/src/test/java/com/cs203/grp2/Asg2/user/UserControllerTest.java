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
import java.util.concurrent.CompletableFuture;

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
        when(userService.getUserById("user123")).thenReturn(CompletableFuture.completedFuture(testUser));

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

        when(userService.getUserById("admin123")).thenReturn(CompletableFuture.completedFuture(adminUser));

        // Act
        User result = userController.getProfile();

        // Assert
        assertNotNull(result);
        assertEquals("admin123", result.getId());
        assertEquals("admin@example.com", result.getEmail());
        verify(userService).getUserById("admin123");
    }

    @Test
    void testGetBookmarks_ShouldReturnUserBookmarks() throws Exception {
        // Arrange
        com.cs203.grp2.Asg2.models.UserSavedRoute route1 = new com.cs203.grp2.Asg2.models.UserSavedRoute();
        route1.setName("My Route 1");
        
        com.cs203.grp2.Asg2.models.UserSavedRoute route2 = new com.cs203.grp2.Asg2.models.UserSavedRoute();
        route2.setName("My Route 2");
        
        List<com.cs203.grp2.Asg2.models.UserSavedRoute> bookmarks = Arrays.asList(route1, route2);
        when(userService.getBookmarks("user123")).thenReturn(bookmarks);

        // Act
        List<com.cs203.grp2.Asg2.models.UserSavedRoute> result = userController.getBookmarks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("My Route 1", result.get(0).getName());
        verify(userService).getBookmarks("user123");
    }

    @Test
    void testAddBookmark_WithValidRequest_ShouldReturnSuccess() throws Exception {
        // Arrange
        com.cs203.grp2.Asg2.DTO.LandedCostResponse response = new com.cs203.grp2.Asg2.DTO.LandedCostResponse(
            "USA", "China", "Crude Oil", "270900",
            50.0, 5000.0, 10.0, 500.0, 5.0, 250.0, 5750.0, "USD", null
        );
        
        com.cs203.grp2.Asg2.DTO.BookmarkRequest request = new com.cs203.grp2.Asg2.DTO.BookmarkRequest();
        request.setSavedResponse(response);
        request.setBookmarkName("Test Bookmark");
        
        doNothing().when(userService).addBookmark(any(), eq("user123"), eq("Test Bookmark"));

        // Act
        org.springframework.http.ResponseEntity<?> result = userController.addBookmark(request);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals("Bookmark added successfully", result.getBody());
        verify(userService).addBookmark(any(com.cs203.grp2.Asg2.DTO.LandedCostResponse.class), 
                                       eq("user123"), eq("Test Bookmark"));
    }

    @Test
    void testAddBookmark_WithNullSavedResponse_ShouldReturnBadRequest() throws Exception {
        // Arrange
        com.cs203.grp2.Asg2.DTO.BookmarkRequest request = new com.cs203.grp2.Asg2.DTO.BookmarkRequest();
        request.setSavedResponse(null);
        request.setBookmarkName("Test Bookmark");

        // Act
        org.springframework.http.ResponseEntity<?> result = userController.addBookmark(request);

        // Assert
        assertNotNull(result);
        assertEquals(400, result.getStatusCode().value());
        assertEquals("Missing savedResponse in request", result.getBody());
        verify(userService, never()).addBookmark(any(), any(), any());
    }

    @Test
    void testAddBookmark_WithNullImportingCountry_ShouldReturnBadRequest() throws Exception {
        // Arrange
        com.cs203.grp2.Asg2.DTO.LandedCostResponse response = new com.cs203.grp2.Asg2.DTO.LandedCostResponse(
            null, "China", "Crude Oil", "270900",
            50.0, 5000.0, 10.0, 500.0, 5.0, 250.0, 5750.0, "USD", null
        );
        
        com.cs203.grp2.Asg2.DTO.BookmarkRequest request = new com.cs203.grp2.Asg2.DTO.BookmarkRequest();
        request.setSavedResponse(response);
        request.setBookmarkName("Test Bookmark");

        // Act
        org.springframework.http.ResponseEntity<?> result = userController.addBookmark(request);

        // Assert
        assertNotNull(result);
        assertEquals(400, result.getStatusCode().value());
        assertEquals("Missing importingCountry in savedResponse", result.getBody());
        verify(userService, never()).addBookmark(any(), any(), any());
    }
}
