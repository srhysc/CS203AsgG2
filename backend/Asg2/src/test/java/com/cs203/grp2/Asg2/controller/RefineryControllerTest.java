package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.RefineryResponseDTO;
import com.cs203.grp2.Asg2.DTO.RefineryCostResponseDTO;
import com.cs203.grp2.Asg2.DTO.CostDetailResponseDTO;
import com.cs203.grp2.Asg2.DTO.RefineryRequestDTO;
import com.cs203.grp2.Asg2.service.RefineryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefineryControllerTest {

    @Mock
    private RefineryService refineryService;

    @InjectMocks
    private RefineryController refineryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test: getAllRefineries returns all refineries from service
    @Test
    void getAllRefineries_ReturnsAllRefineries() {
        // Arrange
        RefineryResponseDTO dto = new RefineryResponseDTO();
        dto.setName("Jamnagar Refinery");
        when(refineryService.getAllRefineries()).thenReturn(List.of(dto));

        // Act
        List<RefineryResponseDTO> result = refineryController.getAllRefineries();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Jamnagar Refinery", result.get(0).getName());
        verify(refineryService).getAllRefineries();
    }

    // Test: getRefineriesByCountry returns filtered refineries
    @Test
    void getRefineriesByCountry_ReturnsFilteredRefineries() {
        // Arrange
        RefineryResponseDTO dto = new RefineryResponseDTO();
        dto.setName("Jamnagar Refinery");
        when(refineryService.getRefineriesByCountry("IND")).thenReturn(List.of(dto));

        // Act
        List<RefineryResponseDTO> result = refineryController.getRefineriesByCountry("IND");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Jamnagar Refinery", result.get(0).getName());
        verify(refineryService).getRefineriesByCountry("IND");
    }

    // Test: getRefinery returns correct refinery
    @Test
    void getRefinery_ReturnsCorrectRefinery() {
        // Arrange
        RefineryResponseDTO dto = new RefineryResponseDTO();
        dto.setName("Jamnagar Refinery");
        when(refineryService.getRefinery("IND", "Jamnagar Refinery")).thenReturn(dto);

        // Act
        RefineryResponseDTO result = refineryController.getRefinery("IND", "Jamnagar Refinery");

        // Assert
        assertNotNull(result);
        assertEquals("Jamnagar Refinery", result.getName());
        verify(refineryService).getRefinery("IND", "Jamnagar Refinery");
    }

    // Test: getAllCosts returns all costs for a refinery
    @Test
    void getAllCosts_ReturnsAllCosts() {
        // Arrange
        RefineryCostResponseDTO costDTO = new RefineryCostResponseDTO();
        costDTO.setDate(LocalDate.of(2020, 1, 1));
        when(refineryService.getAllCosts("IND", "Jamnagar Refinery")).thenReturn(List.of(costDTO));

        // Act
        List<RefineryCostResponseDTO> result = refineryController.getAllCosts("IND", "Jamnagar Refinery");

        // Assert
        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2020, 1, 1), result.get(0).getDate());
        verify(refineryService).getAllCosts("IND", "Jamnagar Refinery");
    }

    // Test: getLatestCost returns the most applicable cost for a date
    @Test
    void getLatestCost_ReturnsMostApplicableCost() {
        // Arrange
        RefineryCostResponseDTO costDTO = new RefineryCostResponseDTO();
        costDTO.setDate(LocalDate.of(2020, 1, 1));
        when(refineryService.getLatestCost("IND", "Jamnagar Refinery", LocalDate.of(2020, 1, 1))).thenReturn(costDTO);

        // Act
        RefineryCostResponseDTO result = refineryController.getLatestCost("IND", "Jamnagar Refinery", "2020-01-01");

        // Assert
        assertNotNull(result);
        assertEquals(LocalDate.of(2020, 1, 1), result.getDate());
        verify(refineryService).getLatestCost("IND", "Jamnagar Refinery", LocalDate.of(2020, 1, 1));
    }

    // Test: getCostByUnit returns cost for requested unit
    @Test
    void getCostByUnit_ReturnsCostForUnit() {
        // Arrange
        CostDetailResponseDTO costDetail = new CostDetailResponseDTO();
        costDetail.setCostPerUnit(5.5);
        costDetail.setUnit("USD per barrel");
        when(refineryService.getCostByUnit("IND", "Jamnagar Refinery", "barrel", LocalDate.of(2020, 1, 1))).thenReturn(costDetail);

        // Act
        CostDetailResponseDTO result = refineryController.getCostByUnit("IND", "Jamnagar Refinery", "barrel", "2020-01-01");

        // Assert
        assertNotNull(result);
        assertEquals(5.5, result.getCostPerUnit());
        assertEquals("USD per barrel", result.getUnit());
        verify(refineryService).getCostByUnit("IND", "Jamnagar Refinery", "barrel", LocalDate.of(2020, 1, 1));
    }

    // Test: addOrUpdateRefinery calls service and returns result
    @Test
    void addOrUpdateRefinery_CallsServiceAndReturnsResult() {
        // Arrange
        RefineryRequestDTO requestDTO = new RefineryRequestDTO();
        requestDTO.setName("New Refinery");
        RefineryResponseDTO responseDTO = new RefineryResponseDTO();
        responseDTO.setName("New Refinery");
        when(refineryService.addOrUpdateRefinery("SGP", requestDTO)).thenReturn(responseDTO);

        // Act
        RefineryResponseDTO result = refineryController.addOrUpdateRefinery("SGP", requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("New Refinery", result.getName());
        verify(refineryService).addOrUpdateRefinery("SGP", requestDTO);
    }
}