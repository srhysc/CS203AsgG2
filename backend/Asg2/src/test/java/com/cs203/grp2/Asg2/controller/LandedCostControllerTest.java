package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.LandedCostRequest;
import com.cs203.grp2.Asg2.DTO.LandedCostResponse;
import com.cs203.grp2.Asg2.service.LandedCostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LandedCostControllerTest {

    @Mock
    private LandedCostService landedCostService;

    @InjectMocks
    private LandedCostController landedCostController;

    private LandedCostRequest testRequest;
    private LandedCostResponse testResponse;

    @BeforeEach
    void setUp() {
        testRequest = new LandedCostRequest();
        testRequest.setImporterCode("840");
        testRequest.setExporterCode("156");
        testRequest.setHsCode("271012");
        testRequest.setUnits(1000);
        testRequest.setCalculationDate(LocalDate.of(2025, 10, 14));

        testResponse = new LandedCostResponse("USA", "China", "Petroleum", "271012",
            50.0, 50000.0, 10.0, 5000.0, 7.0, 3500.0, 58500.0, "USD", null);
    }

    @Test
    void testCalculateLandedCost_WithValidRequest_ShouldReturnResponse() {
        // Arrange
        when(landedCostService.calculateLandedCost(any(LandedCostRequest.class)))
            .thenReturn(testResponse);

        // Act
        LandedCostResponse result = landedCostController.calculateLandedCost(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(58500.0, result.getTotalLandedCost());
        verify(landedCostService).calculateLandedCost(any(LandedCostRequest.class));
    }

    @Test
    void testCalculateLandedCostViaGet_WithNumericCodes_ShouldSetImporterAndExporterCodes() {
        // Arrange
        when(landedCostService.calculateLandedCost(any(LandedCostRequest.class)))
            .thenReturn(testResponse);

        // Act
        LandedCostResponse result = landedCostController.calculateLandedCostViaGet(
            "840", "156", "271012", 1000, LocalDate.of(2025, 10, 14)
        );

        // Assert
        assertNotNull(result);
        verify(landedCostService).calculateLandedCost(argThat(req ->
            req.getImporterCode().equals("840") &&
            req.getExporterCode().equals("156") &&
            req.getHsCode().equals("271012") &&
            req.getUnits() == 1000
        ));
    }

    @Test
    void testCalculateLandedCostViaGet_WithCountryNames_ShouldSetImporterAndExporterNames() {
        // Arrange
        when(landedCostService.calculateLandedCost(any(LandedCostRequest.class)))
            .thenReturn(testResponse);

        // Act
        LandedCostResponse result = landedCostController.calculateLandedCostViaGet(
            "USA", "China", "271012", 1000, LocalDate.of(2025, 10, 14)
        );

        // Assert
        assertNotNull(result);
        verify(landedCostService).calculateLandedCost(argThat(req ->
            req.getImporterName().equals("USA") &&
            req.getExporterName().equals("China")
        ));
    }

    @Test
    void testCalculateLandedCostViaGet_WithMixedInputs_ShouldHandleCorrectly() {
        // Arrange
        when(landedCostService.calculateLandedCost(any(LandedCostRequest.class)))
            .thenReturn(testResponse);

        // Act
        LandedCostResponse result = landedCostController.calculateLandedCostViaGet(
            "840", "China", "271012", 1000, LocalDate.of(2025, 10, 14)
        );

        // Assert
        assertNotNull(result);
        verify(landedCostService).calculateLandedCost(argThat(req ->
            req.getImporterCode().equals("840") &&
            req.getExporterName().equals("China")
        ));
    }

    @Test
    void testCalculateLandedCostViaGet_WithAllParameters_ShouldBuildRequestCorrectly() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 11, 5);
        when(landedCostService.calculateLandedCost(any(LandedCostRequest.class)))
            .thenReturn(testResponse);

        // Act
        LandedCostResponse result = landedCostController.calculateLandedCostViaGet(
            "702", "840", "270900", 5000, testDate
        );

        // Assert
        assertNotNull(result);
        verify(landedCostService).calculateLandedCost(argThat(req ->
            req.getHsCode().equals("270900") &&
            req.getUnits() == 5000 &&
            req.getCalculationDate().equals(testDate)
        ));
    }

    @Test
    void testCalculateLandedCost_ServiceReturnsNull_ShouldReturnNull() {
        // Arrange
        when(landedCostService.calculateLandedCost(any(LandedCostRequest.class)))
            .thenReturn(null);

        // Act
        LandedCostResponse result = landedCostController.calculateLandedCost(testRequest);

        // Assert
        assertNull(result);
        verify(landedCostService).calculateLandedCost(any(LandedCostRequest.class));
    }
}
