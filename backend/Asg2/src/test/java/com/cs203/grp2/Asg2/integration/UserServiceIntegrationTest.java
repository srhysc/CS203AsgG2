package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.DTO.LandedCostResponse;
import com.cs203.grp2.Asg2.models.User;
import com.cs203.grp2.Asg2.models.UserSavedRoute;
import com.cs203.grp2.Asg2.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserService that interact with Firebase.
 * 
 * These tests verify:
 * - User creation and retrieval
 * - Bookmark management (add, retrieve, delete)
 * - User data persistence
 * 
 * Note: These tests require Firebase connectivity and will create/delete
 * test data in the Firebase database.
 */
class UserServiceIntegrationTest extends BaseFirebaseIntegrationTest {

    @Autowired
    private UserService userService;

    private String testUserId;

    @Override
    protected void setupTestData() throws Exception {
        testUserId = generateTestId();
    }

    @Override
    protected void cleanupTestData() throws Exception {
        // Clean up test user and all associated data
        if (testUserId != null) {
            try {
                deleteFromFirebase("users/" + testUserId);
            } catch (Exception e) {
                // Ignore cleanup errors
                System.err.println("Warning: Could not clean up test user: " + e.getMessage());
            }
        }
    }

    @Test
    void testCreateAndRetrieveUser() throws Exception {
        // Arrange
        String email = "test@example.com";
        String username = "testuser";

        // Act - Create user
        User createdUser = userService.getOrCreateUser(testUserId, email, username);

        // Assert - User created with correct data
        assertNotNull(createdUser);
        assertEquals(testUserId, createdUser.getId());
        assertEquals(email, createdUser.getEmail());
        assertEquals(username, createdUser.getUsername());
        assertEquals(User.Role.USER, createdUser.getRole());

        // Act - Retrieve the same user
        User retrievedUser = userService.getUserById(testUserId).get();

        // Assert - Retrieved user matches created user
        assertNotNull(retrievedUser);
        assertEquals(testUserId, retrievedUser.getId());
        assertEquals(email, retrievedUser.getEmail());
        assertEquals(username, retrievedUser.getUsername());
    }

    @Test
    void testGetOrCreateUser_ExistingUser_ShouldReturnExistingUser() throws Exception {
        // Arrange - Create initial user
        String email = "existing@example.com";
        String username = "existinguser";
        User initialUser = userService.getOrCreateUser(testUserId, email, username);
        assertNotNull(initialUser);

        // Act - Call getOrCreateUser again with different data
        User retrievedUser = userService.getOrCreateUser(testUserId, "different@example.com", "differentuser");

        // Assert - Should return existing user with original data
        assertNotNull(retrievedUser);
        assertEquals(testUserId, retrievedUser.getId());
        assertEquals(email, retrievedUser.getEmail()); // Original email
        assertEquals(username, retrievedUser.getUsername()); // Original username
    }

    @Test
    void testAddBookmark() throws Exception {
        // Arrange - Create user first
        userService.getOrCreateUser(testUserId, "test@example.com", "testuser");

        // Create a landed cost response
        LandedCostResponse response = new LandedCostResponse();
        response.setImportingCountry("Singapore");
        response.setExportingCountry("Malaysia");
        response.setPetroleumName("Crude Oil");
        response.setHsCode("2709");
        response.setPricePerUnit(100.0);
        response.setBasePrice(1000.0);
        response.setTariffRate(0.15);
        response.setTariffFees(150.0);
        response.setVatRate(0.08);
        response.setVatFees(50.0);
        response.setTotalLandedCost(1200.0);

        // Act - Add bookmark
        userService.addBookmark(response, testUserId, "Test Route");

        // Assert - Retrieve bookmarks to verify
        List<UserSavedRoute> bookmarks = userService.getBookmarks(testUserId);
        assertNotNull(bookmarks);
        assertEquals(1, bookmarks.size());
        
        UserSavedRoute savedRoute = bookmarks.get(0);
        assertEquals("Test Route", savedRoute.getName());
        assertNotNull(savedRoute.getSavedResponse());
        assertEquals("Singapore", savedRoute.getSavedResponse().getImportingCountry());
        assertEquals("Malaysia", savedRoute.getSavedResponse().getExportingCountry());
    }

    @Test
    void testGetBookmarks() throws Exception {
        // Arrange - Create user and add multiple bookmarks
        userService.getOrCreateUser(testUserId, "test@example.com", "testuser");

        // Add first bookmark
        LandedCostResponse response1 = createLandedCostResponse("Singapore", "Malaysia");
        userService.addBookmark(response1, testUserId, "Route 1");

        // Add second bookmark
        LandedCostResponse response2 = createLandedCostResponse("USA", "China");
        userService.addBookmark(response2, testUserId, "Route 2");

        // Act - Retrieve all bookmarks
        List<UserSavedRoute> bookmarks = userService.getBookmarks(testUserId);

        // Assert - All bookmarks retrieved
        assertNotNull(bookmarks);
        assertEquals(2, bookmarks.size());
        
        // Verify bookmark names
        assertTrue(bookmarks.stream().anyMatch(b -> "Route 1".equals(b.getName())));
        assertTrue(bookmarks.stream().anyMatch(b -> "Route 2".equals(b.getName())));
    }

    @Test
    void testGetBookmarks_NoBookmarks_ShouldReturnEmptyList() throws Exception {
        // Arrange - Create user without bookmarks
        userService.getOrCreateUser(testUserId, "test@example.com", "testuser");

        // Act - Retrieve bookmarks
        List<UserSavedRoute> bookmarks = userService.getBookmarks(testUserId);

        // Assert - Empty list returned
        assertNotNull(bookmarks);
        assertTrue(bookmarks.isEmpty());
    }

    @Test
    void testMultipleBookmarksFromSameUser() throws Exception {
        // Arrange - Create user and add multiple bookmarks
        userService.getOrCreateUser(testUserId, "test@example.com", "testuser");
        
        LandedCostResponse response1 = createLandedCostResponse("Singapore", "Japan");
        userService.addBookmark(response1, testUserId, "Route to Japan");

        LandedCostResponse response2 = createLandedCostResponse("Singapore", "Korea");
        userService.addBookmark(response2, testUserId, "Route to Korea");

        // Act - Retrieve all bookmarks
        List<UserSavedRoute> bookmarks = userService.getBookmarks(testUserId);

        // Assert - Both bookmarks exist
        assertNotNull(bookmarks);
        assertEquals(2, bookmarks.size());
        assertTrue(bookmarks.stream().anyMatch(b -> "Route to Japan".equals(b.getName())));
        assertTrue(bookmarks.stream().anyMatch(b -> "Route to Korea".equals(b.getName())));
    }

    @Test
    void testGetUserById_NonExistentUser_ShouldReturnNull() throws Exception {
        // Arrange
        String nonExistentUserId = generateTestId();

        // Act
        User user = userService.getUserById(nonExistentUserId).get();

        // Assert
        assertNull(user);
    }

    // Helper method to create landed cost response
    private LandedCostResponse createLandedCostResponse(String importer, String exporter) {
        LandedCostResponse response = new LandedCostResponse();
        response.setImportingCountry(importer);
        response.setExportingCountry(exporter);
        response.setPetroleumName("Crude Oil");
        response.setHsCode("2709");
        response.setPricePerUnit(100.0);
        response.setBasePrice(1000.0);
        response.setTariffRate(0.15);
        response.setTariffFees(150.0);
        response.setVatRate(0.08);
        response.setVatFees(50.0);
        response.setTotalLandedCost(1200.0);
        return response;
    }
}
