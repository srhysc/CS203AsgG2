package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PetroleumTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        List<PetroleumPrice> prices = new ArrayList<>();
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 1), 75.5, "USD/barrel"));

        // Act
        Petroleum petroleum = new Petroleum("Crude Oil", "270900", prices);

        // Assert
        assertEquals("Crude Oil", petroleum.getName());
        assertEquals("270900", petroleum.getHsCode());
        assertEquals(prices, petroleum.getPrices());
        assertEquals(1, petroleum.getPrices().size());
    }

    @Test
    void testGetPricePerUnit_WithMatchingDate() {
        // Arrange
        List<PetroleumPrice> prices = new ArrayList<>();
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 1), 75.0, "USD/barrel"));
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 15), 78.0, "USD/barrel"));
        prices.add(new PetroleumPrice(LocalDate.of(2024, 2, 1), 80.0, "USD/barrel"));
        Petroleum petroleum = new Petroleum("Crude Oil", "270900", prices);

        // Act
        double price = petroleum.getPricePerUnit(LocalDate.of(2024, 1, 20));

        // Assert
        assertEquals(78.0, price);
    }

    @Test
    void testGetPricePerUnit_WithExactDate() {
        // Arrange
        List<PetroleumPrice> prices = new ArrayList<>();
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 1), 75.0, "USD/barrel"));
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 15), 78.0, "USD/barrel"));
        Petroleum petroleum = new Petroleum("Crude Oil", "270900", prices);

        // Act
        double price = petroleum.getPricePerUnit(LocalDate.of(2024, 1, 15));

        // Assert
        assertEquals(78.0, price);
    }

    @Test
    void testGetPricePerUnit_WithDateBeforeAllPrices() {
        // Arrange
        List<PetroleumPrice> prices = new ArrayList<>();
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 15), 78.0, "USD/barrel"));
        prices.add(new PetroleumPrice(LocalDate.of(2024, 2, 1), 80.0, "USD/barrel"));
        Petroleum petroleum = new Petroleum("Crude Oil", "270900", prices);

        // Act
        Double price = petroleum.getPricePerUnit(LocalDate.of(2024, 1, 1));

        // Assert
        assertNull(price);
    }

    @Test
    void testGetPricePerUnit_WithEmptyPriceList() {
        // Arrange
        Petroleum petroleum = new Petroleum("Crude Oil", "270900", new ArrayList<>());

        // Act
        Double price = petroleum.getPricePerUnit(LocalDate.of(2024, 1, 1));

        // Assert
        assertNull(price);
    }

    @Test
    void testGetPricePerUnit_WithNullPriceList() {
        // Arrange
        Petroleum petroleum = new Petroleum("Crude Oil", "270900", null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            petroleum.getPricePerUnit(LocalDate.of(2024, 1, 1));
        });
    }

    @Test
    void testGetPricePerUnit_WithUnsortedPrices() {
        // Arrange
        List<PetroleumPrice> prices = new ArrayList<>();
        prices.add(new PetroleumPrice(LocalDate.of(2024, 2, 1), 80.0, "USD/barrel"));
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 1), 75.0, "USD/barrel"));
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 15), 78.0, "USD/barrel"));
        Petroleum petroleum = new Petroleum("Crude Oil", "270900", prices);

        // Act
        double price = petroleum.getPricePerUnit(LocalDate.of(2024, 1, 20));

        // Assert
        // Should still find the latest price before the given date
        assertEquals(78.0, price);
    }

    @Test
    void testGetPricePerUnit_WithFutureDate() {
        // Arrange
        List<PetroleumPrice> prices = new ArrayList<>();
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 1), 75.0, "USD/barrel"));
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 15), 78.0, "USD/barrel"));
        Petroleum petroleum = new Petroleum("Crude Oil", "270900", prices);

        // Act
        double price = petroleum.getPricePerUnit(LocalDate.of(2024, 12, 31));

        // Assert
        assertEquals(78.0, price);
    }

    @Test
    void testMultiplePricesOnSameDate() {
        // Arrange
        List<PetroleumPrice> prices = new ArrayList<>();
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 15), 78.0, "USD/barrel"));
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 15), 79.0, "USD/barrel"));
        Petroleum petroleum = new Petroleum("Crude Oil", "270900", prices);

        // Act
        double price = petroleum.getPricePerUnit(LocalDate.of(2024, 1, 15));

        // Assert
        // Should return one of them (likely the first in descending order)
        assertTrue(price == 78.0 || price == 79.0);
    }

    @Test
    void testEmptyNameAndHsCode() {
        // Arrange
        List<PetroleumPrice> prices = new ArrayList<>();
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 1), 75.0, "USD/barrel"));

        // Act
        Petroleum petroleum = new Petroleum("", "", prices);

        // Assert
        assertEquals("", petroleum.getName());
        assertEquals("", petroleum.getHsCode());
        assertEquals(prices, petroleum.getPrices());
    }

    @Test
    void testNullNameAndHsCode() {
        // Arrange
        List<PetroleumPrice> prices = new ArrayList<>();
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 1), 75.0, "USD/barrel"));

        // Act
        Petroleum petroleum = new Petroleum(null, null, prices);

        // Assert
        assertNull(petroleum.getName());
        assertNull(petroleum.getHsCode());
        assertEquals(prices, petroleum.getPrices());
    }

    @Test
    void testPricesListModification() {
        // Arrange
        List<PetroleumPrice> prices = new ArrayList<>();
        prices.add(new PetroleumPrice(LocalDate.of(2024, 1, 1), 75.0, "USD/barrel"));
        Petroleum petroleum = new Petroleum("Crude Oil", "270900", prices);

        // Act
        prices.add(new PetroleumPrice(LocalDate.of(2024, 2, 1), 80.0, "USD/barrel"));

        // Assert
        // The list is the same reference, so the petroleum object reflects the change
        assertEquals(2, petroleum.getPrices().size());
    }
}
