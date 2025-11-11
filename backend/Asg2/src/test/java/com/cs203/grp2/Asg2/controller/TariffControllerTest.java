package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.TariffRequestDTO;
import com.cs203.grp2.Asg2.models.WitsTariff;
import com.cs203.grp2.Asg2.service.WitsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffControllerTest {

    @Mock
    private WitsService witsService;

    @InjectMocks
    private TariffController tariffController;

    @Test
    void testGetTariff_WithValidParameters_ShouldReturnTariff() {
        WitsTariff mockTariff = new WitsTariff(
            "BGR", "CHN", "271012", 
            LocalDate.of(2025, 10, 14), 
            10.5, "MFN", "WITS Database"
        );
        
        when(witsService.resolveTariff(any(TariffRequestDTO.class))).thenReturn(mockTariff);

        var result = tariffController.getTariff("bgr", "chn", "271012", LocalDate.of(2025, 10, 14));

        assertNotNull(result);
        assertEquals(10.5, result.ratePercent());
        assertEquals("MFN", result.basis());
        assertEquals("WITS Database", result.sourceNote());
        verify(witsService, times(1)).resolveTariff(any(TariffRequestDTO.class));
    }

    @Test
    void testGetTariff_WithDifferentCountries_ShouldWork() {
        WitsTariff mockTariff = new WitsTariff(
            "USA", "DEU", "271012", 
            LocalDate.of(2025, 1, 1), 
            5.0, "preferential", "Trade Agreement"
        );
        
        when(witsService.resolveTariff(any(TariffRequestDTO.class))).thenReturn(mockTariff);

        var result = tariffController.getTariff("usa", "deu", "271012", LocalDate.of(2025, 1, 1));

        assertNotNull(result);
        assertEquals(5.0, result.ratePercent());
        assertEquals("preferential", result.basis());
        verify(witsService, times(1)).resolveTariff(any(TariffRequestDTO.class));
    }

    @Test
    void testGetTariff_WithZeroRate_ShouldReturnZero() {
        WitsTariff mockTariff = new WitsTariff(
            "SGP", "JPN", "271012", 
            LocalDate.of(2025, 6, 15), 
            0.0, "none", "No tariff applied"
        );
        
        when(witsService.resolveTariff(any(TariffRequestDTO.class))).thenReturn(mockTariff);

        var result = tariffController.getTariff("sgp", "jpn", "271012", LocalDate.of(2025, 6, 15));

        assertNotNull(result);
        assertEquals(0.0, result.ratePercent());
        assertEquals("none", result.basis());
        assertEquals("No tariff applied", result.sourceNote());
    }

    @Test
    void testGetTariff_WithDifferentHSCode_ShouldWork() {
        WitsTariff mockTariff = new WitsTariff(
            "FRA", "ITA", "123456", 
            LocalDate.of(2025, 3, 20), 
            15.75, "wits", "WITS Default"
        );
        
        when(witsService.resolveTariff(any(TariffRequestDTO.class))).thenReturn(mockTariff);

        var result = tariffController.getTariff("fra", "ita", "123456", LocalDate.of(2025, 3, 20));

        assertNotNull(result);
        assertEquals(15.75, result.ratePercent());
        assertEquals("wits", result.basis());
    }

    @Test
    void testGetTariff_ServiceInvoked_ShouldPassCorrectParameters() {
        String importer = "CAN";
        String exporter = "MEX";
        String hs6 = "987654";
        LocalDate date = LocalDate.of(2025, 12, 31);
        
        WitsTariff mockTariff = new WitsTariff(
            importer, exporter, hs6, date, 
            8.5, "MFN", "Standard rate"
        );
        
        when(witsService.resolveTariff(any(TariffRequestDTO.class))).thenReturn(mockTariff);

        tariffController.getTariff(importer.toLowerCase(), exporter.toLowerCase(), hs6, date);

        verify(witsService, times(1)).resolveTariff(argThat(req -> 
            req.importerIso3().equals(importer) &&
            req.exporterIso3().equals(exporter) &&
            req.hs6().equals(hs6) &&
            req.date().equals(date)
        ));
    }
}
