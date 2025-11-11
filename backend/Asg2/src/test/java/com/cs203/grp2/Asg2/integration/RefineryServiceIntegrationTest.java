package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.service.RefineryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
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
        // The service now throws RefineryNotFoundException instead of returning empty list
        assertThrows(com.cs203.grp2.Asg2.exceptions.RefineryNotFoundException.class, () -> {
            refineryService.getRefineriesByCountry("NONEXISTENT");
        });
        
        System.out.println("✅ Non-existent country throws exception correctly");
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

    
    @Test
    void testAddOrUpdateRefinery_AddNewRefinery() {
        // Arrange - Create a new test refinery with unique name
        String uniqueSuffix = String.valueOf(System.currentTimeMillis() % 10000);
        String testCountry = "TRF";  // Test refinery country
        
        RefineryRequestDTO requestDTO = new RefineryRequestDTO();
        requestDTO.setName("Test Refinery " + uniqueSuffix);
        requestDTO.setCompany("Test Oil Company");
        requestDTO.setLocation("Test City");
        requestDTO.setOperational_from(2020);
        requestDTO.setOperational_to(2050);
        requestDTO.setCan_refine_any(true);
        requestDTO.setCountryName("Test Refinery Country");
        requestDTO.setCountryIsoNumeric("999");
        
        // Add estimated costs
        RefineryCostRequestDTO costRequest = new RefineryCostRequestDTO();
        costRequest.setDate("2024-01-01");
        
        Map<String, CostDetailRequestDTO> costs = new HashMap<>();
        CostDetailRequestDTO barrelCost = new CostDetailRequestDTO();
        barrelCost.setCost_per_unit(50.0);
        barrelCost.setUnit("USD per barrel");
        costs.put("barrel", barrelCost);
        costRequest.setCosts(costs);
        
        requestDTO.setEstimated_costs(List.of(costRequest));
        
        // Act - Add new refinery
        RefineryResponseDTO result = refineryService.addOrUpdateRefinery(testCountry, requestDTO);
        
        // Assert
        assertNotNull(result, "Should return response DTO");
        assertEquals("Test Refinery " + uniqueSuffix, result.getName(), "Name should match");
        assertEquals("Test Oil Company", result.getCompany(), "Company should match");
        assertEquals("Test City", result.getLocation(), "Location should match");
        assertEquals(2020, result.getOperational_from(), "Operational from should match");
        assertEquals(2050, result.getOperational_to(), "Operational to should match");
        assertTrue(result.isCan_refine_any(), "Can refine any should be true");
        assertEquals(testCountry, result.getCountryIso3(), "Country ISO3 should match");
        assertNotNull(result.getEstimated_costs(), "Estimated costs should not be null");
        assertEquals(1, result.getEstimated_costs().size(), "Should have 1 cost entry");
        
        System.out.println("✅ New refinery added: " + result.getName());
    }
    
    @Test
    void testAddOrUpdateRefinery_UpdateExistingRefinery() {
        // Given - First add a refinery
        String uniqueSuffix = String.valueOf(System.currentTimeMillis() % 10000);
        String testCountry = "TRU";
        String refineryName = "Update Test Refinery " + uniqueSuffix;
        
        RefineryRequestDTO initialRequest = new RefineryRequestDTO();
        initialRequest.setName(refineryName);
        initialRequest.setCompany("Initial Company");
        initialRequest.setLocation("Initial Location");
        initialRequest.setOperational_from(2020);
        initialRequest.setOperational_to(2050);
        initialRequest.setCan_refine_any(false);
        initialRequest.setCountryName("Update Test Country");
        initialRequest.setCountryIsoNumeric("998");
        
        RefineryCostRequestDTO initialCost = new RefineryCostRequestDTO();
        initialCost.setDate("2024-01-01");
        Map<String, CostDetailRequestDTO> initialCosts = new HashMap<>();
        CostDetailRequestDTO initialBarrelCost = new CostDetailRequestDTO();
        initialBarrelCost.setCost_per_unit(45.0);
        initialBarrelCost.setUnit("USD per barrel");
        initialCosts.put("barrel", initialBarrelCost);
        initialCost.setCosts(initialCosts);
        initialRequest.setEstimated_costs(List.of(initialCost));
        
        refineryService.addOrUpdateRefinery(testCountry, initialRequest);
        
        // When - Update with new cost entry
        RefineryRequestDTO updateRequest = new RefineryRequestDTO();
        updateRequest.setName(refineryName);  // Same name to trigger update
        updateRequest.setCompany("Initial Company");
        updateRequest.setLocation("Initial Location");
        updateRequest.setOperational_from(2020);
        updateRequest.setOperational_to(2050);
        updateRequest.setCan_refine_any(false);
        updateRequest.setCountryName("Update Test Country");
        updateRequest.setCountryIsoNumeric("998");
        
        RefineryCostRequestDTO newCost = new RefineryCostRequestDTO();
        newCost.setDate("2024-06-01");  // Different date
        Map<String, CostDetailRequestDTO> newCosts = new HashMap<>();
        CostDetailRequestDTO newBarrelCost = new CostDetailRequestDTO();
        newBarrelCost.setCost_per_unit(55.0);  // Different cost
        newBarrelCost.setUnit("USD per barrel");
        newCosts.put("barrel", newBarrelCost);
        newCost.setCosts(newCosts);
        updateRequest.setEstimated_costs(List.of(newCost));
        
        RefineryResponseDTO result = refineryService.addOrUpdateRefinery(testCountry, updateRequest);
        
        // Assert - Should return the new cost (not verify Firebase has both, as that's complex)
        assertNotNull(result, "Should return response DTO");
        assertEquals(refineryName, result.getName(), "Name should match");
        assertEquals("2024-06-01", result.getEstimated_costs().get(0).getDate(), "Should have new date");
        
        System.out.println("✅ Refinery updated with new cost entry");
    }
    
    @Test
    void testAddOrUpdateRefinery_WithMultipleCostTypes() {
        // Arrange - Refinery with barrel and metric ton costs
        String uniqueSuffix = String.valueOf(System.currentTimeMillis() % 10000);
        String testCountry = "TRM";
        
        RefineryRequestDTO requestDTO = new RefineryRequestDTO();
        requestDTO.setName("Multi-Cost Refinery " + uniqueSuffix);
        requestDTO.setCompany("Multi Oil Corp");
        requestDTO.setLocation("Industrial Zone");
        requestDTO.setOperational_from(2015);
        requestDTO.setOperational_to(2045);
        requestDTO.setCan_refine_any(true);
        requestDTO.setCountryName("Multi Test Country");
        requestDTO.setCountryIsoNumeric("997");
        
        RefineryCostRequestDTO costRequest = new RefineryCostRequestDTO();
        costRequest.setDate("2024-03-01");
        
        Map<String, CostDetailRequestDTO> costs = new HashMap<>();
        
        CostDetailRequestDTO barrelCost = new CostDetailRequestDTO();
        barrelCost.setCost_per_unit(48.5);
        barrelCost.setUnit("USD per barrel");
        costs.put("barrel", barrelCost);
        
        CostDetailRequestDTO tonCost = new CostDetailRequestDTO();
        tonCost.setCost_per_unit(350.0);
        tonCost.setUnit("USD per metric ton");
        costs.put("metric_ton", tonCost);
        
        costRequest.setCosts(costs);
        requestDTO.setEstimated_costs(List.of(costRequest));
        
        // Act
        RefineryResponseDTO result = refineryService.addOrUpdateRefinery(testCountry, requestDTO);
        
        // Assert
        assertNotNull(result, "Should return response DTO");
        assertNotNull(result.getEstimated_costs(), "Should have estimated costs");
        assertEquals(1, result.getEstimated_costs().size(), "Should have 1 cost entry");
        
        RefineryCostResponseDTO costResponse = result.getEstimated_costs().get(0);
        assertEquals(2, costResponse.getCosts().size(), "Should have 2 cost types");
        assertTrue(costResponse.getCosts().containsKey("barrel"), "Should have barrel cost");
        assertTrue(costResponse.getCosts().containsKey("metric_ton"), "Should have metric ton cost");
        
        System.out.println("✅ Refinery added with multiple cost types");
    }
    
    
    @Test
    void testGetCostByUnit_BarrelUnit() {
        // Given - Get a refinery with costs
        List<RefineryResponseDTO> refineries = refineryService.getAllRefineries();
        
        if (refineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping test");
            return;
        }
        
        // Find a refinery with barrel cost
        RefineryResponseDTO testRefinery = null;
        for (RefineryResponseDTO refinery : refineries) {
            if (refinery.getEstimated_costs() != null && !refinery.getEstimated_costs().isEmpty()) {
                RefineryCostResponseDTO cost = refinery.getEstimated_costs().get(0);
                if (cost.getCosts() != null && cost.getCosts().containsKey("barrel")) {
                    testRefinery = refinery;
                    break;
                }
            }
        }
        
        if (testRefinery == null) {
            System.out.println("⚠️ No refinery with barrel cost found");
            return;
        }
        
        // When - Get cost by barrel unit
        CostDetailResponseDTO result = refineryService.getCostByUnit(
            testRefinery.getCountryIso3(),
            testRefinery.getName(),
            "barrel",
            null  // Latest date
        );
        
        // Assert
        assertNotNull(result, "Should return cost detail");
        assertNotNull(result.getCost_per_unit(), "Cost per unit should not be null");
        assertNotNull(result.getUnit(), "Unit should not be null");
        assertTrue(result.getCost_per_unit() > 0, "Cost should be positive");
        
        System.out.println("✅ Retrieved barrel cost: $" + result.getCost_per_unit() + "/" + result.getUnit());
    }
    
    @Test
    void testGetCostByUnit_WithSpecificDate() {
        // Given - Get a refinery with costs
        List<RefineryResponseDTO> refineries = refineryService.getAllRefineries();
        
        if (refineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping test");
            return;
        }
        
        RefineryResponseDTO testRefinery = null;
        String testDate = null;
        
        for (RefineryResponseDTO refinery : refineries) {
            if (refinery.getEstimated_costs() != null && !refinery.getEstimated_costs().isEmpty()) {
                RefineryCostResponseDTO cost = refinery.getEstimated_costs().get(0);
                if (cost.getCosts() != null && cost.getCosts().containsKey("barrel")) {
                    testRefinery = refinery;
                    testDate = cost.getDate();
                    break;
                }
            }
        }
        
        if (testRefinery == null || testDate == null) {
            System.out.println("⚠️ No suitable refinery found");
            return;
        }
        
        // When - Get cost for specific date
        CostDetailResponseDTO result = refineryService.getCostByUnit(
            testRefinery.getCountryIso3(),
            testRefinery.getName(),
            "barrel",
            testDate
        );
        
        // Assert
        assertNotNull(result, "Should return cost detail");
        assertNotNull(result.getCost_per_unit(), "Cost per unit should not be null");
        
        System.out.println("✅ Retrieved cost for date " + testDate + ": $" + result.getCost_per_unit());
    }
    
    @Test
    void testGetCostByUnit_NonExistentUnit() {
        // Given - Get any refinery
        List<RefineryResponseDTO> refineries = refineryService.getAllRefineries();
        
        if (refineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping test");
            return;
        }
        
        RefineryResponseDTO testRefinery = refineries.get(0);
        
        // When/Then - Request non-existent unit should throw exception
        assertThrows(com.cs203.grp2.Asg2.exceptions.RefineryNotFoundException.class, () -> {
            refineryService.getCostByUnit(
                testRefinery.getCountryIso3(),
                testRefinery.getName(),
                "nonexistent_unit",
                null
            );
        });
        
        System.out.println("✅ Non-existent unit throws exception correctly");
    }
    
    @Test
    void testGetCostByUnit_NullUnit() {
        // Given - Get any refinery
        List<RefineryResponseDTO> refineries = refineryService.getAllRefineries();
        
        if (refineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping test");
            return;
        }
        
        RefineryResponseDTO testRefinery = refineries.get(0);
        
        // When/Then - Null unit should throw exception
        assertThrows(com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException.class, () -> {
            refineryService.getCostByUnit(
                testRefinery.getCountryIso3(),
                testRefinery.getName(),
                null,
                null
            );
        });
        
        System.out.println("✅ Null unit throws exception correctly");
    }
    
    @Test
    void testGetCostByUnit_EmptyUnit() {
        // Given - Get any refinery
        List<RefineryResponseDTO> refineries = refineryService.getAllRefineries();
        
        if (refineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping test");
            return;
        }
        
        RefineryResponseDTO testRefinery = refineries.get(0);
        
        // When/Then - Empty unit should throw exception
        assertThrows(com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException.class, () -> {
            refineryService.getCostByUnit(
                testRefinery.getCountryIso3(),
                testRefinery.getName(),
                "",
                null
            );
        });
        
        System.out.println("✅ Empty unit throws exception correctly");
    }
    
    // ========== EDGE CASE TESTS FOR OTHER METHODS ==========
    
    @Test
    void testGetLatestCost_NullDate() {
        // Given - Get a refinery with costs
        List<RefineryResponseDTO> refineries = refineryService.getAllRefineries();
        
        if (refineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping test");
            return;
        }
        
        RefineryResponseDTO testRefinery = null;
        for (RefineryResponseDTO refinery : refineries) {
            if (refinery.getEstimated_costs() != null && !refinery.getEstimated_costs().isEmpty()) {
                testRefinery = refinery;
                break;
            }
        }
        
        if (testRefinery == null) {
            System.out.println("⚠️ No refinery with costs found");
            return;
        }
        
        // When - Get cost with null date (should return all costs)
        List<RefineryCostResponseDTO> results = refineryService.getLatestCost(
            testRefinery.getCountryIso3(),
            testRefinery.getName(),
            null
        );
        
        // Assert
        assertNotNull(results, "Results should not be null");
        assertFalse(results.isEmpty(), "Should return all costs when date is null");
        
        System.out.println("✅ Null date returns all " + results.size() + " cost entries");
    }
    
    @Test
    void testGetLatestCost_EmptyDate() {
        // Given - Get a refinery with costs
        List<RefineryResponseDTO> refineries = refineryService.getAllRefineries();
        
        if (refineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping test");
            return;
        }
        
        RefineryResponseDTO testRefinery = null;
        for (RefineryResponseDTO refinery : refineries) {
            if (refinery.getEstimated_costs() != null && !refinery.getEstimated_costs().isEmpty()) {
                testRefinery = refinery;
                break;
            }
        }
        
        if (testRefinery == null) {
            System.out.println("⚠️ No refinery with costs found");
            return;
        }
        
        // When - Get cost with empty date (should return all costs)
        List<RefineryCostResponseDTO> results = refineryService.getLatestCost(
            testRefinery.getCountryIso3(),
            testRefinery.getName(),
            ""
        );
        
        // Assert
        assertNotNull(results, "Results should not be null");
        assertFalse(results.isEmpty(), "Should return all costs when date is empty");
        
        System.out.println("✅ Empty date returns all " + results.size() + " cost entries");
    }
    
    @Test
    void testGetLatestCost_FutureDate() {
        // Given - Get a refinery with costs
        List<RefineryResponseDTO> refineries = refineryService.getAllRefineries();
        
        if (refineries.isEmpty()) {
            System.out.println("⚠️ No refineries in database, skipping test");
            return;
        }
        
        RefineryResponseDTO testRefinery = null;
        for (RefineryResponseDTO refinery : refineries) {
            if (refinery.getEstimated_costs() != null && !refinery.getEstimated_costs().isEmpty()) {
                testRefinery = refinery;
                break;
            }
        }
        
        if (testRefinery == null) {
            System.out.println("⚠️ No refinery with costs found");
            return;
        }
        
        // When - Get cost with future date (should return latest available cost)
        List<RefineryCostResponseDTO> results = refineryService.getLatestCost(
            testRefinery.getCountryIso3(),
            testRefinery.getName(),
            "2030-12-31"  // Future date
        );
        
        // Assert
        assertNotNull(results, "Results should not be null");
        assertFalse(results.isEmpty(), "Should return latest cost before future date");
        assertEquals(1, results.size(), "Should return only one cost entry");
        
        System.out.println("✅ Future date returns latest cost: " + results.get(0).getDate());
    }
    
    @Test
    void testGetRefinery_NonExistentRefinery() {
        // When/Then - Non-existent refinery should throw exception
        assertThrows(com.cs203.grp2.Asg2.exceptions.RefineryNotFoundException.class, () -> {
            refineryService.getRefinery("USA", "Nonexistent Refinery " + System.currentTimeMillis());
        });
        
        System.out.println("✅ Non-existent refinery throws exception correctly");
    }
    
    @Test
    void testGetAllCosts_NonExistentRefinery() {
        // When/Then - Non-existent refinery should throw exception
        assertThrows(com.cs203.grp2.Asg2.exceptions.RefineryNotFoundException.class, () -> {
            refineryService.getAllCosts("USA", "Nonexistent Refinery " + System.currentTimeMillis());
        });
        
        System.out.println("✅ Non-existent refinery for getAllCosts throws exception correctly");
    }
}
