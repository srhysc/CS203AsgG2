package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.service.ShippingFeesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void getAllShippingFees_ReturnsAllShippingFees() {
        ShippingFeeResponseDTO dto = new ShippingFeeResponseDTO();
        dto.setCountry1Name("Saudi Arabia");
        dto.setCountry2Name("India");
        when(shippingFeesService.getAllShippingFees()).thenReturn(List.of(dto));

        List<ShippingFeeResponseDTO> result = shippingFeesController.getAllShippingFees();

        assertEquals(1, result.size());
        assertEquals("Saudi Arabia", result.get(0).getCountry1Name());
        verify(shippingFeesService).getAllShippingFees();
    }

    @Test
    void getAllCosts_ReturnsAllCostsForCountryPair() {
        ShippingFeeEntryResponseDTO entryDTO = new ShippingFeeEntryResponseDTO();
        entryDTO.setDate(LocalDate.of(2008, 4, 19));
        when(shippingFeesService.getAllCosts("SAU", "IND")).thenReturn(List.of(entryDTO));

        List<ShippingFeeEntryResponseDTO> result = shippingFeesController.getAllCosts("SAU", "IND");

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2008, 4, 19), result.get(0).getDate());
        verify(shippingFeesService).getAllCosts("SAU", "IND");
    }

    @Test
    void getCost_ReturnsCostForUnitAndDate() {
        ShippingCostDetailResponseDTO costDTO = new ShippingCostDetailResponseDTO(2.10, "USD per barrel");
        when(shippingFeesService.getCostByUnit("SAU", "IND", "barrel", LocalDate.of(2022, 2, 15))).thenReturn(costDTO);

        Object result = shippingFeesController.getCost("SAU", "IND", "2022-02-15", "barrel");

        assertNotNull(result);
        assertTrue(result instanceof ShippingCostDetailResponseDTO);
        assertEquals(2.10, ((ShippingCostDetailResponseDTO) result).getCostPerUnit());
        verify(shippingFeesService).getCostByUnit("SAU", "IND", "barrel", LocalDate.of(2022, 2, 15));
    }

    @Test
    void addOrUpdateShippingFee_CallsServiceAndReturnsResult() {
        ShippingFeeRequestDTO requestDTO = new ShippingFeeRequestDTO();
        ShippingFeeResponseDTO responseDTO = new ShippingFeeResponseDTO();
        responseDTO.setCountry1Name("Singapore");
        when(shippingFeesService.addOrUpdateShippingFee(requestDTO)).thenReturn(responseDTO);

        ShippingFeeResponseDTO result = shippingFeesController.addOrUpdateShippingFee(requestDTO);

        assertNotNull(result);
        assertEquals("Singapore", result.getCountry1Name());
        verify(shippingFeesService).addOrUpdateShippingFee(requestDTO);
    }
}