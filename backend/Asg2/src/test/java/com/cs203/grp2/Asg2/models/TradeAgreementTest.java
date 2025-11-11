package com.cs203.grp2.Asg2.models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TradeAgreementTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // Act
        TradeAgreement agreement = new TradeAgreement();

        // Assert
        assertNotNull(agreement);
        assertNull(agreement.getAgreementName());
        assertNull(agreement.getCountryA());
        assertNull(agreement.getCountryB());
    }

    @Test
    void testParameterizedConstructor() {
        // Act
        TradeAgreement agreement = new TradeAgreement("USMCA", "USA", "CAN");

        // Assert
        assertEquals("USMCA", agreement.getAgreementName());
        assertEquals("USA", agreement.getCountryA());
        assertEquals("CAN", agreement.getCountryB());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement();

        // Act
        agreement.setAgreementName("EU-UK Trade Agreement");
        agreement.setCountryA("France");
        agreement.setCountryB("United Kingdom");

        // Assert
        assertEquals("EU-UK Trade Agreement", agreement.getAgreementName());
        assertEquals("France", agreement.getCountryA());
        assertEquals("United Kingdom", agreement.getCountryB());
    }

    @Test
    void testValidTradeAgreement() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("ASEAN", "Singapore", "Malaysia");

        // Act
        Set<ConstraintViolation<TradeAgreement>> violations = validator.validate(agreement);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankAgreementName() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("", "USA", "CAN");

        // Act
        Set<ConstraintViolation<TradeAgreement>> violations = validator.validate(agreement);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<TradeAgreement> violation = violations.iterator().next();
        assertEquals("Agreement name must not be blank", violation.getMessage());
    }

    @Test
    void testBlankCountryA() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("USMCA", "", "CAN");

        // Act
        Set<ConstraintViolation<TradeAgreement>> violations = validator.validate(agreement);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<TradeAgreement> violation = violations.iterator().next();
        assertEquals("Country A must not be blank", violation.getMessage());
    }

    @Test
    void testBlankCountryB() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("USMCA", "USA", "");

        // Act
        Set<ConstraintViolation<TradeAgreement>> violations = validator.validate(agreement);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<TradeAgreement> violation = violations.iterator().next();
        assertEquals("Country B must not be blank", violation.getMessage());
    }

    @Test
    void testMultipleBlankFields() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("", "", "");

        // Act
        Set<ConstraintViolation<TradeAgreement>> violations = validator.validate(agreement);

        // Assert
        assertEquals(3, violations.size());
    }

    @Test
    void testToString() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("USMCA", "USA", "CAN");

        // Act
        String result = agreement.toString();

        // Assert
        assertTrue(result.contains("USMCA"));
        assertTrue(result.contains("USA"));
        assertTrue(result.contains("CAN"));
        assertTrue(result.contains("TradeAgreement"));
    }

    @Test
    void testToStringWithSpecialCharacters() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("EU-UK/Brexit", "France & Germany", "UK");

        // Act
        String result = agreement.toString();

        // Assert
        assertTrue(result.contains("EU-UK/Brexit"));
        assertTrue(result.contains("France & Germany"));
    }

    @Test
    void testLongAgreementName() {
        // Arrange
        String longName = "Very Long Agreement Name That Spans Multiple Words And Countries";
        TradeAgreement agreement = new TradeAgreement(longName, "USA", "CAN");

        // Act
        Set<ConstraintViolation<TradeAgreement>> violations = validator.validate(agreement);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals(longName, agreement.getAgreementName());
    }

    @Test
    void testWhitespaceOnlyFields() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("   ", "   ", "   ");

        // Act
        Set<ConstraintViolation<TradeAgreement>> violations = validator.validate(agreement);

        // Assert
        assertEquals(3, violations.size());
    }

    @Test
    void testSameCountryForBothSides() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("Bilateral", "USA", "USA");

        // Act
        Set<ConstraintViolation<TradeAgreement>> violations = validator.validate(agreement);

        // Assert
        // No validation prevents same country - model allows it
        assertTrue(violations.isEmpty());
        assertEquals("USA", agreement.getCountryA());
        assertEquals("USA", agreement.getCountryB());
    }
}
