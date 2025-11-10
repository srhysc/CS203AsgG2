package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException;
import com.cs203.grp2.Asg2.exceptions.PetroleumNotFoundException;
import com.cs203.grp2.Asg2.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LandedCostServiceTest {

    @Mock
    private CountryService countryService;

    @Mock
    private PetroleumService petroleumService;

    @Mock
    private WitsService tariffService;

    @Mock
    private RouteOptimizeService routeOptimizationService;

    @InjectMocks
    private LandedCostService landedCostService;

    private Country singapore;
    private Country malaysia;
    private Country china;
    private Petroleum crudeOil;
    private LandedCostRequest validRequest;

    @BeforeEach
    void setUp() {
        // Set up test data
        singapore = new Country();
        singapore.setCode("702");
        singapore.setName("Singapore");

        malaysia = new Country();
        malaysia.setCode("458");
        malaysia.setName("Malaysia");

        china = new Country();
        china.setCode("156");
        china.setName("China");

        crudeOil = new Petroleum("Crude Oil", "270900", Arrays.asList());

        validRequest = new LandedCostRequest();
        validRequest.setImporterCode("702");
        validRequest.setExporterCode("458");
        validRequest.setHsCode("270900");
        validRequest.setUnits(1000);
        validRequest.setCalculationDate(LocalDate.now());
    }

    @Test
    void testCalculateLandedCost_Success() {
        // Arrange
        RouteBreakdown directRoute = new RouteBreakdown(
            "Malaysia", null, "Singapore",
            100.0, 10.0, 9.0, 124.0, 9.0, "Crude Oil", 0.0
        );

        RouteBreakdown transitRoute1 = new RouteBreakdown(
            "Malaysia", "China", "Singapore",
            100.0, 12.0, 13.0, 131.0, 9.0, "Crude Oil", 0.0
        );

        RouteBreakdown transitRoute2 = new RouteBreakdown(
            "Malaysia", "Thailand", "Singapore",
            100.0, 11.0, 7.0, 123.5, 9.0, "Crude Oil", 0.0
        );

        List<RouteBreakdown> topRoutes = Arrays.asList(directRoute, transitRoute1, transitRoute2);

        RouteOptimizationResponse routeResponse = new RouteOptimizationResponse(topRoutes, 50.0);

        when(countryService.getCountryByCode("702")).thenReturn(singapore);
        when(countryService.getCountryByCode("458")).thenReturn(malaysia);
        when(petroleumService.getPetroleumByHsCode("270900")).thenReturn(crudeOil);
        when(routeOptimizationService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(routeResponse);

        // Act
        LandedCostResponse response = landedCostService.calculateLandedCost(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Singapore", response.getImportingCountry());
        assertEquals("Malaysia", response.getExportingCountry());
        assertEquals("Crude Oil", response.getPetroleumName());
        assertEquals("270900", response.getHsCode());
        assertEquals(50.0, response.getPricePerUnit());
        assertEquals(100.0, response.getBasePrice());
        assertEquals(124.0, response.getTotalLandedCost());
        assertEquals("USD", response.getCurrency());
        assertNotNull(response.getAlternativeRoutes());
        assertEquals(2, response.getAlternativeRoutes().size());
        assertTrue(response.getAlternativeRoutes().containsKey("China"));
        assertTrue(response.getAlternativeRoutes().containsKey("Thailand"));

        verify(countryService).getCountryByCode("702");
        verify(countryService).getCountryByCode("458");
        verify(petroleumService).getPetroleumByHsCode("270900");
        verify(routeOptimizationService).optimizeRoutes(any(RouteOptimizationRequest.class));
    }

    @Test
    void testCalculateLandedCost_WithCountryNames() {
        // Arrange
        validRequest.setImporterCode(null);
        validRequest.setImporterName("Singapore");
        validRequest.setExporterCode(null);
        validRequest.setExporterName("Malaysia");

        RouteBreakdown directRoute = new RouteBreakdown(
            "Malaysia", null, "Singapore",
            100.0, 10.0, 9.0, 124.0, 9.0, "Crude Oil", 0.0
        );

        List<RouteBreakdown> topRoutes = Arrays.asList(directRoute);

        RouteOptimizationResponse routeResponse = new RouteOptimizationResponse(topRoutes, 50.0);

        when(countryService.getCountryByName("Singapore")).thenReturn(singapore);
        when(countryService.getCountryByName("Malaysia")).thenReturn(malaysia);
        when(petroleumService.getPetroleumByHsCode("270900")).thenReturn(crudeOil);
        when(routeOptimizationService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(routeResponse);

        // Act
        LandedCostResponse response = landedCostService.calculateLandedCost(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Singapore", response.getImportingCountry());
        assertEquals("Malaysia", response.getExportingCountry());

        verify(countryService).getCountryByName("Singapore");
        verify(countryService).getCountryByName("Malaysia");
    }

    @Test
    void testCalculateLandedCost_SameImporterAndExporter_ThrowsException() {
        // Arrange
        validRequest.setImporterCode("702");
        validRequest.setExporterCode("702");

        when(countryService.getCountryByCode("702")).thenReturn(singapore);

        // Act & Assert
        GeneralBadRequestException exception = assertThrows(
            GeneralBadRequestException.class,
            () -> landedCostService.calculateLandedCost(validRequest)
        );

        assertEquals("Importer and exporter cannot be the same country", exception.getMessage());
        verify(countryService, times(2)).getCountryByCode("702");
        verify(petroleumService, never()).getPetroleumByHsCode(anyString());
    }

    @Test
    void testCalculateLandedCost_InvalidHsCode_ThrowsException() {
        // Arrange
        when(countryService.getCountryByCode("702")).thenReturn(singapore);
        when(countryService.getCountryByCode("458")).thenReturn(malaysia);
        when(petroleumService.getPetroleumByHsCode("270900")).thenReturn(null);

        // Act & Assert
        PetroleumNotFoundException exception = assertThrows(
            PetroleumNotFoundException.class,
            () -> landedCostService.calculateLandedCost(validRequest)
        );

        assertEquals("Invalid HS code for petroleum: 270900", exception.getMessage());
        verify(petroleumService).getPetroleumByHsCode("270900");
        verify(routeOptimizationService, never()).optimizeRoutes(any());
    }

    @Test
    void testCalculateLandedCost_NoImporterSpecified_ThrowsException() {
        // Arrange
        validRequest.setImporterCode(null);
        validRequest.setImporterName(null);

        // Act & Assert
        GeneralBadRequestException exception = assertThrows(
            GeneralBadRequestException.class,
            () -> landedCostService.calculateLandedCost(validRequest)
        );

        assertEquals("Importer not specified", exception.getMessage());
    }

    @Test
    void testCalculateLandedCost_NoExporterSpecified_ThrowsException() {
        // Arrange
        validRequest.setExporterCode(null);
        validRequest.setExporterName(null);

        when(countryService.getCountryByCode("702")).thenReturn(singapore);

        // Act & Assert
        GeneralBadRequestException exception = assertThrows(
            GeneralBadRequestException.class,
            () -> landedCostService.calculateLandedCost(validRequest)
        );

        assertEquals("Exporter not specified", exception.getMessage());
        verify(countryService).getCountryByCode("702");
    }

    @Test
    void testCalculateLandedCost_TariffRateCalculation() {
        // Arrange
        RouteBreakdown directRoute = new RouteBreakdown(
            "Malaysia", null, "Singapore",
            200.0, 30.0, 9.0, 249.0, 9.0, "Crude Oil", 0.0
        );

        List<RouteBreakdown> topRoutes = Arrays.asList(directRoute);

        RouteOptimizationResponse routeResponse = new RouteOptimizationResponse(topRoutes, 50.0);

        when(countryService.getCountryByCode("702")).thenReturn(singapore);
        when(countryService.getCountryByCode("458")).thenReturn(malaysia);
        when(petroleumService.getPetroleumByHsCode("270900")).thenReturn(crudeOil);
        when(routeOptimizationService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(routeResponse);

        // Act
        LandedCostResponse response = landedCostService.calculateLandedCost(validRequest);

        // Assert
        // Tariff rate should be tariffFees / baseCost = 30 / 200 = 0.15 (15%)
        // Note: The service now returns percentage as 15.0 instead of 0.15
        assertEquals(15.0, response.getTariffRate(), 0.001);
        assertEquals(30.0, response.getTariffFees());
        assertEquals(200.0, response.getBasePrice());
    }

    @Test
    void testCalculateLandedCost_WithOnlyDirectRoute() {
        // Arrange
        RouteBreakdown directRoute = new RouteBreakdown(
            "Malaysia", null, "Singapore",
            100.0, 10.0, 9.0, 124.0, 9.0, "Crude Oil", 0.0
        );

        List<RouteBreakdown> topRoutes = Arrays.asList(directRoute); // Only direct route

        RouteOptimizationResponse routeResponse = new RouteOptimizationResponse(topRoutes, 50.0);

        when(countryService.getCountryByCode("702")).thenReturn(singapore);
        when(countryService.getCountryByCode("458")).thenReturn(malaysia);
        when(petroleumService.getPetroleumByHsCode("270900")).thenReturn(crudeOil);
        when(routeOptimizationService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(routeResponse);

        // Act
        LandedCostResponse response = landedCostService.calculateLandedCost(validRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getAlternativeRoutes());
        assertEquals(0, response.getAlternativeRoutes().size()); // No alternative routes
    }

    @Test
    void testCalculateLandedCost_VerifyRouteOptimizationRequest() {
        // Arrange
        RouteBreakdown directRoute = new RouteBreakdown(
            "Malaysia", null, "Singapore",
            100.0, 10.0, 9.0, 124.0, 9.0, "Crude Oil", 0.0
        );

        List<RouteBreakdown> topRoutes = Arrays.asList(directRoute);

        RouteOptimizationResponse routeResponse = new RouteOptimizationResponse(topRoutes, 50.0);

        when(countryService.getCountryByCode("702")).thenReturn(singapore);
        when(countryService.getCountryByCode("458")).thenReturn(malaysia);
        when(petroleumService.getPetroleumByHsCode("270900")).thenReturn(crudeOil);
        when(routeOptimizationService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(routeResponse);

        // Act
        landedCostService.calculateLandedCost(validRequest);

        // Assert - Verify RouteOptimizationRequest is built correctly
        verify(routeOptimizationService).optimizeRoutes(argThat(request -> 
            request.getImportingCountry().equals(singapore) &&
            request.getExportingCountry().equals(malaysia) &&
            request.getHsCode().equals("270900") &&
            request.getUnits() == 1000
        ));
    }

    @Test
    void testCalculateLandedCost_AlternativeRoutesMapping() {
        // Arrange
        RouteBreakdown directRoute = new RouteBreakdown(
            "Malaysia", null, "Singapore",
            100.0, 10.0, 9.0, 124.0, 9.0, "Crude Oil", 0.0
        );

        RouteBreakdown transitRoute1 = new RouteBreakdown(
            "Malaysia", "China", "Singapore",
            100.0, 12.0, 13.0, 131.0, 9.0, "Crude Oil", 0.0
        );

        RouteBreakdown transitRoute2 = new RouteBreakdown(
            "Malaysia", "Thailand", "Singapore",
            100.0, 11.0, 7.0, 123.5, 9.0, "Crude Oil", 0.0
        );

        RouteBreakdown transitRoute3 = new RouteBreakdown(
            "Malaysia", "Vietnam", "Singapore",
            100.0, 13.0, 10.0, 129.5, 9.0, "Crude Oil", 0.0
        );

        List<RouteBreakdown> topRoutes = Arrays.asList(directRoute, transitRoute1, transitRoute2, transitRoute3);

        RouteOptimizationResponse routeResponse = new RouteOptimizationResponse(topRoutes, 50.0);

        when(countryService.getCountryByCode("702")).thenReturn(singapore);
        when(countryService.getCountryByCode("458")).thenReturn(malaysia);
        when(petroleumService.getPetroleumByHsCode("270900")).thenReturn(crudeOil);
        when(routeOptimizationService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(routeResponse);

        // Act
        LandedCostResponse response = landedCostService.calculateLandedCost(validRequest);

        // Assert
        Map<String, RouteBreakdown> alternatives = response.getAlternativeRoutes();
        assertEquals(3, alternatives.size());
        
        // Verify each alternative route is mapped correctly
        assertTrue(alternatives.containsKey("China"));
        RouteBreakdown chinaRoute = alternatives.get("China");
        assertEquals(131.0, chinaRoute.getTotalLandedCost());
        
        assertTrue(alternatives.containsKey("Thailand"));
        RouteBreakdown thailandRoute = alternatives.get("Thailand");
        assertEquals(123.5, thailandRoute.getTotalLandedCost());
        
        assertTrue(alternatives.containsKey("Vietnam"));
        RouteBreakdown vietnamRoute = alternatives.get("Vietnam");
        assertEquals(129.5, vietnamRoute.getTotalLandedCost());
    }
}

