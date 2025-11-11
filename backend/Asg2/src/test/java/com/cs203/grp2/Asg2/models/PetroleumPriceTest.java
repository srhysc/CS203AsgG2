package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PetroleumPriceTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 1);

        // Act
        PetroleumPrice price = new PetroleumPrice(date, 75.5, "USD/barrel");

        // Assert
        assertEquals(date, price.getDate());
        assertEquals(75.5, price.getAvgPricePerUnitUsd());
        assertEquals("USD/barrel", price.getUnit());
    }

    @Test
    void testDifferentPriceValues() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 1);

        // Act
        PetroleumPrice price1 = new PetroleumPrice(date, 0.0, "USD/barrel");
        PetroleumPrice price2 = new PetroleumPrice(date, 100.0, "USD/barrel");
        PetroleumPrice price3 = new PetroleumPrice(date, 9999.99, "USD/barrel");

        // Assert
        assertEquals(0.0, price1.getAvgPricePerUnitUsd());
        assertEquals(100.0, price2.getAvgPricePerUnitUsd());
        assertEquals(9999.99, price3.getAvgPricePerUnitUsd());
    }

    @Test
    void testDifferentUnits() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 1);

        // Act
        PetroleumPrice price1 = new PetroleumPrice(date, 75.5, "USD/barrel");
        PetroleumPrice price2 = new PetroleumPrice(date, 50.0, "USD/gallon");
        PetroleumPrice price3 = new PetroleumPrice(date, 1.5, "USD/liter");

        // Assert
        assertEquals("USD/barrel", price1.getUnit());
        assertEquals("USD/gallon", price2.getUnit());
        assertEquals("USD/liter", price3.getUnit());
    }

    @Test
    void testDifferentDates() {
        // Arrange & Act
        PetroleumPrice price1 = new PetroleumPrice(LocalDate.of(2024, 1, 1), 75.0, "USD/barrel");
        PetroleumPrice price2 = new PetroleumPrice(LocalDate.of(2024, 6, 15), 80.0, "USD/barrel");
        PetroleumPrice price3 = new PetroleumPrice(LocalDate.of(2024, 12, 31), 85.0, "USD/barrel");

        // Assert
        assertEquals(LocalDate.of(2024, 1, 1), price1.getDate());
        assertEquals(LocalDate.of(2024, 6, 15), price2.getDate());
        assertEquals(LocalDate.of(2024, 12, 31), price3.getDate());
    }

    @Test
    void testNullValues() {
        // Act
        PetroleumPrice price = new PetroleumPrice(null, 75.5, null);

        // Assert
        assertNull(price.getDate());
        assertEquals(75.5, price.getAvgPricePerUnitUsd());
        assertNull(price.getUnit());
    }

    @Test
    void testNegativePrice() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 1);

        // Act
        PetroleumPrice price = new PetroleumPrice(date, -10.0, "USD/barrel");

        // Assert
        assertEquals(-10.0, price.getAvgPricePerUnitUsd());
    }

    @Test
    void testEmptyUnit() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 1);

        // Act
        PetroleumPrice price = new PetroleumPrice(date, 75.5, "");

        // Assert
        assertEquals("", price.getUnit());
    }

    @Test
    void testVerySmallPrice() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 1);

        // Act
        PetroleumPrice price = new PetroleumPrice(date, 0.01, "USD/barrel");

        // Assert
        assertEquals(0.01, price.getAvgPricePerUnitUsd(), 0.001);
    }

    @Test
    void testDecimalPrecision() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 1);

        // Act
        PetroleumPrice price = new PetroleumPrice(date, 75.555555, "USD/barrel");

        // Assert
        assertEquals(75.555555, price.getAvgPricePerUnitUsd(), 0.000001);
    }

    @Test
    void testMultipleInstancesIndependence() {
        // Arrange
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 2, 1);

        // Act
        PetroleumPrice price1 = new PetroleumPrice(date1, 75.0, "USD/barrel");
        PetroleumPrice price2 = new PetroleumPrice(date2, 80.0, "USD/gallon");

        // Assert
        assertNotEquals(price1.getDate(), price2.getDate());
        assertNotEquals(price1.getAvgPricePerUnitUsd(), price2.getAvgPricePerUnitUsd());
        assertNotEquals(price1.getUnit(), price2.getUnit());
    }
}
