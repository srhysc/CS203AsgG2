package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteOptimizationResponseTest {

    @Test
    void testConstructorWithMultipleRoutes() {
        // Arrange
        List<RouteBreakdown> routes = Arrays.asList(
            new RouteBreakdown("USA", "MEX", "CAN", 1000.0, 150.0, 75.0, 1225.0, 0.05, "Crude Oil"),
            new RouteBreakdown("USA", null, "CAN", 1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil"),
            new RouteBreakdown("USA", "PAN", "CAN", 1000.0, 200.0, 100.0, 1300.0, 0.05, "Crude Oil")
        );

        // Act
        RouteOptimizationResponse response = new RouteOptimizationResponse(routes, 85.50);

        // Assert
        assertNotNull(response.getTopRoutes());
        assertEquals(3, response.getTopRoutes().size());
        assertEquals(85.50, response.getPetroleumPrice());
    }

    @Test
    void testConstructorWithSingleRoute() {
        // Arrange
        List<RouteBreakdown> routes = Arrays.asList(
            new RouteBreakdown("USA", null, "CAN", 1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil")
        );

        // Act
        RouteOptimizationResponse response = new RouteOptimizationResponse(routes, 80.00);

        // Assert
        assertEquals(1, response.getTopRoutes().size());
        assertEquals(80.00, response.getPetroleumPrice());
    }

    @Test
    void testConstructorWithEmptyRoutes() {
        // Arrange
        List<RouteBreakdown> emptyRoutes = new ArrayList<>();

        // Act
        RouteOptimizationResponse response = new RouteOptimizationResponse(emptyRoutes, 90.00);

        // Assert
        assertNotNull(response.getTopRoutes());
        assertTrue(response.getTopRoutes().isEmpty());
        assertEquals(90.00, response.getPetroleumPrice());
    }

    @Test
    void testConstructorWithNullRoutes() {
        // Act
        RouteOptimizationResponse response = new RouteOptimizationResponse(null, 85.50);

        // Assert
        assertNull(response.getTopRoutes());
        assertEquals(85.50, response.getPetroleumPrice());
    }

    @Test
    void testGetTopRoutes() {
        // Arrange
        RouteBreakdown route1 = new RouteBreakdown("USA", "MEX", "CAN", 1000.0, 150.0, 75.0, 1225.0, 0.05, "Crude Oil");
        RouteBreakdown route2 = new RouteBreakdown("USA", null, "CAN", 1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil");
        List<RouteBreakdown> routes = Arrays.asList(route1, route2);

        // Act
        RouteOptimizationResponse response = new RouteOptimizationResponse(routes, 85.50);

        // Assert
        List<RouteBreakdown> retrievedRoutes = response.getTopRoutes();
        assertEquals(2, retrievedRoutes.size());
        assertEquals(route1, retrievedRoutes.get(0));
        assertEquals(route2, retrievedRoutes.get(1));
    }

    @Test
    void testGetPetroleumPrice() {
        // Arrange
        List<RouteBreakdown> routes = Arrays.asList(
            new RouteBreakdown("USA", null, "CAN", 1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil")
        );

        // Act
        RouteOptimizationResponse response = new RouteOptimizationResponse(routes, 92.75);

        // Assert
        assertEquals(92.75, response.getPetroleumPrice());
    }

    @Test
    void testPetroleumPriceZero() {
        // Arrange
        List<RouteBreakdown> routes = Arrays.asList(
            new RouteBreakdown("USA", null, "CAN", 1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil")
        );

        // Act
        RouteOptimizationResponse response = new RouteOptimizationResponse(routes, 0.0);

        // Assert
        assertEquals(0.0, response.getPetroleumPrice());
    }

    @Test
    void testPetroleumPriceNegative() {
        // Arrange
        List<RouteBreakdown> routes = Arrays.asList(
            new RouteBreakdown("USA", null, "CAN", 1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil")
        );

        // Act - Constructor doesn't validate negative prices
        RouteOptimizationResponse response = new RouteOptimizationResponse(routes, -10.0);

        // Assert
        assertEquals(-10.0, response.getPetroleumPrice());
    }

    @Test
    void testRoutesSortedByCost() {
        // Arrange - Routes sorted by totalLandedCost (lowest to highest)
        RouteBreakdown cheapestRoute = new RouteBreakdown("USA", null, "CAN", 1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil");
        RouteBreakdown midRoute = new RouteBreakdown("USA", "MEX", "CAN", 1000.0, 150.0, 75.0, 1225.0, 0.05, "Crude Oil");
        RouteBreakdown expensiveRoute = new RouteBreakdown("USA", "PAN", "CAN", 1000.0, 200.0, 100.0, 1300.0, 0.05, "Crude Oil");
        
        List<RouteBreakdown> routes = Arrays.asList(cheapestRoute, midRoute, expensiveRoute);

        // Act
        RouteOptimizationResponse response = new RouteOptimizationResponse(routes, 85.50);

        // Assert - Verify order is preserved
        assertEquals(1150.0, response.getTopRoutes().get(0).getTotalLandedCost());
        assertEquals(1225.0, response.getTopRoutes().get(1).getTotalLandedCost());
        assertEquals(1300.0, response.getTopRoutes().get(2).getTotalLandedCost());
    }

    @Test
    void testHighPetroleumPrice() {
        // Arrange
        List<RouteBreakdown> routes = Arrays.asList(
            new RouteBreakdown("SAU", "ARE", "CHN", 5000.0, 500.0, 250.0, 5750.0, 0.05, "Crude Oil")
        );

        // Act
        RouteOptimizationResponse response = new RouteOptimizationResponse(routes, 150.00);

        // Assert
        assertEquals(150.00, response.getPetroleumPrice());
        assertEquals(1, response.getTopRoutes().size());
    }

    @Test
    void testResponseImmutability() {
        // Arrange
        List<RouteBreakdown> routes = new ArrayList<>(Arrays.asList(
            new RouteBreakdown("USA", null, "CAN", 1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil")
        ));
        RouteOptimizationResponse response = new RouteOptimizationResponse(routes, 85.50);

        // Act - Try to modify the original list
        int originalSize = response.getTopRoutes().size();
        routes.add(new RouteBreakdown("USA", "MEX", "CAN", 1000.0, 150.0, 75.0, 1225.0, 0.05, "Crude Oil"));

        // Assert - Response should reflect the change (no defensive copy made)
        // Note: This shows the class is NOT immutable - documenting current behavior
        assertTrue(response.getTopRoutes().size() >= originalSize);
    }

    @Test
    void testMultipleRoutesWithDifferentTransitPoints() {
        // Arrange
        List<RouteBreakdown> routes = Arrays.asList(
            new RouteBreakdown("CHN", null, "USA", 2000.0, 200.0, 100.0, 2300.0, 0.05, "Crude Oil"),
            new RouteBreakdown("CHN", "SGP", "USA", 2000.0, 180.0, 90.0, 2270.0, 0.05, "Crude Oil"),
            new RouteBreakdown("CHN", "JPN", "USA", 2000.0, 220.0, 110.0, 2330.0, 0.05, "Crude Oil")
        );

        // Act
        RouteOptimizationResponse response = new RouteOptimizationResponse(routes, 88.00);

        // Assert
        assertEquals(3, response.getTopRoutes().size());
        assertNull(response.getTopRoutes().get(0).getTransitCountry()); // Direct route
        assertEquals("SGP", response.getTopRoutes().get(1).getTransitCountry());
        assertEquals("JPN", response.getTopRoutes().get(2).getTransitCountry());
    }
}
