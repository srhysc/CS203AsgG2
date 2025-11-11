package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteOptionTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        List<String> transit = Arrays.asList("Singapore", "Malaysia");

        // Act
        RouteOption route = new RouteOption("China", transit, "USA", 1500.0);

        // Assert
        assertEquals("China", route.getExporter());
        assertEquals(transit, route.getTransitCountries());
        assertEquals("USA", route.getImporter());
        assertEquals(1500.0, route.getTotalCost());
    }

    @Test
    void testWithNoTransitCountries() {
        // Arrange
        List<String> transit = Arrays.asList();

        // Act
        RouteOption route = new RouteOption("USA", transit, "Canada", 500.0);

        // Assert
        assertEquals("USA", route.getExporter());
        assertTrue(route.getTransitCountries().isEmpty());
        assertEquals("Canada", route.getImporter());
        assertEquals(500.0, route.getTotalCost());
    }

    @Test
    void testWithSingleTransitCountry() {
        // Arrange
        List<String> transit = Arrays.asList("Mexico");

        // Act
        RouteOption route = new RouteOption("USA", transit, "Panama", 800.0);

        // Assert
        assertEquals(1, route.getTransitCountries().size());
        assertEquals("Mexico", route.getTransitCountries().get(0));
    }

    @Test
    void testWithMultipleTransitCountries() {
        // Arrange
        List<String> transit = Arrays.asList("France", "Germany", "Poland", "Ukraine");

        // Act
        RouteOption route = new RouteOption("UK", transit, "Russia", 2500.0);

        // Assert
        assertEquals(4, route.getTransitCountries().size());
        assertEquals("France", route.getTransitCountries().get(0));
        assertEquals("Ukraine", route.getTransitCountries().get(3));
    }

    @Test
    void testZeroCost() {
        // Arrange
        List<String> transit = Arrays.asList();

        // Act
        RouteOption route = new RouteOption("Singapore", transit, "Malaysia", 0.0);

        // Assert
        assertEquals(0.0, route.getTotalCost());
    }

    @Test
    void testHighCost() {
        // Arrange
        List<String> transit = Arrays.asList("Multiple", "Transit", "Countries");

        // Act
        RouteOption route = new RouteOption("Australia", transit, "USA", 99999.99);

        // Assert
        assertEquals(99999.99, route.getTotalCost());
    }

    @Test
    void testNullValues() {
        // Act
        RouteOption route = new RouteOption(null, null, null, 1000.0);

        // Assert
        assertNull(route.getExporter());
        assertNull(route.getTransitCountries());
        assertNull(route.getImporter());
        assertEquals(1000.0, route.getTotalCost());
    }

    @Test
    void testEmptyStrings() {
        // Arrange
        List<String> transit = Arrays.asList();

        // Act
        RouteOption route = new RouteOption("", transit, "", 500.0);

        // Assert
        assertEquals("", route.getExporter());
        assertEquals("", route.getImporter());
    }

    @Test
    void testSameExporterAndImporter() {
        // Arrange
        List<String> transit = Arrays.asList();

        // Act
        RouteOption route = new RouteOption("USA", transit, "USA", 0.0);

        // Assert
        assertEquals("USA", route.getExporter());
        assertEquals("USA", route.getImporter());
    }

    @Test
    void testNegativeCost() {
        // Arrange
        List<String> transit = Arrays.asList();

        // Act
        RouteOption route = new RouteOption("USA", transit, "Canada", -100.0);

        // Assert
        assertEquals(-100.0, route.getTotalCost());
    }

    @Test
    void testDecimalPrecision() {
        // Arrange
        List<String> transit = Arrays.asList("Singapore");

        // Act
        RouteOption route = new RouteOption("China", transit, "Malaysia", 1234.567890);

        // Assert
        assertEquals(1234.567890, route.getTotalCost(), 0.000001);
    }

    @Test
    void testTransitCountriesListModification() {
        // Arrange
        List<String> transit = Arrays.asList("Singapore", "Malaysia");
        RouteOption route = new RouteOption("China", transit, "USA", 1500.0);

        // Assert - getTransitCountries returns the same reference
        assertSame(transit, route.getTransitCountries());
    }

    @Test
    void testMultipleInstancesIndependence() {
        // Arrange
        List<String> transit1 = Arrays.asList("Singapore");
        List<String> transit2 = Arrays.asList("France", "Germany");

        // Act
        RouteOption route1 = new RouteOption("China", transit1, "USA", 1500.0);
        RouteOption route2 = new RouteOption("UK", transit2, "Russia", 2000.0);

        // Assert
        assertNotEquals(route1.getExporter(), route2.getExporter());
        assertNotEquals(route1.getImporter(), route2.getImporter());
        assertNotEquals(route1.getTotalCost(), route2.getTotalCost());
        assertNotEquals(route1.getTransitCountries().size(), route2.getTransitCountries().size());
    }
}
