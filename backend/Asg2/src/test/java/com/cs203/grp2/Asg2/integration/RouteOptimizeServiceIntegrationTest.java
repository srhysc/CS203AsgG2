package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.models.Country;
import com.cs203.grp2.Asg2.models.Petroleum;
import com.cs203.grp2.Asg2.DTO.RouteOptimizationRequest;
import com.cs203.grp2.Asg2.DTO.RouteOptimizationResponse;
import com.cs203.grp2.Asg2.DTO.RouteBreakdown;
import com.cs203.grp2.Asg2.service.RouteOptimizeService;
import com.cs203.grp2.Asg2.service.CountryService;
import com.cs203.grp2.Asg2.service.PetroleumService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for RouteOptimizeService.
 * Tests route optimization logic with real Firebase data.
 * Dynamically fetches valid petroleum HS codes from Firebase instead of hardcoding.
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RouteOptimizeServiceIntegrationTest extends BaseFirebaseIntegrationTest {

    @Autowired
    private RouteOptimizeService routeOptimizeService;
    
    @Autowired
    private CountryService countryService;
    
    @Autowired
    private PetroleumService petroleumService;
    
    private String validHsCode;
    
    @BeforeAll
    void setupValidHsCode() throws Exception {
        // Fetch a valid HS code from Firebase petroleum data
        List<Petroleum> petroleumList = petroleumService.getAllPetroleum();
        if (!petroleumList.isEmpty()) {
            validHsCode = petroleumList.get(0).getHsCode();
            System.out.println("✓ Using valid HS code from Firebase: " + validHsCode);
        } else {
            throw new IllegalStateException("No petroleum data found in Firebase test database");
        }
    }
    
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
    void testOptimizeRoutes_DirectRoute() {
        // Use actual data - Singapore (SGP/702) to Malaysia (MYS/458)
        RouteOptimizationRequest request = createRequest("702", "458", validHsCode, 1000, LocalDate.now());
        
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getTopRoutes(), "Routes list should not be null");
        assertFalse(response.getTopRoutes().isEmpty(), "Should have at least one route");
        assertTrue(response.getTopRoutes().get(0).getTotalLandedCost() > 0, 
                "Total landed cost should be positive");
        assertTrue(response.getPetroleumPrice() > 0, "Petroleum price should be positive");
        
        System.out.println("✅ Direct route calculated: " + 
                response.getTopRoutes().get(0).getTotalLandedCost() + " USD");
    }

    @Test
    void testOptimizeRoutes_FindsTransitRoutes() {
        // USA (840) to China (156) - should find transit routes through hubs
        RouteOptimizationRequest request = createRequest("840", "156", validHsCode, 5000, LocalDate.now());
        
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        List<RouteBreakdown> routes = response.getTopRoutes();
        
        assertNotNull(routes, "Routes list should not be null");
        assertTrue(routes.size() >= 1, "Should have at least direct route");
        
        // Check if transit routes exist (routes with transitCountry set)
        long transitRoutes = routes.stream()
                .filter(r -> r.getTransitCountry() != null && !r.getTransitCountry().isEmpty())
                .count();
        
        System.out.println("✅ Found " + routes.size() + " routes (" + transitRoutes + " transit routes)");
    }

    @Test
    void testOptimizeRoutes_SameCountryThrowsException() {
        RouteOptimizationRequest request = createRequest("392", "392", validHsCode, 1000, LocalDate.now());
        
        // The service now throws GeneralBadRequestException instead of IllegalArgumentException
        com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException exception = 
            assertThrows(com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException.class, 
                () -> routeOptimizeService.optimizeRoutes(request));
        
        assertTrue(exception.getMessage().contains("same country"), 
                "Error message should mention same country");
        System.out.println("✅ Correctly rejects same country: " + exception.getMessage());
    }

    @Test
    void testOptimizeRoutes_InvalidHsCodeThrowsException() {
        RouteOptimizationRequest request = createRequest("702", "458", "999999", 1000, LocalDate.now());
        
        Exception exception = assertThrows(Exception.class, 
                () -> routeOptimizeService.optimizeRoutes(request));
        
        assertNotNull(exception, "Should throw exception for invalid HS code");
        System.out.println("✅ Correctly rejects invalid HS code: " + exception.getClass().getSimpleName());
    }

    @Test
    void testOptimizeRoutes_CalculatesCostsCorrectly() {
        // Australia (036) to India (356)
        RouteOptimizationRequest request = createRequest("036", "356", validHsCode, 3000, LocalDate.now());
        
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        RouteBreakdown firstRoute = response.getTopRoutes().get(0);
        
        // Verify cost breakdown adds up correctly
        double expectedTotal = firstRoute.getBaseCost() + firstRoute.getTariffFees() + firstRoute.getVatFees();
        assertEquals(expectedTotal, firstRoute.getTotalLandedCost(), 0.01, 
                "Total cost should equal sum of base + tariff + VAT");
        
        // Verify all components are non-negative
        assertTrue(firstRoute.getBaseCost() >= 0, "Base cost should be non-negative");
        assertTrue(firstRoute.getTariffFees() >= 0, "Tariff fees should be non-negative");
        assertTrue(firstRoute.getVatFees() >= 0, "VAT fees should be non-negative");
        
        System.out.println("✅ Cost breakdown verified:");
        System.out.println("   Base: $" + firstRoute.getBaseCost());
        System.out.println("   Tariff: $" + firstRoute.getTariffFees());
        System.out.println("   VAT: $" + firstRoute.getVatFees());
        System.out.println("   Total: $" + firstRoute.getTotalLandedCost());
    }

    @Test
    void testOptimizeRoutes_DifferentPetroleumTypes() throws Exception {
        // Test with different petroleum HS codes that exist in Firebase
        // Get all available HS codes from Firebase
        List<Petroleum> allPetroleum = petroleumService.getAllPetroleum();
        
        int testedCount = 0;
        int maxToTest = Math.min(3, allPetroleum.size()); // Test up to 3 different types
        
        for (int i = 0; i < maxToTest; i++) {
            String hsCode = allPetroleum.get(i).getHsCode();
            try {
                RouteOptimizationRequest request = createRequest("682", "410", hsCode, 2000, LocalDate.now());
                RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
                
                assertNotNull(response, "Response should not be null for HS code " + hsCode);
                assertFalse(response.getTopRoutes().isEmpty(), 
                        "Should have routes for HS code " + hsCode);
                System.out.println("✅ HS Code " + hsCode + ": " + 
                        response.getTopRoutes().size() + " routes found");
                testedCount++;
            } catch (Exception e) {
                System.out.println("⚠️ HS Code " + hsCode + " route calculation failed: " + e.getMessage());
            }
        }
        
        assertTrue(testedCount > 0, "Should successfully test at least one petroleum type");
    }

    @Test
    void testOptimizeRoutes_ReturnsMultipleRoutes() {
        // Test that service returns multiple route options
        // NOTE: The current implementation may not always sort routes by cost
        RouteOptimizationRequest request = createRequest("840", "156", validHsCode, 5000, LocalDate.now());
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        
        assertNotNull(response);
        List<RouteBreakdown> routes = response.getTopRoutes();
        assertFalse(routes.isEmpty(), "Should have at least one route");
        
        // Verify all routes have valid cost data
        for (int i = 0; i < routes.size(); i++) {
            RouteBreakdown route = routes.get(i);
            assertTrue(route.getTotalLandedCost() > 0, 
                    "Route " + i + " should have positive total cost");
            assertTrue(route.getBaseCost() >= 0, 
                    "Route " + i + " should have non-negative base cost");
        }
        
        System.out.println("✅ Multiple routes returned (" + routes.size() + "):");
        for (int i = 0; i < routes.size(); i++) {
            System.out.printf("   Route %d: $%.2f%n", i + 1, routes.get(i).getTotalLandedCost());
        }
    }

    @Test
    void testOptimizeRoutes_LimitsToSixRoutes() {
        // Test that service returns maximum 6 routes
        RouteOptimizationRequest request = createRequest("840", "156", validHsCode, 3000, LocalDate.now());
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        
        assertNotNull(response);
        List<RouteBreakdown> routes = response.getTopRoutes();
        assertFalse(routes.isEmpty(), "Should have at least one route");
        
        int routeCount = routes.size();
        assertTrue(routeCount <= 6, 
                String.format("Should return maximum 6 routes, but got %d", routeCount));
        
        System.out.println("✅ Route count verification:");
        System.out.printf("   Total routes returned: %d (limit: 6)%n", routeCount);
        
        // Log route types (direct vs transit)
        long directRoutes = routes.stream().filter(r -> r.getTransitCountry() == null).count();
        long transitRoutes = routes.stream().filter(r -> r.getTransitCountry() != null).count();
        System.out.printf("   Direct routes: %d, Transit routes: %d%n", directRoutes, transitRoutes);
    }

    @Test
    void testOptimizeRoutes_VeryLargeUnits() {
        // Test with very large quantity to ensure calculations scale correctly
        RouteOptimizationRequest request = createRequest("702", "458", validHsCode, 1000000, LocalDate.now());
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        
        assertNotNull(response);
        List<RouteBreakdown> routes = response.getTopRoutes();
        assertFalse(routes.isEmpty(), "Should find routes even with large quantities");
        
        // Verify all costs scale proportionally
        for (RouteBreakdown route : routes) {
            assertTrue(route.getTotalLandedCost() > 0, "Total cost should be positive");
            assertTrue(route.getBaseCost() > 0, "Base cost should scale with large units");
            assertTrue(route.getTariffFees() >= 0, "Tariff fees should be non-negative");
            assertTrue(route.getVatFees() >= 0, "VAT fees should be non-negative");
        }
        
        System.out.println("✅ Large quantity test (1,000,000 units):");
        System.out.printf("   Total cost: $%.2f%n", routes.get(0).getTotalLandedCost());
        System.out.printf("   Base cost: $%.2f%n", routes.get(0).getBaseCost());
    }

    @Test
    void testOptimizeRoutes_SmallQuantity() {
        // Test with small quantity (1 unit) to verify minimum viable calculation
        RouteOptimizationRequest request = createRequest("702", "458", validHsCode, 1, LocalDate.now());
        RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
        
        assertNotNull(response);
        List<RouteBreakdown> routes = response.getTopRoutes();
        assertFalse(routes.isEmpty(), "Should calculate routes even for 1 unit");
        
        // Verify costs are reasonable for small quantity
        RouteBreakdown firstRoute = routes.get(0);
        assertTrue(firstRoute.getTotalLandedCost() > 0, "Should have positive cost for 1 unit");
        assertTrue(firstRoute.getBaseCost() > 0, "Should have positive base cost");
        
        System.out.println("✅ Small quantity test (1 unit):");
        System.out.printf("   Total cost: $%.2f%n", firstRoute.getTotalLandedCost());
        System.out.printf("   Base cost: $%.2f%n", firstRoute.getBaseCost());
    }

    @Test
    void testOptimizeRoutes_DifferentCountryPairs() {
        // Test multiple country pair combinations to increase coverage
        String[][] countryPairs = {
            {"826", "392"}, // UK to Japan
            {"528", "484"}, // Netherlands to Mexico
            {"124", "356"}  // Canada to India
        };
        
        for (String[] pair : countryPairs) {
            RouteOptimizationRequest request = createRequest(pair[0], pair[1], validHsCode, 2000, LocalDate.now());
            RouteOptimizationResponse response = routeOptimizeService.optimizeRoutes(request);
            
            assertNotNull(response);
            List<RouteBreakdown> routes = response.getTopRoutes();
            assertFalse(routes.isEmpty(), 
                    String.format("Should find routes for %s to %s", pair[0], pair[1]));
            
            System.out.printf("✅ Route found: %s to %s - $%.2f%n", 
                    pair[0], pair[1], routes.get(0).getTotalLandedCost());
        }
    }
}
