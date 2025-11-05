package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.TariffRequestDTO;
import com.cs203.grp2.Asg2.DTO.TariffResponseDTO;
import com.cs203.grp2.Asg2.service.WitsService;
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
class TariffControllerTest {

    @Mock
    private WitsService witsService;

    @InjectMocks
    private TariffController tariffController;

    @Test
    void testGetTariff_WithValidParameters_ShouldReturnTariff() {
        // Arrange
        TariffResponseDTO mockResponse = new TariffResponseDTO(10.5, "MFN", "WITS Database");
        when(witsService.resolveTariff(any(TariffRequestDTO.class))).thenReturn(mockResponse);

        // Act
        TariffResponseDTO result = tariffController.getTariff(
            "BGR", "CHN", "271012", LocalDate.of(2025, 10, 14)
        );

        // Assert
        assertNotNull(result);
        assertEquals(10.5, result.ratePercent());
        assertEquals("MFN", result.basis());
        verify(witsService).resolveTariff(any(TariffRequestDTO.class));
    }

    @Test
    void testGetTariff_ShouldConvertCountryCodesToUpperCase() {
        // Arrange
        TariffResponseDTO mockResponse = new TariffResponseDTO(5.0, "FTA", "World Bank");
        when(witsService.resolveTariff(any(TariffRequestDTO.class))).thenReturn(mockResponse);

        // Act
        TariffResponseDTO result = tariffController.getTariff(
            "bgr", "chn", "271012", LocalDate.of(2025, 10, 14)
        );

        // Assert
        assertNotNull(result);
        verify(witsService).resolveTariff(argThat(req ->
            req.importer().equals("BGR") && req.exporter().equals("CHN")
        ));
    }

    @Test
    void testGetTariff_WithDifferentDate_ShouldPassCorrectDate() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 11, 5);
        TariffResponseDTO mockResponse = new TariffResponseDTO(7.5, "GSP", "WITS");
        when(witsService.resolveTariff(any(TariffRequestDTO.class))).thenReturn(mockResponse);

        // Act
        TariffResponseDTO result = tariffController.getTariff(
            "USA", "MEX", "270900", testDate
        );

        // Assert
        assertNotNull(result);
        verify(witsService).resolveTariff(argThat(req ->
            req.date().equals(testDate)
        ));
    }

    @Test
    void testGetTariff_WithDifferentHsCode_ShouldWork() {
        // Arrange
        TariffResponseDTO mockResponse = new TariffResponseDTO(12.0, "MFN", "Source");
        when(witsService.resolveTariff(any(TariffRequestDTO.class))).thenReturn(mockResponse);

        // Act
        TariffResponseDTO result = tariffController.getTariff(
            "JPN", "KOR", "270900", LocalDate.of(2025, 1, 1)
        );

        // Assert
        assertNotNull(result);
        assertEquals(12.0, result.ratePercent());
        verify(witsService).resolveTariff(any(TariffRequestDTO.class));
    }

    @Test
    void testGetTariff_ShouldBuildRequestWithAllParameters() {
        // Arrange
        TariffResponseDTO mockResponse = new TariffResponseDTO(8.0, "FTA", "DB");
        when(witsService.resolveTariff(any(TariffRequestDTO.class))).thenReturn(mockResponse);

        // Act
        tariffController.getTariff("FRA", "DEU", "271012", LocalDate.of(2025, 6, 15));

        // Assert
        verify(witsService).resolveTariff(argThat(req ->
            req.importer().equals("FRA") &&
            req.exporter().equals("DEU") &&
            req.hs6().equals("271012") &&
            req.date().equals(LocalDate.of(2025, 6, 15))
        ));
    }
}
