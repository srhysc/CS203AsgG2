package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.models.Country;
import com.cs203.grp2.Asg2.DTO.RouteOptimizationRequest;
import com.cs203.grp2.Asg2.DTO.RouteOptimizationResponse;
import com.cs203.grp2.Asg2.DTO.RouteBreakdown;
import com.cs203.grp2.Asg2.service.RouteOptimizeService;
import com.cs203.grp2.Asg2.service.CountryService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for RouteOptimizeService.
 * Tests route optimization logic with real Firebase data.
 */
@SpringBootTest
@ActiveProfiles("integration-test")
public class RouteOptimizeServiceIntegrationTest extends BaseFirebaseIntegrationTest {

    @Autowired
    private RouteOptimizeService routeOptimizeService;
    
    @Autowired
    private CountryService countryService;
    
    private RouteOptimizationRequest createRequest(String exporterCode, String importerCode, String hsCode, int units, LocalDate date) {
        Country exporter = countryService.getCountryByCode(exporterCode);
        Country importer = countryService.getCountryByCode(importerCode);
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setExporter(exporter);
        request.setImporter(importer);
        request.setHsCode(hsCode);
        request.setUnits(units);
        request.setCalculationDate(date);
        return request;
    }

    @Test
    @Disabled("Requires HS code 270900 (Crude Oil) to be present in Firebase test database")
    void testOptimizeRoutes_DirectRoute() {
        RouteOptimizationRequest request = createRequest("702", "458", "270900", 1000, LocalDate.now());
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        assertNotNull(response);
        assertTrue(response.getTopRoutes().get(0).getTotalLandedCost() > 0);
    }

    @Test
    @Disabled("Requires HS code 270900 (Crude Oil) to be present in Firebase test database")
    void testOptimizeRoutes_FindsTransitRoutes() {
        RouteOptimizationRequest request = createRequest("840", "156", "270900", 5000, LocalDate.now());
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        List<RouteBreakdown> routes = response.getTopRoutes();
        assertTrue(routes.size() > 1);
    }

    @Test
    void testOptimizeRoutes_SameCountryThrowsException() {
        RouteOptimizationRequest request = createRequest("392", "392", "270900", 1000, LocalDate.now());
        // The service now throws GeneralBadRequestException instead of IllegalArgumentException
        assertThrows(com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException.class, 
            () -> routeOptimizeService.optimizeRoutes(request));
    }

    @Test
    void testOptimizeRoutes_InvalidHsCodeThrowsException() {
        RouteOptimizationRequest request = createRequest("702", "458", "999999", 1000, LocalDate.now());
        assertThrows(RuntimeException.class, () -> routeOptimizeService.optimizeRoutes(request));
    }

    @Test
    @Disabled("Requires HS code 270900 (Crude Oil) to be present in Firebase test database")
    void testOptimizeRoutes_CalculatesCostsCorrectly() {
        RouteOptimizationRequest request = createRequest("036", "356", "270900", 3000, LocalDate.now());
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        RouteBreakdown firstRoute = response.getTopRoutes().get(0);
        double expectedTotal = firstRoute.getBaseCost() + firstRoute.getTariffFees() + firstRoute.getVatFees();
        assertEquals(expectedTotal, firstRoute.getTotalLandedCost(), 0.01);
    }

    @Test
    @Disabled("Requires HS codes (270900, 271000, 271121) to be present in Firebase test database")
    void testOptimizeRoutes_DifferentPetroleumTypes() {
        String[] hsCodes = {"270900", "271000", "271121"};
        for (String hsCode : hsCodes) {
            RouteOptimizationRequest request = createRequest("682", "410", hsCode, 2000, LocalDate.now());
            RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
            assertNotNull(response);
        }
    }

    @Test
    @Disabled("Requires HS code 270900 (Crude Oil) to be present in Firebase test database")
    void testOptimizeRoutes_SortedByCost() {
        RouteOptimizationRequest request = createRequest("076", "392", "270900", 4000, LocalDate.now());
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        List<RouteBreakdown> routes = response.getTopRoutes();
        for (int i = 0; i < routes.size() - 1; i++) {
            assertTrue(routes.get(i).getTotalLandedCost() <= routes.get(i + 1).getTotalLandedCost());
        }
    }

    @Test
    @Disabled("Requires HS code 270900 (Crude Oil) to be present in Firebase test database")
    void testOptimizeRoutes_LimitsToSixRoutes() {
        RouteOptimizationRequest request = createRequest("643", "380", "270900", 3500, LocalDate.now());
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        assertTrue(response.getTopRoutes().size() <= 6);
    }
}
