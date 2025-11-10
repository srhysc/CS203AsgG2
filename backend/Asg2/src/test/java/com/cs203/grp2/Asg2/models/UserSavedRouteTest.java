package com.cs203.grp2.Asg2.models;

import com.cs203.grp2.Asg2.DTO.LandedCostResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSavedRouteTest {

    @Test
    void testDefaultConstructor() {
        // Act
        UserSavedRoute route = new UserSavedRoute();

        // Assert
        assertNotNull(route);
        assertNull(route.getSavedResponse());
        assertNull(route.getName());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        LandedCostResponse response = new LandedCostResponse(
            "USA", "China", "Crude Oil", "270900",
            50.0, 5000.0, 10.0, 500.0, 5.0, 250.0, 5750.0, "USD", null
        );

        // Act
        UserSavedRoute route = new UserSavedRoute(response, "My Route");

        // Assert
        assertNotNull(route.getSavedResponse());
        assertEquals("USA", route.getSavedResponse().getImportingCountry());
        assertEquals("My Route", route.getName());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        UserSavedRoute route = new UserSavedRoute();
        LandedCostResponse response = new LandedCostResponse(
            "Singapore", "Malaysia", "Gasoline", "271012",
            60.0, 6000.0, 8.5, 510.0, 7.0, 455.7, 6965.7, "SGD", null
        );

        // Act
        route.setSavedResponse(response);
        route.setName("Singapore-Malaysia Route");

        // Assert
        assertNotNull(route.getSavedResponse());
        assertEquals("Singapore", route.getSavedResponse().getImportingCountry());
        assertEquals("Malaysia", route.getSavedResponse().getExportingCountry());
        assertEquals("Singapore-Malaysia Route", route.getName());
    }

    @Test
    void testSetName_WithNull() {
        // Arrange
        UserSavedRoute route = new UserSavedRoute();

        // Act
        route.setName(null);

        // Assert
        assertNull(route.getName());
    }

    @Test
    void testSetSavedResponse_WithNull() {
        // Arrange
        UserSavedRoute route = new UserSavedRoute();

        // Act
        route.setSavedResponse(null);

        // Assert
        assertNull(route.getSavedResponse());
    }

    @Test
    void testSetName_WithEmptyString() {
        // Arrange
        UserSavedRoute route = new UserSavedRoute();

        // Act
        route.setName("");

        // Assert
        assertEquals("", route.getName());
    }

    @Test
    void testConstructor_WithNullResponse() {
        // Act
        UserSavedRoute route = new UserSavedRoute(null, "Test Route");

        // Assert
        assertNull(route.getSavedResponse());
        assertEquals("Test Route", route.getName());
    }

    @Test
    void testConstructor_WithNullName() {
        // Arrange
        LandedCostResponse response = new LandedCostResponse(
            "USA", "China", "Crude Oil", "270900",
            50.0, 5000.0, 10.0, 500.0, 5.0, 250.0, 5750.0, "USD", null
        );

        // Act
        UserSavedRoute route = new UserSavedRoute(response, null);

        // Assert
        assertNotNull(route.getSavedResponse());
        assertNull(route.getName());
    }

    @Test
    void testSetName_WithSpecialCharacters() {
        // Arrange
        UserSavedRoute route = new UserSavedRoute();

        // Act
        route.setName("Route #1 (USA->China) @2024");

        // Assert
        assertEquals("Route #1 (USA->China) @2024", route.getName());
    }
}
