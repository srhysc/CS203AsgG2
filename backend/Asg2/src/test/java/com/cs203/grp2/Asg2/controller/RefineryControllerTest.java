package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.CostDetailResponseDTO;
import com.cs203.grp2.Asg2.DTO.RefineryCostResponseDTO;
import com.cs203.grp2.Asg2.DTO.RefineryResponseDTO;
import com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException;
import com.cs203.grp2.Asg2.exceptions.RefineryNotFoundException;
import com.cs203.grp2.Asg2.service.RefineryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefineryControllerTest {

    @Mock
    private RefineryService refineryService;

    @InjectMocks
    private RefineryController refineryController;

    private RefineryResponseDTO testRefinery;
    private RefineryCostResponseDTO testCost;
    private CostDetailResponseDTO testCostDetail;

    @BeforeEach
    void setUp() {
        testRefinery = new RefineryResponseDTO();
        testRefinery.setName("Test Refinery");
        testRefinery.setCountryIso3("SGP");

        testCost = new RefineryCostResponseDTO();
        testCost.setDate("2024-01-01");

        testCostDetail = new CostDetailResponseDTO();
        testCostDetail.setCost_per_unit(50.0);
        testCostDetail.setUnit("barrel");
    }

    @Test
    void testGetAllRefineries_Success() {
        // Arrange
        List<RefineryResponseDTO> refineries = List.of(testRefinery);
        when(refineryService.getAllRefineries()).thenReturn(refineries);

        // Act
        List<RefineryResponseDTO> result = refineryController.getAllRefineries();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Refinery", result.get(0).getName());
        verify(refineryService).getAllRefineries();
    }

    @Test
    void testGetAllRefineries_EmptyList_ThrowsException() {
        // Arrange
        when(refineryService.getAllRefineries()).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(RefineryNotFoundException.class, () -> {
            refineryController.getAllRefineries();
        });
        verify(refineryService).getAllRefineries();
    }

    @Test
    void testGetAllRefineries_Null_ThrowsException() {
        // Arrange
        when(refineryService.getAllRefineries()).thenReturn(null);

        // Act & Assert
        assertThrows(RefineryNotFoundException.class, () -> {
            refineryController.getAllRefineries();
        });
    }

    @Test
    void testGetRefineriesByCountry_Success() {
        // Arrange
        List<RefineryResponseDTO> refineries = List.of(testRefinery);
        when(refineryService.getRefineriesByCountry("SGP")).thenReturn(refineries);

        // Act
        List<RefineryResponseDTO> result = refineryController.getRefineriesByCountry("SGP");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(refineryService).getRefineriesByCountry("SGP");
    }

    @Test
    void testGetRefineriesByCountry_NotFound_ThrowsException() {
        // Arrange
        when(refineryService.getRefineriesByCountry("XXX")).thenReturn(new ArrayList<>());

        // Act & Assert
        RefineryNotFoundException exception = assertThrows(RefineryNotFoundException.class, () -> {
            refineryController.getRefineriesByCountry("XXX");
        });
        assertTrue(exception.getMessage().contains("XXX"));
    }

    @Test
    void testGetRefinery_Success() {
        // Arrange
        when(refineryService.getRefinery("SGP", "Test Refinery")).thenReturn(testRefinery);

        // Act
        RefineryResponseDTO result = refineryController.getRefinery("SGP", "Test Refinery");

        // Assert
        assertNotNull(result);
        assertEquals("Test Refinery", result.getName());
        verify(refineryService).getRefinery("SGP", "Test Refinery");
    }

    @Test
    void testGetRefinery_NotFound_ThrowsException() {
        // Arrange
        when(refineryService.getRefinery("SGP", "Nonexistent")).thenReturn(null);

        // Act & Assert
        RefineryNotFoundException exception = assertThrows(RefineryNotFoundException.class, () -> {
            refineryController.getRefinery("SGP", "Nonexistent");
        });
        assertTrue(exception.getMessage().contains("Nonexistent"));
        assertTrue(exception.getMessage().contains("SGP"));
    }

    @Test
    void testGetAllCosts_Success() {
        // Arrange
        List<RefineryCostResponseDTO> costs = List.of(testCost);
        when(refineryService.getAllCosts("SGP", "Test Refinery")).thenReturn(costs);

        // Act
        List<RefineryCostResponseDTO> result = refineryController.getAllCosts("SGP", "Test Refinery");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(refineryService).getAllCosts("SGP", "Test Refinery");
    }

    @Test
    void testGetAllCosts_EmptyList_ThrowsException() {
        // Arrange
        when(refineryService.getAllCosts("SGP", "Test Refinery")).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(RefineryNotFoundException.class, () -> {
            refineryController.getAllCosts("SGP", "Test Refinery");
        });
    }

    @Test
    void testGetLatestCost_Success() {
        // Arrange
        List<RefineryCostResponseDTO> costs = List.of(testCost);
        when(refineryService.getLatestCost("SGP", "Test Refinery", "2024-01-01")).thenReturn(costs);

        // Act
        List<RefineryCostResponseDTO> result = refineryController.getLatestCost("SGP", "Test Refinery", "2024-01-01");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(refineryService).getLatestCost("SGP", "Test Refinery", "2024-01-01");
    }

    @Test
    void testGetLatestCost_WithoutDate_Success() {
        // Arrange
        List<RefineryCostResponseDTO> costs = List.of(testCost);
        when(refineryService.getLatestCost("SGP", "Test Refinery", null)).thenReturn(costs);

        // Act
        List<RefineryCostResponseDTO> result = refineryController.getLatestCost("SGP", "Test Refinery", null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetCostByUnit_Success() {
        // Arrange
        when(refineryService.getCostByUnit("SGP", "Test Refinery", "barrel", "2024-01-01"))
            .thenReturn(testCostDetail);

        // Act
        CostDetailResponseDTO result = refineryController.getCostByUnit("SGP", "Test Refinery", "barrel", "2024-01-01");

        // Assert
        assertNotNull(result);
        assertEquals("barrel", result.getUnit());
        assertEquals(50.0, result.getCost_per_unit());
        verify(refineryService).getCostByUnit("SGP", "Test Refinery", "barrel", "2024-01-01");
    }

    @Test
    void testGetCostByUnit_NullUnit_ThrowsException() {
        // Act & Assert
        assertThrows(GeneralBadRequestException.class, () -> {
            refineryController.getCostByUnit("SGP", "Test Refinery", null, "2024-01-01");
        });
        verify(refineryService, never()).getCostByUnit(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testGetCostByUnit_EmptyUnit_ThrowsException() {
        // Act & Assert
        assertThrows(GeneralBadRequestException.class, () -> {
            refineryController.getCostByUnit("SGP", "Test Refinery", "", "2024-01-01");
        });
    }

    @Test
    void testGetCostByUnit_NotFound_ThrowsException() {
        // Arrange
        when(refineryService.getCostByUnit("SGP", "Test Refinery", "ton", "2024-01-01"))
            .thenReturn(null);

        // Act & Assert
        RefineryNotFoundException exception = assertThrows(RefineryNotFoundException.class, () -> {
            refineryController.getCostByUnit("SGP", "Test Refinery", "ton", "2024-01-01");
        });
        assertTrue(exception.getMessage().contains("ton"));
    }
}
