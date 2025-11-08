package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LandedCostRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testDefaultConstructor() {
        // Act
        LandedCostRequest request = new LandedCostRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getImporterCode());
        assertNull(request.getExporterCode());
        assertNull(request.getHsCode());
        assertEquals(0, request.getUnits());
        assertNull(request.getCalculationDate());
    }

    @Test
    void testSettersAndGetters_WithValidData() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();
        LocalDate testDate = LocalDate.of(2025, 10, 14);

        // Act
        request.setImporterCode("100");
        request.setExporterCode("156");
        request.setImporterName("Bulgaria");
        request.setExporterName("China");
        request.setHsCode("271012");
        request.setUnits(500);
        request.setCalculationDate(testDate);

        // Assert
        assertEquals("100", request.getImporterCode());
        assertEquals("156", request.getExporterCode());
        assertEquals("Bulgaria", request.getImporterName());
        assertEquals("China", request.getExporterName());
        assertEquals("271012", request.getHsCode());
        assertEquals(500, request.getUnits());
        assertEquals(testDate, request.getCalculationDate());
    }

    @Test
    void testValidation_WithValidHsCode_ShouldPass() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();
        request.setHsCode("123456");
        request.setUnits(10);
        request.setCalculationDate(LocalDate.now());

        // Act
        Set<ConstraintViolation<LandedCostRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_WithInvalidHsCode_TooShort_ShouldFail() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();
        request.setHsCode("12345"); // Only 5 digits
        request.setUnits(10);
        request.setCalculationDate(LocalDate.now());

        // Act
        Set<ConstraintViolation<LandedCostRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("HS Code must be 6 digits")));
    }

    @Test
    void testValidation_WithInvalidHsCode_TooLong_ShouldFail() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();
        request.setHsCode("1234567"); // 7 digits
        request.setUnits(10);
        request.setCalculationDate(LocalDate.now());

        // Act
        Set<ConstraintViolation<LandedCostRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("HS Code must be 6 digits")));
    }

    @Test
    void testValidation_WithInvalidHsCode_NonNumeric_ShouldFail() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();
        request.setHsCode("ABC123"); // Contains letters
        request.setUnits(10);
        request.setCalculationDate(LocalDate.now());

        // Act
        Set<ConstraintViolation<LandedCostRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidation_WithZeroUnits_ShouldFail() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();
        request.setHsCode("123456");
        request.setUnits(0); // Below minimum
        request.setCalculationDate(LocalDate.now());

        // Act
        Set<ConstraintViolation<LandedCostRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("units")));
    }

    @Test
    void testValidation_WithNegativeUnits_ShouldFail() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();
        request.setHsCode("123456");
        request.setUnits(-5); // Negative value
        request.setCalculationDate(LocalDate.now());

        // Act
        Set<ConstraintViolation<LandedCostRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidation_WithNullDate_ShouldFail() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();
        request.setHsCode("123456");
        request.setUnits(10);
        request.setCalculationDate(null);

        // Act
        Set<ConstraintViolation<LandedCostRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Date cannot be null")));
    }

    @Test
    void testSetCountryNames_WithNullValues() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();

        // Act
        request.setImporterName(null);
        request.setExporterName(null);

        // Assert
        assertNull(request.getImporterName());
        assertNull(request.getExporterName());
    }

    @Test
    void testSetCountryCodes_WithEmptyStrings() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();

        // Act
        request.setImporterCode("");
        request.setExporterCode("");

        // Assert
        assertEquals("", request.getImporterCode());
        assertEquals("", request.getExporterCode());
    }

    @Test
    void testCompleteValidRequest() {
        // Arrange
        LandedCostRequest request = new LandedCostRequest();
        LocalDate testDate = LocalDate.of(2025, 6, 15);

        request.setImporterCode("840");
        request.setExporterCode("124");
        request.setImporterName("United States");
        request.setExporterName("Canada");
        request.setHsCode("270900");
        request.setUnits(1000);
        request.setCalculationDate(testDate);

        // Act
        Set<ConstraintViolation<LandedCostRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
        assertNotNull(request.getImporterCode());
        assertNotNull(request.getExporterCode());
        assertNotNull(request.getHsCode());
        assertTrue(request.getUnits() > 0);
        assertNotNull(request.getCalculationDate());
    }
}
