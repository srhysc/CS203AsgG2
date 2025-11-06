package com.cs203.grp2.Asg2.models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ShippingFeesTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // Act
        ShippingFees fees = new ShippingFees();

        // Assert
        assertNotNull(fees);
        assertEquals(0.0, fees.getFee());
        assertNull(fees.getImportingCountry());
        assertNull(fees.getExportingCountry());
    }

    @Test
    void testParameterizedConstructor() {
        // Act
        ShippingFees fees = new ShippingFees(150.50, "USA", "China");

        // Assert
        assertEquals(150.50, fees.getFee());
        assertEquals("USA", fees.getImportingCountry());
        assertEquals("China", fees.getExportingCountry());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        ShippingFees fees = new ShippingFees();

        // Act
        fees.setFee(250.75);
        fees.setImportingCountry("Germany");
        fees.setExportingCountry("France");

        // Assert
        assertEquals(250.75, fees.getFee());
        assertEquals("Germany", fees.getImportingCountry());
        assertEquals("France", fees.getExportingCountry());
    }

    @Test
    void testValidShippingFees() {
        // Arrange
        ShippingFees fees = new ShippingFees(100.0, "Singapore", "Malaysia");

        // Act
        Set<ConstraintViolation<ShippingFees>> violations = validator.validate(fees);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNegativeFee() {
        // Arrange
        ShippingFees fees = new ShippingFees(-50.0, "USA", "Canada");

        // Act
        Set<ConstraintViolation<ShippingFees>> violations = validator.validate(fees);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<ShippingFees> violation = violations.iterator().next();
        assertEquals("Fee must be zero or positive", violation.getMessage());
    }

    @Test
    void testZeroFee() {
        // Arrange
        ShippingFees fees = new ShippingFees(0.0, "USA", "Canada");

        // Act
        Set<ConstraintViolation<ShippingFees>> violations = validator.validate(fees);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankImportingCountry() {
        // Arrange
        ShippingFees fees = new ShippingFees(100.0, "", "China");

        // Act
        Set<ConstraintViolation<ShippingFees>> violations = validator.validate(fees);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<ShippingFees> violation = violations.iterator().next();
        assertEquals("Importing country must not be blank", violation.getMessage());
    }

    @Test
    void testBlankExportingCountry() {
        // Arrange
        ShippingFees fees = new ShippingFees(100.0, "USA", "");

        // Act
        Set<ConstraintViolation<ShippingFees>> violations = validator.validate(fees);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<ShippingFees> violation = violations.iterator().next();
        assertEquals("Exporting country must not be blank", violation.getMessage());
    }

    @Test
    void testMultipleViolations() {
        // Arrange
        ShippingFees fees = new ShippingFees(-100.0, "", "");

        // Act
        Set<ConstraintViolation<ShippingFees>> violations = validator.validate(fees);

        // Assert
        assertEquals(3, violations.size());
    }

    @Test
    void testToString() {
        // Arrange
        ShippingFees fees = new ShippingFees(150.50, "USA", "China");

        // Act
        String result = fees.toString();

        // Assert
        assertTrue(result.contains("150.5"));
        assertTrue(result.contains("USA"));
        assertTrue(result.contains("China"));
        assertTrue(result.contains("ShippingFees"));
    }

    @Test
    void testHighFeeValue() {
        // Arrange
        ShippingFees fees = new ShippingFees(99999.99, "USA", "Australia");

        // Act
        Set<ConstraintViolation<ShippingFees>> violations = validator.validate(fees);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals(99999.99, fees.getFee());
    }

    @Test
    void testWhitespaceOnlyCountries() {
        // Arrange
        ShippingFees fees = new ShippingFees(100.0, "   ", "   ");

        // Act
        Set<ConstraintViolation<ShippingFees>> violations = validator.validate(fees);

        // Assert
        assertEquals(2, violations.size());
    }

    @Test
    void testSameImportingAndExportingCountry() {
        // Arrange
        ShippingFees fees = new ShippingFees(50.0, "USA", "USA");

        // Act
        Set<ConstraintViolation<ShippingFees>> violations = validator.validate(fees);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals("USA", fees.getImportingCountry());
        assertEquals("USA", fees.getExportingCountry());
    }

    @Test
    void testVerySmallFee() {
        // Arrange
        ShippingFees fees = new ShippingFees(0.01, "Singapore", "Malaysia");

        // Act
        Set<ConstraintViolation<ShippingFees>> violations = validator.validate(fees);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals(0.01, fees.getFee());
    }
}
