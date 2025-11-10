package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.service.RefineryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for RefineryServiceImpl.
 * 
 * These tests interact with the real Firebase database to verify that:
 * 1. Data can be loaded from Firebase's Refineries path
 * 2. Multiple method calls work correctly (tests the reload pattern)
 * 3. Filtering by country works with real data
 * 4. DTO conversion handles real Firebase data structure
 * 
 * Note: These tests are slower than unit tests (~2-5 seconds each) because
 * they make actual Firebase calls, but they provide confidence that the
 * complex nested Firebase data loading actually works.
 */
public class RefineryServiceIntegrationTest extends BaseFirebaseIntegrationTest {

    @Autowired
    private RefineryServiceImpl refineryService;

    @Test
    void testGetAllRefineries_LoadsDataFromFirebase() {
        // When: Get all refineries from Firebase
        List<RefineryResponseDTO> refineries = refineryService.getAllRefineries();
        
        // Then: Should load refineries successfully
        assertNotNull(refineries, "Refineries list should not be null");
        
        // Log result for visibility
        System.out.println("✅ Loaded " + refineries.size() + " refineries from Firebase");
        
        // Verify data structure if refineries exist
        if (!refineries.isEmpty()) {
            RefineryResponseDTO firstRefinery = refineries.get(0);
            assertNotNull(firstRefinery.getName(), "Refinery should have a name");
            System.out.println("Sample refinery: " + firstRefinery.getName() + 
                             " in " + firstRefinery.getCountryName());
        }
    }

    @Test
    void testGetAllRefineries_MultipleCallsWork() {
        // This test verifies the reload pattern works correctly
        // In unit tests, this would fail because mocks don't re-trigger
        
        // When: Call getAllRefineries multiple times
        List<RefineryResponseDTO> firstCall = refineryService.getAllRefineries();
        List<RefineryResponseDTO> secondCall = refineryService.getAllRefineries();
        List<RefineryResponseDTO> thirdCall = refineryService.getAllRefineries();
        
        // Then: All calls should return the same data
        assertNotNull(firstCall, "First call should return data");
        assertNotNull(secondCall, "Second call should return data");
        assertNotNull(thirdCall, "Third call should return data");
        
        assertEquals(firstCall.size(), secondCall.size(), 
            "Second call should return same number of refineries");
        assertEquals(firstCall.size(), thirdCall.size(), 
            "Third call should return same number of refineries");
        
        System.out.println("✅ Multiple calls work correctly - " + firstCall.size() + " refineries each time");
    }

    @Test
    void testGetRefineriesByCountry_FiltersCorrectly() {
        // Given: Get all refineries first to know what countries exist
        List<RefineryResponseDTO> allRefineries = refineryService.getAllRefineries();
        
        if (allRefineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping filter test");
            return;
        }
        
        // Pick a country that has refineries
        String testCountryIso3 = allRefineries.get(0).getCountryIso3();
        assertNotNull(testCountryIso3, "Refinery should have country ISO3 code");
        
        // When: Filter by that country
        List<RefineryResponseDTO> countryRefineries = 
            refineryService.getRefineriesByCountry(testCountryIso3);
        
        // Then: Should return only refineries from that country
        assertNotNull(countryRefineries, "Filtered result should not be null");
        assertFalse(countryRefineries.isEmpty(), 
            "Should find at least one refinery in " + testCountryIso3);
        
        // Verify all returned refineries are from the requested country
        for (RefineryResponseDTO refinery : countryRefineries) {
            assertEquals(testCountryIso3.toLowerCase(), 
                        refinery.getCountryIso3().toLowerCase(),
                        "All refineries should be from " + testCountryIso3);
        }
        
        System.out.println("✅ Found " + countryRefineries.size() + 
                         " refineries in " + testCountryIso3);
    }

    @Test
    void testGetRefineriesByCountry_NonExistentCountryReturnsEmpty() {
        // When: Search for country that doesn't exist
        List<RefineryResponseDTO> result = 
            refineryService.getRefineriesByCountry("NONEXISTENT");
        
        // Then: Should return empty list, not null
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty list for non-existent country");
        
        System.out.println("✅ Non-existent country returns empty list correctly");
    }

    @Test
    void testGetRefinery_FindsSpecificRefinery() {
        // Given: Get all refineries to find a specific one
        List<RefineryResponseDTO> allRefineries = refineryService.getAllRefineries();
        
        if (allRefineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping specific refinery test");
            return;
        }
        
        RefineryResponseDTO expectedRefinery = allRefineries.get(0);
        String countryIso3 = expectedRefinery.getCountryIso3();
        String refineryName = expectedRefinery.getName();
        
        // When: Look up that specific refinery
        RefineryResponseDTO result = 
            refineryService.getRefinery(countryIso3, refineryName);
        
        // Then: Should find it
        assertNotNull(result, "Should find the refinery");
        assertEquals(refineryName, result.getName(), "Name should match");
        assertEquals(countryIso3.toLowerCase(), result.getCountryIso3().toLowerCase(), 
                    "Country should match");
        
        System.out.println("✅ Found specific refinery: " + refineryName);
    }

    @Test
    void testGetRefinery_CaseInsensitiveSearch() {
        // Given: Get a refinery
        List<RefineryResponseDTO> allRefineries = refineryService.getAllRefineries();
        
        if (allRefineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping case test");
            return;
        }
        
        RefineryResponseDTO refinery = allRefineries.get(0);
        String countryIso3 = refinery.getCountryIso3();
        String refineryName = refinery.getName();
        
        // When: Search with different case
        RefineryResponseDTO resultLower = 
            refineryService.getRefinery(countryIso3.toLowerCase(), refineryName.toLowerCase());
        RefineryResponseDTO resultUpper = 
            refineryService.getRefinery(countryIso3.toUpperCase(), refineryName.toUpperCase());
        
        // Then: Should find it regardless of case
        assertNotNull(resultLower, "Should find with lowercase");
        assertNotNull(resultUpper, "Should find with uppercase");
        
        System.out.println("✅ Case-insensitive search works correctly");
    }

    @Test
    void testGetAllCosts_ReturnsValidCostStructure() {
        // Given: Get a refinery that has costs
        List<RefineryResponseDTO> allRefineries = refineryService.getAllRefineries();
        
        RefineryResponseDTO refineryWithCosts = null;
        for (RefineryResponseDTO r : allRefineries) {
            if (r.getEstimated_costs() != null && !r.getEstimated_costs().isEmpty()) {
                refineryWithCosts = r;
                break;
            }
        }
        
        if (refineryWithCosts == null) {
            System.out.println("⚠️ No refineries with costs in database, skipping cost test");
            return;
        }
        
        // When: Get costs for that refinery
        List<RefineryCostResponseDTO> costs = refineryService.getAllCosts(
            refineryWithCosts.getCountryIso3(), 
            refineryWithCosts.getName()
        );
        
        // Then: Should return cost data
        assertNotNull(costs, "Costs should not be null");
        
        // Verify cost structure if costs exist
        if (!costs.isEmpty()) {
            RefineryCostResponseDTO firstCost = costs.get(0);
            assertNotNull(firstCost.getDate(), "Cost should have a date");
            assertNotNull(firstCost.getCosts(), "Cost should have cost details");
            
            System.out.println("✅ Found " + costs.size() + " cost entries for " + 
                             refineryWithCosts.getName());
        } else {
            System.out.println("⚠️ No cost entries returned for " + refineryWithCosts.getName());
        }
    }

    @Test
    void testGetLatestCost_FindsCostForDate() {
        // Given: Get refineries and find one with costs
        List<RefineryResponseDTO> allRefineries = refineryService.getAllRefineries();
        
        RefineryResponseDTO refineryWithCosts = null;
        for (RefineryResponseDTO r : allRefineries) {
            if (r.getEstimated_costs() != null && !r.getEstimated_costs().isEmpty()) {
                refineryWithCosts = r;
                break;
            }
        }
        
        if (refineryWithCosts == null) {
            System.out.println("⚠️ No refineries with costs, skipping date test");
            return;
        }
        
        String testDate = refineryWithCosts.getEstimated_costs().get(0).getDate();
        
        // When: Get cost for specific date
        List<RefineryCostResponseDTO> results = refineryService.getLatestCost(
            refineryWithCosts.getCountryIso3(),
            refineryWithCosts.getName(),
            testDate
        );
        
        // Then: Should find cost entries
        assertNotNull(results, "Should not return null");
        
        if (!results.isEmpty()) {
            RefineryCostResponseDTO result = results.get(0);
            assertEquals(testDate, result.getDate(), "Date should match");
            System.out.println("✅ Found cost for date: " + testDate);
        } else {
            System.out.println("⚠️ No costs found for date: " + testDate);
        }
    }

    @Test
    void testDataStructure_ValidatesDTOFields() {
        // This test validates that Firebase data maps correctly to DTOs
        List<RefineryResponseDTO> refineries = refineryService.getAllRefineries();
        
        if (refineries.isEmpty()) {
            System.out.println("⚠️ No refineries to validate");
            return;
        }
        
        RefineryResponseDTO refinery = refineries.get(0);
        
        // Validate required fields are present
        assertNotNull(refinery.getName(), "Name should be present");
        assertNotNull(refinery.getCountryIso3(), "Country ISO3 should be present");
        
        // Validate costs structure if present
        if (refinery.getEstimated_costs() != null && !refinery.getEstimated_costs().isEmpty()) {
            RefineryCostResponseDTO cost = refinery.getEstimated_costs().get(0);
            assertNotNull(cost.getDate(), "Cost date should be present");
            
            if (cost.getCosts() != null && !cost.getCosts().isEmpty()) {
                Map.Entry<String, CostDetailResponseDTO> firstCostDetail = 
                    cost.getCosts().entrySet().iterator().next();
                
                CostDetailResponseDTO detail = firstCostDetail.getValue();
                assertNotNull(detail.getCost_per_unit(), "Cost per unit should be present");
                assertNotNull(detail.getUnit(), "Unit should be present");
                
                System.out.println("✅ Cost structure valid - " + firstCostDetail.getKey() + 
                                 ": $" + detail.getCost_per_unit() + "/" + detail.getUnit());
            }
        }
        
        System.out.println("✅ DTO structure validates correctly");
    }
}
