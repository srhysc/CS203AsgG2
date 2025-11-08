package com.cs203.grp2.Asg2.DTO;

import com.cs203.grp2.Asg2.models.Country;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RouteOptimizationRequestTest {

    private Validator validator;
    private Country exporter;
    private Country importer;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // Create sample countries
        exporter = new Country();
        exporter.setName("United States");
        exporter.setISO3("USA");
        exporter.setCode("840");
        
        importer = new Country();
        importer.setName("Canada");
        importer.setISO3("CAN");
        importer.setCode("124");
    }

    @Test
    void testValidRouteOptimizationRequest() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode("123456");
        request.setUnits(100);
        request.setCalculationDate(LocalDate.of(2024, 1, 15));

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }

    @Test
    void testInvalidHsCode_TooShort() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode("12345"); // Only 5 digits
        request.setUnits(100);
        request.setCalculationDate(LocalDate.of(2024, 1, 15));

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<RouteOptimizationRequest> violation = violations.iterator().next();
        assertEquals("HS Code must be 6 digits", violation.getMessage());
        assertEquals("hsCode", violation.getPropertyPath().toString());
    }

    @Test
    void testInvalidHsCode_TooLong() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode("1234567"); // 7 digits
        request.setUnits(100);
        request.setCalculationDate(LocalDate.of(2024, 1, 15));

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("HS Code must be 6 digits")));
    }

    @Test
    void testInvalidHsCode_ContainsLetters() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode("12AB56"); // Contains letters
        request.setUnits(100);
        request.setCalculationDate(LocalDate.of(2024, 1, 15));

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("HS Code must be 6 digits")));
    }

    @Test
    void testInvalidUnits_Zero() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode("123456");
        request.setUnits(0); // Invalid: must be at least 1
        request.setCalculationDate(LocalDate.of(2024, 1, 15));

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<RouteOptimizationRequest> violation = violations.iterator().next();
        assertEquals("units", violation.getPropertyPath().toString());
    }

    @Test
    void testInvalidUnits_Negative() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode("123456");
        request.setUnits(-10); // Invalid: must be at least 1
        request.setCalculationDate(LocalDate.of(2024, 1, 15));

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
    }

    @Test
    void testNullCalculationDate() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode("123456");
        request.setUnits(100);
        request.setCalculationDate(null); // Invalid: cannot be null

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<RouteOptimizationRequest> violation = violations.iterator().next();
        assertEquals("Date cannot be null", violation.getMessage());
        assertEquals("calculationDate", violation.getPropertyPath().toString());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        LocalDate date = LocalDate.of(2024, 6, 15);

        // Act
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode("654321");
        request.setUnits(250);
        request.setCalculationDate(date);

        // Assert
        assertEquals(exporter, request.getExportingCountry());
        assertEquals(importer, request.getImportingCountry());
        assertEquals("654321", request.getHsCode());
        assertEquals(250, request.getUnits());
        assertEquals(date, request.getCalculationDate());
    }

    @Test
    void testNullCountries() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(null);
        request.setImporter(null);
        request.setHsCode("123456");
        request.setUnits(100);
        request.setCalculationDate(LocalDate.of(2024, 1, 15));

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert - Countries don't have @NotNull constraint
        assertTrue(violations.isEmpty());
        assertNull(request.getExportingCountry());
        assertNull(request.getImportingCountry());
    }

    @Test
    void testMultipleViolations() {
        // Arrange - Multiple invalid fields
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode("ABC"); // Invalid: not 6 digits
        request.setUnits(0); // Invalid: less than 1
        request.setCalculationDate(null); // Invalid: null

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(3, violations.size());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        RouteOptimizationRequest request = new RouteOptimizationRequest();

        // Assert
        assertNull(request.getExportingCountry());
        assertNull(request.getImportingCountry());
        assertNull(request.getHsCode());
        assertEquals(0, request.getUnits());
        assertNull(request.getCalculationDate());
    }

    @Test
    void testValidHsCode_AllZeros() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode("000000"); // Valid: 6 digits
        request.setUnits(1);
        request.setCalculationDate(LocalDate.of(2024, 1, 15));

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSameExporterAndImporter() {
        // Arrange
        Country sameCountry = new Country();
        sameCountry.setName("United States");
        sameCountry.setISO3("USA");
        sameCountry.setCode("840");
        
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(sameCountry);
        request.setImporter(sameCountry);
        request.setHsCode("123456");
        request.setUnits(100);
        request.setCalculationDate(LocalDate.of(2024, 1, 15));

        // Act
        Set<ConstraintViolation<RouteOptimizationRequest>> violations = validator.validate(request);

        // Assert - No validation prevents same exporter/importer
        assertTrue(violations.isEmpty());
        assertEquals(request.getExportingCountry(), request.getImportingCountry());
    }
}
