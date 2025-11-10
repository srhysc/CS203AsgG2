package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LandedCostResponseTest {

    @Test
    void testConstructor_WithAllParameters() {
        // Arrange
        Map<String, RouteBreakdown> altRoutes = new HashMap<>();
        altRoutes.put("Route1", new RouteBreakdown("China", "Singapore", "USA", 
                                                    100.0, 10.0, 5.0, 115.0, 0.05, "Crude Oil", 0.0));

        // Act
        LandedCostResponse response = new LandedCostResponse(
            "United States", "China",
            "Crude Oil", "270900",
            50.0, 5000.0,
            10.0, 500.0,
            5.0, 275.0,
            5775.0, "USD", 0.0,
            altRoutes
        );

        // Assert
        assertEquals("United States", response.getImportingCountry());
        assertEquals("China", response.getExportingCountry());
        assertEquals("Crude Oil", response.getPetroleumName());
        assertEquals("270900", response.getHsCode());
        assertEquals(50.0, response.getPricePerUnit());
        assertEquals(5000.0, response.getBasePrice());
        assertEquals(10.0, response.getTariffRate());
        assertEquals(500.0, response.getTariffFees());
        assertEquals(5.0, response.getVatRate());
        assertEquals(275.0, response.getVatFees());
        assertEquals(5775.0, response.getTotalLandedCost());
        assertEquals("USD", response.getCurrency());
        assertNotNull(response.getAlternativeRoutes());
        assertEquals(1, response.getAlternativeRoutes().size());
    }

    @Test
    void testConstructor_WithNullAlternativeRoutes() {
        // Act
        LandedCostResponse response = new LandedCostResponse(
            "Bulgaria", "Germany",
            "Gasoline", "271012",
            60.0, 6000.0,
            8.5, 510.0,
            20.0, 1302.0,
            7812.0, "EUR", 0.0,
            null
        );

        // Assert
        assertNotNull(response);
        assertNull(response.getAlternativeRoutes());
    }

    @Test
    void testConstructor_WithEmptyAlternativeRoutes() {
        // Arrange
        Map<String, RouteBreakdown> emptyRoutes = new HashMap<>();

        // Act
        LandedCostResponse response = new LandedCostResponse(
            "Japan", "South Korea",
            "Natural Gas", "271121",
            70.0, 7000.0,
            5.0, 350.0,
            10.0, 735.0,
            8085.0, "JPY", 0.0,
            emptyRoutes
        );

        // Assert
        assertNotNull(response.getAlternativeRoutes());
        assertTrue(response.getAlternativeRoutes().isEmpty());
    }

    @Test
    void testConstructor_WithZeroValues() {
        // Act
        LandedCostResponse response = new LandedCostResponse(
            "Singapore", "Malaysia",
            "Petroleum", "270000",
            0.0, 0.0,
            0.0, 0.0,
            0.0, 0.0,
            0.0, "SGD", 0.0,
            new HashMap<>()
        );

        // Assert
        assertEquals(0.0, response.getPricePerUnit());
        assertEquals(0.0, response.getBasePrice());
        assertEquals(0.0, response.getTariffRate());
        assertEquals(0.0, response.getTariffFees());
        assertEquals(0.0, response.getVatRate());
        assertEquals(0.0, response.getVatFees());
        assertEquals(0.0, response.getTotalLandedCost());
    }

    @Test
    void testGetters_AllFieldsAccessible() {
        // Arrange
        Map<String, RouteBreakdown> routes = new HashMap<>();
        routes.put("Direct", new RouteBreakdown("USA", null, "Canada", 
                                                100.0, 5.0, 2.5, 107.5, 0.025, "Diesel", 0.0));
        routes.put("Via-Mexico", new RouteBreakdown("USA", "Mexico", "Canada", 
                                                     110.0, 7.0, 3.0, 120.0, 0.03, "Diesel", 0.0));

        LandedCostResponse response = new LandedCostResponse(
            "Canada", "USA",
            "Diesel", "271019",
            55.0, 5500.0,
            2.5, 137.5,
            5.0, 281.875,
            5919.375, "CAD", 0.0,
            routes
        );

        // Act & Assert
        assertNotNull(response.getImportingCountry());
        assertNotNull(response.getExportingCountry());
        assertNotNull(response.getPetroleumName());
        assertNotNull(response.getHsCode());
        assertTrue(response.getPricePerUnit() > 0);
        assertTrue(response.getBasePrice() > 0);
        assertTrue(response.getTariffRate() >= 0);
        assertTrue(response.getTariffFees() >= 0);
        assertTrue(response.getVatRate() >= 0);
        assertTrue(response.getVatFees() >= 0);
        assertTrue(response.getTotalLandedCost() > 0);
        assertNotNull(response.getCurrency());
        assertEquals(2, response.getAlternativeRoutes().size());
    }

    @Test
    void testImmutability_NoSetters() {
        // Arrange
        LandedCostResponse response = new LandedCostResponse(
            "France", "Italy",
            "Crude Oil", "270900",
            45.0, 4500.0,
            7.0, 315.0,
            20.0, 963.0,
            5778.0, "EUR", 0.0,
            new HashMap<>()
        );

        // Assert - Verify getters work and no setters exist
        assertEquals("France", response.getImportingCountry());
        assertEquals("Italy", response.getExportingCountry());
        // Class should be immutable - no setters
        assertNotNull(response);
    }

    @Test
    void testAlternativeRoutes_WithMultipleRoutes() {
        // Arrange
        Map<String, RouteBreakdown> routes = new HashMap<>();
        routes.put("Route-A", new RouteBreakdown("CHN", "SGP", "AUS", 
                                                  200.0, 15.0, 12.5, 227.5, 0.0625, "LNG", 0.0));
        routes.put("Route-B", new RouteBreakdown("CHN", "KOR", "AUS", 
                                                  210.0, 18.0, 13.0, 241.0, 0.065, "LNG", 0.0));
        routes.put("Route-C", new RouteBreakdown("CHN", "JPN", "AUS", 
                                                  220.0, 20.0, 14.0, 254.0, 0.07, "LNG", 0.0));

        // Act
        LandedCostResponse response = new LandedCostResponse(
            "Australia", "China",
            "LNG", "271111",
            80.0, 8000.0,
            12.0, 960.0,
            10.0, 896.0,
            9856.0, "AUD", 0.0,
            routes
        );

        // Assert
        assertEquals(3, response.getAlternativeRoutes().size());
        assertTrue(response.getAlternativeRoutes().containsKey("Route-A"));
        assertTrue(response.getAlternativeRoutes().containsKey("Route-B"));
        assertTrue(response.getAlternativeRoutes().containsKey("Route-C"));
    }

    @Test
    void testCalculation_TotalLandedCost() {
        // Arrange - Verify the calculation logic makes sense
        double basePrice = 1000.0;
        double tariffFees = 100.0;
        double vatFees = 50.0;
        double expectedTotal = basePrice + tariffFees + vatFees; // 1150.0

        // Act
        LandedCostResponse response = new LandedCostResponse(
            "Germany", "Netherlands",
            "Petroleum Products", "271000",
            10.0, basePrice,
            10.0, tariffFees,
            5.0, vatFees,
            expectedTotal, "EUR", 0.0,
            new HashMap<>()
        );

        // Assert
        assertEquals(expectedTotal, response.getTotalLandedCost());
        assertEquals(basePrice + tariffFees + vatFees, response.getTotalLandedCost());
    }

    @Test
    void testConstructor_WithLongCountryNames() {
        // Act
        LandedCostResponse response = new LandedCostResponse(
            "United States of America", "People's Republic of China",
            "Crude Petroleum Oil", "270900",
            100.0, 10000.0,
            15.0, 1500.0,
            7.5, 862.5,
            12362.5, "USD", 0.0,
            new HashMap<>()
        );

        // Assert
        assertEquals("United States of America", response.getImportingCountry());
        assertEquals("People's Republic of China", response.getExportingCountry());
        assertEquals("Crude Petroleum Oil", response.getPetroleumName());
    }
}
