package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.RouteOptimizationRequest;
import com.cs203.grp2.Asg2.DTO.RouteOptimizationResponse;
import com.cs203.grp2.Asg2.models.Country;
import com.cs203.grp2.Asg2.service.CountryService;
import com.cs203.grp2.Asg2.service.RouteOptimizeService;
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
class RouteOptimizationControllerTest {

    @Mock
    private RouteOptimizeService routeOptimizeService;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private RouteOptimizationController routeOptimizationController;

    @Test
    void testCalculateRoutes_WithValidRequest_ShouldReturnResponse() {
        // Arrange
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        RouteOptimizationResponse mockResponse = mock(RouteOptimizationResponse.class);
        when(routeOptimizeService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(mockResponse);

        // Act
        RouteOptimizationResponse result = routeOptimizationController.calculateRoutes(request);

        // Assert
        assertNotNull(result);
        verify(routeOptimizeService).optimizeRoutes(any(RouteOptimizationRequest.class));
    }

    @Test
    void testCalculateRoutesViaGet_WithNumericIdentifiers_ShouldCallGetCountryByCode() {
        // Arrange
        Country importer = new Country();
        importer.setCode("840");
        Country exporter = new Country();
        exporter.setCode("156");

        when(countryService.getCountryByCode("840")).thenReturn(importer);
        when(countryService.getCountryByCode("156")).thenReturn(exporter);
        when(routeOptimizeService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(mock(RouteOptimizationResponse.class));

        // Act
        RouteOptimizationResponse result = routeOptimizationController.calculateRoutesViaGet(
            "840", "156", "271012", 1000, LocalDate.of(2025, 10, 14)
        );

        // Assert
        assertNotNull(result);
        verify(countryService).getCountryByCode("840");
        verify(countryService).getCountryByCode("156");
        verify(routeOptimizeService).optimizeRoutes(any(RouteOptimizationRequest.class));
    }

    @Test
    void testCalculateRoutesViaGet_WithCountryNames_ShouldCallGetCountryByName() {
        // Arrange
        Country importer = new Country();
        importer.setName("USA");
        Country exporter = new Country();
        exporter.setName("China");

        when(countryService.getCountryByName("USA")).thenReturn(importer);
        when(countryService.getCountryByName("China")).thenReturn(exporter);
        when(routeOptimizeService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(mock(RouteOptimizationResponse.class));

        // Act
        RouteOptimizationResponse result = routeOptimizationController.calculateRoutesViaGet(
            "USA", "China", "271012", 1000, LocalDate.of(2025, 10, 14)
        );

        // Assert
        assertNotNull(result);
        verify(countryService).getCountryByName("USA");
        verify(countryService).getCountryByName("China");
        verify(routeOptimizeService).optimizeRoutes(any(RouteOptimizationRequest.class));
    }

    @Test
    void testCalculateRoutesViaGet_WithMixedIdentifiers_ShouldHandleCorrectly() {
        // Arrange
        Country importer = new Country();
        Country exporter = new Country();

        when(countryService.getCountryByCode("840")).thenReturn(importer);
        when(countryService.getCountryByName("China")).thenReturn(exporter);
        when(routeOptimizeService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(mock(RouteOptimizationResponse.class));

        // Act
        RouteOptimizationResponse result = routeOptimizationController.calculateRoutesViaGet(
            "840", "China", "271012", 1000, LocalDate.of(2025, 10, 14)
        );

        // Assert
        assertNotNull(result);
        verify(countryService).getCountryByCode("840");
        verify(countryService).getCountryByName("China");
    }

    @Test
    void testCalculateRoutesViaGet_WithAllParameters_ShouldBuildRequestCorrectly() {
        // Arrange
        Country importer = new Country();
        Country exporter = new Country();
        
        when(countryService.getCountryByCode(anyString())).thenReturn(importer);
        when(countryService.getCountryByCode(anyString())).thenReturn(exporter);
        when(routeOptimizeService.optimizeRoutes(any(RouteOptimizationRequest.class)))
            .thenReturn(mock(RouteOptimizationResponse.class));

        LocalDate testDate = LocalDate.of(2025, 11, 5);

        // Act
        RouteOptimizationResponse result = routeOptimizationController.calculateRoutesViaGet(
            "702", "840", "270900", 5000, testDate
        );

        // Assert
        assertNotNull(result);
        verify(routeOptimizeService).optimizeRoutes(any(RouteOptimizationRequest.class));
    }
}
