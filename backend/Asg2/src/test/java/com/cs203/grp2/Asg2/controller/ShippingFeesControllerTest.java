package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.service.ShippingFeesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShippingFeesControllerTest {

    @Mock
    private ShippingFeesService shippingFeesService;

    @InjectMocks
    private ShippingFeesController shippingFeesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllShippingFees_Success() {
        // Arrange
        List<ShippingFeeResponseDTO> fees = new ArrayList<>();
        ShippingFeeResponseDTO fee = new ShippingFeeResponseDTO();
        fees.add(fee);

        when(shippingFeesService.getAllShippingFees()).thenReturn(fees);

        // Act
        List<ShippingFeeResponseDTO> result = shippingFeesController.getAllShippingFees();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(shippingFeesService).getAllShippingFees();
    }

    @Test
    void testGetAllCosts_Success() {
        // Arrange
        List<ShippingFeeEntryResponseDTO> entries = new ArrayList<>();
        ShippingFeeEntryResponseDTO entry = new ShippingFeeEntryResponseDTO();
        entries.add(entry);

        when(shippingFeesService.getAllCosts("SGP", "MYS")).thenReturn(entries);

        // Act
        List<ShippingFeeEntryResponseDTO> result = shippingFeesController.getAllCosts("SGP", "MYS");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(shippingFeesService).getAllCosts("SGP", "MYS");
    }

    @Test
    void testGetCost_NoDateNoUnit_ReturnsAllHistoricalCosts() {
        // Arrange
        List<ShippingFeeEntryResponseDTO> entries = new ArrayList<>();
        ShippingFeeEntryResponseDTO entry = new ShippingFeeEntryResponseDTO();
        entries.add(entry);

        when(shippingFeesService.getAllCosts("SGP", "MYS")).thenReturn(entries);

        // Act
        Object result = shippingFeesController.getCost("SGP", "MYS", null, null);

        // Assert
        assertTrue(result instanceof List);
        verify(shippingFeesService).getAllCosts("SGP", "MYS");
    }

    @Test
    void testGetCost_NoDateWithUnit_ReturnsHistoricalCostsByUnit() {
        // Arrange
        List<ShippingFeeEntryResponseDTO> entries = new ArrayList<>();
        ShippingFeeEntryResponseDTO entry = new ShippingFeeEntryResponseDTO();
        entries.add(entry);

        when(shippingFeesService.getAllCostsByUnit("SGP", "MYS", "barrel")).thenReturn(entries);

        // Act
        Object result = shippingFeesController.getCost("SGP", "MYS", null, "barrel");

        // Assert
        assertTrue(result instanceof List);
        verify(shippingFeesService).getAllCostsByUnit("SGP", "MYS", "barrel");
    }

    @Test
    void testGetCost_WithDateAndUnit_ReturnsSingleCost() {
        // Arrange
        ShippingCostDetailResponseDTO cost = new ShippingCostDetailResponseDTO();
        LocalDate date = LocalDate.of(2024, 1, 1);

        when(shippingFeesService.getCostByUnit("SGP", "MYS", "barrel", date)).thenReturn(cost);

        // Act
        Object result = shippingFeesController.getCost("SGP", "MYS", "2024-01-01", "barrel");

        // Assert
        assertTrue(result instanceof List);
        @SuppressWarnings("unchecked")
        List<ShippingCostDetailResponseDTO> resultList = (List<ShippingCostDetailResponseDTO>) result;
        assertEquals(1, resultList.size());
        verify(shippingFeesService).getCostByUnit("SGP", "MYS", "barrel", date);
    }

    @Test
    void testGetCost_WithDateNoUnit_ReturnsEntry() {
        // Arrange
        ShippingFeeEntryResponseDTO entry = new ShippingFeeEntryResponseDTO();
        LocalDate date = LocalDate.of(2024, 1, 1);

        when(shippingFeesService.getLatestCost("SGP", "MYS", date)).thenReturn(entry);

        // Act
        Object result = shippingFeesController.getCost("SGP", "MYS", "2024-01-01", null);

        // Assert
        assertTrue(result instanceof List);
        @SuppressWarnings("unchecked")
        List<ShippingFeeEntryResponseDTO> resultList = (List<ShippingFeeEntryResponseDTO>) result;
        assertEquals(1, resultList.size());
        verify(shippingFeesService).getLatestCost("SGP", "MYS", date);
    }

    @Test
    void testGetCost_WithDateAndUnit_NotFound_ReturnsEmptyList() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(shippingFeesService.getCostByUnit("SGP", "MYS", "barrel", date)).thenReturn(null);

        // Act
        Object result = shippingFeesController.getCost("SGP", "MYS", "2024-01-01", "barrel");

        // Assert
        assertTrue(result instanceof List);
        @SuppressWarnings("unchecked")
        List<?> resultList = (List<?>) result;
        assertEquals(0, resultList.size());
    }

    @Test
    void testGetCost_WithDateNoUnit_NotFound_ReturnsEmptyList() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(shippingFeesService.getLatestCost("SGP", "MYS", date)).thenReturn(null);

        // Act
        Object result = shippingFeesController.getCost("SGP", "MYS", "2024-01-01", null);

        // Assert
        assertTrue(result instanceof List);
        @SuppressWarnings("unchecked")
        List<?> resultList = (List<?>) result;
        assertEquals(0, resultList.size());
    }

    @Test
    void testAddOrUpdateShippingFee_Success() {
        // Arrange
        ShippingFeeRequestDTO requestDTO = new ShippingFeeRequestDTO();
        ShippingFeeResponseDTO responseDTO = new ShippingFeeResponseDTO();

        when(shippingFeesService.addOrUpdateShippingFee(requestDTO)).thenReturn(responseDTO);

        // Act
        ShippingFeeResponseDTO result = shippingFeesController.addOrUpdateShippingFee(requestDTO);

        // Assert
        assertNotNull(result);
        verify(shippingFeesService).addOrUpdateShippingFee(requestDTO);
    }

    @Test
    void testGetCost_EmptyDate_WithUnit_ReturnsHistoricalCostsByUnit() {
        // Arrange
        List<ShippingFeeEntryResponseDTO> entries = new ArrayList<>();
        when(shippingFeesService.getAllCostsByUnit("SGP", "MYS", "ton")).thenReturn(entries);

        // Act
        Object result = shippingFeesController.getCost("SGP", "MYS", "", "ton");

        // Assert
        assertTrue(result instanceof List);
        verify(shippingFeesService).getAllCostsByUnit("SGP", "MYS", "ton");
    }

    @Test
    void testGetCost_EmptyDateAndUnit_ReturnsAllHistoricalCosts() {
        // Arrange
        List<ShippingFeeEntryResponseDTO> entries = new ArrayList<>();
        when(shippingFeesService.getAllCosts("SGP", "MYS")).thenReturn(entries);

        // Act
        Object result = shippingFeesController.getCost("SGP", "MYS", "", "");

        // Assert
        assertTrue(result instanceof List);
        verify(shippingFeesService).getAllCosts("SGP", "MYS");
    }
}
