package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteBreakdownTest {

    @Test
    void testConstructorWithAllParameters() {
        // Arrange & Act
        RouteBreakdown breakdown = new RouteBreakdown(
            "USA", "MEX", "CAN",
            1000.0, 150.0, 75.0, 1225.0, 0.05, "Crude Oil"
        );

        // Assert
        assertEquals("USA", breakdown.getExportingCountry());
        assertEquals("MEX", breakdown.getTransitCountry());
        assertEquals("CAN", breakdown.getImportingCountry());
        assertEquals(1000.0, breakdown.getBaseCost());
        assertEquals(150.0, breakdown.getTariffFees());
        assertEquals(75.0, breakdown.getVatFees());
        assertEquals(1225.0, breakdown.getTotalLandedCost());
        assertEquals(0.05, breakdown.getVatRate());
    }

    @Test
    void testConstructorWithNullTransitCountry() {
        // Arrange & Act - Direct route without transit
        RouteBreakdown breakdown = new RouteBreakdown(
            "USA", null, "CAN",
            1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil"
        );

        // Assert
        assertEquals("USA", breakdown.getExportingCountry());
        assertNull(breakdown.getTransitCountry());
        assertEquals("CAN", breakdown.getImportingCountry());
        assertEquals(1000.0, breakdown.getBaseCost());
    }

    @Test
    void testConstructorWithZeroValues() {
        // Arrange & Act - Route with no fees
        RouteBreakdown breakdown = new RouteBreakdown(
            "USA", "MEX", "CAN",
            1000.0, 0.0, 0.0, 1000.0, 0.0, "Crude Oil"
        );

        // Assert
        assertEquals(0.0, breakdown.getTariffFees());
        assertEquals(0.0, breakdown.getVatFees());
        assertEquals(0.0, breakdown.getVatRate());
        assertEquals(1000.0, breakdown.getTotalLandedCost());
    }

    @Test
    void testConstructorWithHighTariffAndVat() {
        // Arrange & Act - Route with high tariffs
        RouteBreakdown breakdown = new RouteBreakdown(
            "CHN", "SGP", "AUS",
            5000.0, 2500.0, 1000.0, 8500.0, 0.10, "Crude Oil"
        );

        // Assert
        assertEquals(2500.0, breakdown.getTariffFees());
        assertEquals(1000.0, breakdown.getVatFees());
        assertEquals(8500.0, breakdown.getTotalLandedCost());
        assertEquals(0.10, breakdown.getVatRate());
    }

    @Test
    void testGettersReturnCorrectValues() {
        // Arrange
        RouteBreakdown breakdown = new RouteBreakdown(
            "DEU", "FRA", "GBR",
            2000.0, 300.0, 120.0, 2420.0, 0.06, "Crude Oil"
        );

        // Act & Assert
        assertAll("All getters should return correct values",
            () -> assertEquals("DEU", breakdown.getExportingCountry()),
            () -> assertEquals("FRA", breakdown.getTransitCountry()),
            () -> assertEquals("GBR", breakdown.getImportingCountry()),
            () -> assertEquals(2000.0, breakdown.getBaseCost()),
            () -> assertEquals(300.0, breakdown.getTariffFees()),
            () -> assertEquals(120.0, breakdown.getVatFees()),
            () -> assertEquals(2420.0, breakdown.getTotalLandedCost()),
            () -> assertEquals(0.06, breakdown.getVatRate())
        );
    }

    @Test
    void testCostCalculationLogic() {
        // Arrange - Test if costs are logically consistent
        double baseCost = 1000.0;
        double tariffFees = 150.0;
        double vatFees = 50.0;
        double expectedTotal = baseCost + tariffFees + vatFees;

        // Act
        RouteBreakdown breakdown = new RouteBreakdown(
            "USA", "MEX", "CAN",
            baseCost, tariffFees, vatFees, expectedTotal, 0.05, "Crude Oil"
        );

        // Assert
        double calculatedTotal = breakdown.getBaseCost() + 
                                breakdown.getTariffFees() + 
                                breakdown.getVatFees();
        assertEquals(expectedTotal, calculatedTotal);
        assertEquals(expectedTotal, breakdown.getTotalLandedCost());
    }

    @Test
    void testDirectRouteVsTransitRoute() {
        // Arrange
        RouteBreakdown directRoute = new RouteBreakdown(
            "USA", null, "CAN",
            1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil"
        );

        RouteBreakdown transitRoute = new RouteBreakdown(
            "USA", "MEX", "CAN",
            1000.0, 150.0, 75.0, 1225.0, 0.05, "Crude Oil"
        );

        // Assert
        assertNull(directRoute.getTransitCountry());
        assertNotNull(transitRoute.getTransitCountry());
        assertTrue(transitRoute.getTotalLandedCost() > directRoute.getTotalLandedCost());
    }

    @Test
    void testVatRateAsDecimal() {
        // Arrange & Act - VAT rate stored as decimal (0.05 = 5%)
        RouteBreakdown breakdown = new RouteBreakdown(
            "USA", "MEX", "CAN",
            1000.0, 150.0, 75.0, 1225.0, 0.05, "Crude Oil"
        );

        // Assert
        assertEquals(0.05, breakdown.getVatRate());
        // Verify VAT fees are stored correctly (constructor doesn't validate calculation)
        assertEquals(75.0, breakdown.getVatFees());
    }

    @Test
    void testEmptyStringCountryNames() {
        // Arrange & Act - Edge case with empty strings
        RouteBreakdown breakdown = new RouteBreakdown(
            "", "", "",
            1000.0, 100.0, 50.0, 1150.0, 0.05, "Crude Oil"
        );

        // Assert
        assertEquals("", breakdown.getExportingCountry());
        assertEquals("", breakdown.getTransitCountry());
        assertEquals("", breakdown.getImportingCountry());
    }

    @Test
    void testNegativeValuesNotValidated() {
        // Arrange & Act - Constructor doesn't validate negative values
        RouteBreakdown breakdown = new RouteBreakdown(
            "USA", "MEX", "CAN",
            -1000.0, -150.0, -75.0, -1225.0, -0.05, "Crude Oil"
        );

        // Assert - Values are stored as-is (no validation in constructor)
        assertEquals(-1000.0, breakdown.getBaseCost());
        assertEquals(-150.0, breakdown.getTariffFees());
        assertEquals(-75.0, breakdown.getVatFees());
        assertEquals(-1225.0, breakdown.getTotalLandedCost());
        assertEquals(-0.05, breakdown.getVatRate());
    }

    @Test
    void testSetImportingCountry() {
        // Arrange
        RouteBreakdown breakdown = new RouteBreakdown();
        
        // Act
        breakdown.setImportingCountry("Singapore");
        
        // Assert
        assertEquals("Singapore", breakdown.getImportingCountry());
    }

    @Test
    void testSetExportingCountry() {
        // Arrange
        RouteBreakdown breakdown = new RouteBreakdown();
        
        // Act
        breakdown.setExportingCountry("Malaysia");
        
        // Assert
        assertEquals("Malaysia", breakdown.getExportingCountry());
    }

    @Test
    void testSetPetroleumName() {
        // Arrange
        RouteBreakdown breakdown = new RouteBreakdown();
        
        // Act - Setter used by Firebase for object population
        breakdown.setPetroleumName("Diesel");
        
        // Assert - No getter available, but setter executes without error
        assertNotNull(breakdown);
    }

    @Test
    void testSetTariffFees() {
        // Arrange
        RouteBreakdown breakdown = new RouteBreakdown();
        
        // Act
        breakdown.setTariffFees(250.75);
        
        // Assert
        assertEquals(250.75, breakdown.getTariffFees());
    }

    @Test
    void testSetVatRate() {
        // Arrange
        RouteBreakdown breakdown = new RouteBreakdown();
        
        // Act
        breakdown.setVatRate(0.08);
        
        // Assert
        assertEquals(0.08, breakdown.getVatRate());
    }

    @Test
    void testSetTotalLandedCost() {
        // Arrange
        RouteBreakdown breakdown = new RouteBreakdown();
        
        // Act
        breakdown.setTotalLandedCost(1500.50);
        
        // Assert
        assertEquals(1500.50, breakdown.getTotalLandedCost());
    }

    @Test
    void testSettersWithNullValues() {
        // Arrange
        RouteBreakdown breakdown = new RouteBreakdown();
        
        // Act
        breakdown.setImportingCountry(null);
        breakdown.setExportingCountry(null);
        breakdown.setPetroleumName(null); // No getter, but setter should handle null
        
        // Assert
        assertNull(breakdown.getImportingCountry());
        assertNull(breakdown.getExportingCountry());
    }
}
