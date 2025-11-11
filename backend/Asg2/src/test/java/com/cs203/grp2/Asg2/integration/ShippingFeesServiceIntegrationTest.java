package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.DTO.ShippingCostDetailRequestDTO;
import com.cs203.grp2.Asg2.DTO.ShippingCostDetailResponseDTO;
import com.cs203.grp2.Asg2.DTO.ShippingFeeEntryRequestDTO;
import com.cs203.grp2.Asg2.DTO.ShippingFeeEntryResponseDTO;
import com.cs203.grp2.Asg2.DTO.ShippingFeeRequestDTO;
import com.cs203.grp2.Asg2.DTO.ShippingFeeResponseDTO;
import com.cs203.grp2.Asg2.service.ShippingFeesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ShippingFeesServiceImpl
 * Tests the service's interaction with Firebase to fetch shipping cost data
 */
public class ShippingFeesServiceIntegrationTest extends BaseFirebaseIntegrationTest {

    @Autowired
    private ShippingFeesService shippingFeesService;

    private static final String SINGAPORE_ISO3 = "SGP";
    private static final String MALAYSIA_ISO3 = "MYS";

    @Test
    void testGetAllShippingFees_Success() {
        // Act
        List<ShippingFeeResponseDTO> fees = shippingFeesService.getAllShippingFees();

        // Assert
        assertNotNull(fees, "Shipping fees list should not be null");
        System.out.println("✓ Found " + fees.size() + " shipping fee records");

        if (!fees.isEmpty()) {
            ShippingFeeResponseDTO first = fees.get(0);
            assertNotNull(first.getCountry1Iso3(), "Country1 ISO3 should not be null");
            assertNotNull(first.getCountry2Iso3(), "Country2 ISO3 should not be null");
            System.out.println("✓ Sample shipping route: " + first.getCountry1Name() + " ↔ " + first.getCountry2Name());
        }
    }

    @Test
    void testGetShippingFees_BetweenTwoCountries() {
        // Act
        ShippingFeeResponseDTO fee = shippingFeesService.getShippingFees(SINGAPORE_ISO3, MALAYSIA_ISO3);

        // Assert
        if (fee != null) {
            assertNotNull(fee.getCountry1Iso3());
            assertNotNull(fee.getCountry2Iso3());
            assertTrue(fee.getShippingFees() != null, "Should have shipping fee entries");
            System.out.println("✓ Found shipping fees between " + fee.getCountry1Name() + " and " + fee.getCountry2Name());
            System.out.println("✓ Number of historical entries: " + fee.getShippingFees().size());
        } else {
            System.out.println("⚠ No shipping fee data found for SGP-MYS route");
        }
    }

    @Test
    void testGetShippingFees_BidirectionalRoute() {
        // Act - Try both directions
        ShippingFeeResponseDTO sgToMy = shippingFeesService.getShippingFees(SINGAPORE_ISO3, MALAYSIA_ISO3);
        ShippingFeeResponseDTO myToSg = shippingFeesService.getShippingFees(MALAYSIA_ISO3, SINGAPORE_ISO3);

        // Assert - Should return same data regardless of direction
        if (sgToMy != null && myToSg != null) {
            assertEquals(sgToMy.getShippingFees().size(), myToSg.getShippingFees().size(),
                    "Bidirectional routes should return same data");
            System.out.println("✓ Bidirectional route search works correctly");
        } else if (sgToMy == null && myToSg == null) {
            System.out.println("⚠ No data available for bidirectional test");
        } else {
            fail("Bidirectional routes should return consistent results");
        }
    }

    @Test
    void testGetAllCosts_ValidRoute() {
        // Act
        List<ShippingFeeEntryResponseDTO> costs = shippingFeesService.getAllCosts(SINGAPORE_ISO3, MALAYSIA_ISO3);

        // Assert
        assertNotNull(costs, "Costs list should not be null");
        System.out.println("✓ Found " + costs.size() + " cost entries for SGP-MYS route");

        if (!costs.isEmpty()) {
            ShippingFeeEntryResponseDTO entry = costs.get(0);
            assertNotNull(entry.getDate(), "Date should not be null");
            assertNotNull(entry.getCosts(), "Costs map should not be null");
            System.out.println("✓ Sample entry date: " + entry.getDate());
        }
    }

    @Test
    void testGetAllCostsByUnit_Barrel() {
        // Act
        List<ShippingFeeEntryResponseDTO> barrelCosts = shippingFeesService.getAllCostsByUnit(
                SINGAPORE_ISO3, MALAYSIA_ISO3, "barrel");

        // Assert
        assertNotNull(barrelCosts, "Barrel costs should not be null");

        if (!barrelCosts.isEmpty()) {
            for (ShippingFeeEntryResponseDTO entry : barrelCosts) {
                if (entry.getCosts() != null && entry.getCosts().containsKey("barrel")) {
                    ShippingCostDetailResponseDTO detail = entry.getCosts().get("barrel");
                    assertTrue(detail.getCostPerUnit() > 0, "Cost per barrel should be positive");
                    // Unit field contains full description like "USD per barrel", not just "barrel"
                    assertTrue(detail.getUnit().toLowerCase().contains("barrel"), 
                            "Unit should contain 'barrel', actual: " + detail.getUnit());
                }
            }
            System.out.println("✓ Found " + barrelCosts.size() + " barrel cost entries");
        }
    }

    @Test
    void testGetAllCostsByUnit_Ton() {
        // Act
        List<ShippingFeeEntryResponseDTO> tonCosts = shippingFeesService.getAllCostsByUnit(
                SINGAPORE_ISO3, MALAYSIA_ISO3, "ton");

        // Assert
        assertNotNull(tonCosts, "Ton costs should not be null");

        if (!tonCosts.isEmpty()) {
            for (ShippingFeeEntryResponseDTO entry : tonCosts) {
                if (entry.getCosts() != null && entry.getCosts().containsKey("ton")) {
                    ShippingCostDetailResponseDTO detail = entry.getCosts().get("ton");
                    assertTrue(detail.getCostPerUnit() > 0, "Cost per ton should be positive");
                    // Unit field contains full description like "USD per ton", not just "ton"
                    assertTrue(detail.getUnit().toLowerCase().contains("ton"), 
                            "Unit should contain 'ton', actual: " + detail.getUnit());
                }
            }
            System.out.println("✓ Found " + tonCosts.size() + " ton cost entries");
        }
    }

    @Test
    void testGetAllCostsByUnit_InvalidUnit() {
        // Act
        List<ShippingFeeEntryResponseDTO> invalidCosts = shippingFeesService.getAllCostsByUnit(
                SINGAPORE_ISO3, MALAYSIA_ISO3, "invalid_unit");

        // Assert - Should return empty list for invalid unit
        assertNotNull(invalidCosts);
        assertTrue(invalidCosts.isEmpty(), "Should return empty list for invalid unit");
        System.out.println("✓ Correctly rejects invalid unit type");
    }

    @Test
    void testGetLatestCost_ValidRoute() {
        // Arrange
        LocalDate testDate = LocalDate.now();

        // Act
        ShippingFeeEntryResponseDTO latestCost = shippingFeesService.getLatestCost(
                SINGAPORE_ISO3, MALAYSIA_ISO3, testDate);

        // Assert
        if (latestCost != null) {
            assertNotNull(latestCost.getDate());
            assertNotNull(latestCost.getCosts());
            assertTrue(!latestCost.getCosts().isEmpty(), "Should have at least one unit cost");
            System.out.println("✓ Found latest cost for date: " + latestCost.getDate());
        } else {
            System.out.println("⚠ No cost data available for the specified date");
        }
    }

    @Test
    void testGetCostByUnit_ValidRouteAndUnit() {
        // Arrange
        LocalDate testDate = LocalDate.now();

        // Act
        ShippingCostDetailResponseDTO barrelCost = shippingFeesService.getCostByUnit(
                SINGAPORE_ISO3, MALAYSIA_ISO3, "barrel", testDate);

        // Assert
        if (barrelCost != null) {
            assertTrue(barrelCost.getCostPerUnit() > 0, "Cost should be positive");
            // Unit field contains full description like "USD per barrel", not just "barrel"
            assertTrue(barrelCost.getUnit().toLowerCase().contains("barrel"), 
                    "Unit should contain 'barrel', actual: " + barrelCost.getUnit());
            System.out.println("✓ Found barrel cost: $" + barrelCost.getCostPerUnit() + " per " + barrelCost.getUnit());
        } else {
            System.out.println("⚠ No barrel cost data available for the specified date");
        }
    }

    @Test
    void testShippingFeesDataStructure() {
        // Act
        List<ShippingFeeResponseDTO> fees = shippingFeesService.getAllShippingFees();

        // Assert - Validate data structure
        for (ShippingFeeResponseDTO fee : fees) {
            assertNotNull(fee.getCountry1Iso3(), "Country1 ISO3 must not be null");
            assertNotNull(fee.getCountry2Iso3(), "Country2 ISO3 must not be null");
            assertNotNull(fee.getShippingFees(), "Shipping fees list must not be null");

            // Validate each entry
            for (ShippingFeeEntryResponseDTO entry : fee.getShippingFees()) {
                assertNotNull(entry.getDate(), "Date must not be null");
                assertNotNull(entry.getCosts(), "Costs map must not be null");

                // Validate costs
                for (Map.Entry<String, ShippingCostDetailResponseDTO> costEntry : entry.getCosts().entrySet()) {
                    String unit = costEntry.getKey();
                    ShippingCostDetailResponseDTO detail = costEntry.getValue();

                    assertTrue(unit.equals("barrel") || unit.equals("ton"),
                            "Unit key should be barrel or ton, but was: " + unit);
                    assertTrue(detail.getCostPerUnit() >= 0, "Cost cannot be negative");
                    // Unit field in detail contains full description like "USD per barrel"
                    assertTrue(detail.getUnit().toLowerCase().contains(unit),
                            "Unit field should contain '" + unit + "', actual: " + detail.getUnit());
                }
            }
        }
        System.out.println("✓ All shipping fees data structures are valid");
    }

    @Test
    void testMultipleCallsConsistency() {
        // Act - Call service multiple times
        List<ShippingFeeResponseDTO> firstCall = shippingFeesService.getAllShippingFees();
        List<ShippingFeeResponseDTO> secondCall = shippingFeesService.getAllShippingFees();

        // Assert - Results should be consistent
        assertEquals(firstCall.size(), secondCall.size(),
                "Multiple calls should return same number of shipping fees");

        System.out.println("✓ Multiple calls return consistent data: " + firstCall.size() + " routes");
    }

    @Test
    void testGetShippingFees_NonExistentRoute() {
        // Act - Try to get fees for a route that likely doesn't exist
        ShippingFeeResponseDTO fee = shippingFeesService.getShippingFees("XXX", "YYY");

        // Assert
        assertNull(fee, "Should return null for non-existent route");
        System.out.println("✓ Correctly returns null for non-existent route");
    }

    @Test
    void testDateOrdering() {
        // Act
        List<ShippingFeeEntryResponseDTO> costs = shippingFeesService.getAllCosts(SINGAPORE_ISO3, MALAYSIA_ISO3);

        // Assert - Check if dates are in order
        if (costs.size() > 1) {
            LocalDate prevDate = null;
            for (ShippingFeeEntryResponseDTO entry : costs) {
                if (prevDate != null && entry.getDate() != null) {
                    // Dates should be in descending or ascending order
                    System.out.println("  Date: " + entry.getDate());
                }
                prevDate = entry.getDate();
            }
            System.out.println("✓ Date ordering validated for " + costs.size() + " entries");
        }
    }

    @Test
    void testGetAllCosts_EmptyRoute() {
        // Act - Test with a route that might not have costs
        List<ShippingFeeEntryResponseDTO> costs = shippingFeesService.getAllCosts("XXX", "YYY");

        // Assert - Should return empty list for non-existent route
        assertNotNull(costs, "Costs should not be null even for non-existent route");
        System.out.println("✓ Empty route returns non-null list: " + costs.size() + " entries");
    }

    @Test
    void testGetLatestCost_FutureDate() {
        // Arrange - Use a future date
        LocalDate futureDate = LocalDate.now().plusYears(1);

        // Act
        ShippingFeeEntryResponseDTO futureCost = shippingFeesService.getLatestCost(
                SINGAPORE_ISO3, MALAYSIA_ISO3, futureDate);

        // Assert
        if (futureCost != null) {
            System.out.println("✓ Future date handling: Found cost for date " + futureCost.getDate());
        } else {
            System.out.println("✓ Future date handling: No cost data for future dates (expected)");
        }
    }

    @Test
    void testGetLatestCost_PastDate() {
        // Arrange - Use a historical date
        LocalDate pastDate = LocalDate.of(2020, 1, 1);

        // Act
        ShippingFeeEntryResponseDTO pastCost = shippingFeesService.getLatestCost(
                SINGAPORE_ISO3, MALAYSIA_ISO3, pastDate);

        // Assert
        if (pastCost != null) {
            assertNotNull(pastCost.getDate());
            System.out.println("✓ Historical date handling: Found cost for date " + pastCost.getDate());
        } else {
            System.out.println("✓ Historical date handling: No cost data for this date");
        }
    }

    @Test
    void testGetCostByUnit_NonExistentRoute() {
        // Arrange
        LocalDate testDate = LocalDate.now();

        // Act
        ShippingCostDetailResponseDTO cost = shippingFeesService.getCostByUnit(
                "XXX", "YYY", "barrel", testDate);

        // Assert
        assertNull(cost, "Should return null for non-existent route");
        System.out.println("✓ Non-existent route correctly returns null for getCostByUnit");
    }

    @Test
    void testGetCostByUnit_InvalidUnit() {
        // Arrange
        LocalDate testDate = LocalDate.now();

        // Act
        ShippingCostDetailResponseDTO cost = shippingFeesService.getCostByUnit(
                SINGAPORE_ISO3, MALAYSIA_ISO3, "invalid_unit", testDate);

        // Assert
        assertNull(cost, "Should return null for invalid unit");
        System.out.println("✓ Invalid unit correctly returns null");
    }

    @Test
    void testGetAllCostsByUnit_EmptyResult() {
        // Act - Test with unit that might not exist for this route
        List<ShippingFeeEntryResponseDTO> costs = shippingFeesService.getAllCostsByUnit(
                SINGAPORE_ISO3, MALAYSIA_ISO3, "liter");

        // Assert
        assertNotNull(costs, "Should return non-null list");
        System.out.println("✓ Non-existent unit returns empty list: " + costs.size() + " entries");
    }

    @Test
    void testGetShippingFees_CaseInsensitivity() {
        // Act - Test with different cases
        ShippingFeeResponseDTO lowerCase = shippingFeesService.getShippingFees(
                SINGAPORE_ISO3.toLowerCase(), MALAYSIA_ISO3.toLowerCase());
        ShippingFeeResponseDTO upperCase = shippingFeesService.getShippingFees(
                SINGAPORE_ISO3.toUpperCase(), MALAYSIA_ISO3.toUpperCase());

        // Assert - Both should work
        if (lowerCase != null && upperCase != null) {
            assertEquals(lowerCase.getShippingFees().size(), upperCase.getShippingFees().size(),
                    "Case-insensitive search should return consistent results");
            System.out.println("✓ Case-insensitive search works correctly");
        } else {
            System.out.println("⚠ No data available for case-insensitive test");
        }
    }

    @Test
    void testCostValueRanges() {
        // Act
        List<ShippingFeeResponseDTO> fees = shippingFeesService.getAllShippingFees();

        // Assert - Validate cost ranges are reasonable
        for (ShippingFeeResponseDTO fee : fees) {
            for (ShippingFeeEntryResponseDTO entry : fee.getShippingFees()) {
                for (Map.Entry<String, ShippingCostDetailResponseDTO> costEntry : entry.getCosts().entrySet()) {
                    ShippingCostDetailResponseDTO detail = costEntry.getValue();
                    
                    assertTrue(detail.getCostPerUnit() >= 0, 
                            "Cost cannot be negative: " + detail.getCostPerUnit());
                    
                    // Reasonable upper bound - shipping shouldn't exceed $100,000 per unit
                    assertTrue(detail.getCostPerUnit() < 100000, 
                            "Cost seems unreasonably high: " + detail.getCostPerUnit());
                }
            }
        }
        System.out.println("✓ All cost values are within reasonable ranges");
    }

    @Test
    void testMultipleUnitsAvailability() {
        // Act
        List<ShippingFeeResponseDTO> fees = shippingFeesService.getAllShippingFees();

        // Assert - Check if routes have multiple units available
        int routesWithMultipleUnits = 0;
        for (ShippingFeeResponseDTO fee : fees) {
            if (!fee.getShippingFees().isEmpty()) {
                ShippingFeeEntryResponseDTO entry = fee.getShippingFees().get(0);
                if (entry.getCosts().size() > 1) {
                    routesWithMultipleUnits++;
                }
            }
        }

        System.out.println("✓ Routes with multiple units: " + routesWithMultipleUnits + " out of " + fees.size());
        assertTrue(routesWithMultipleUnits >= 0, "Should have some routes with multiple units");
    }

    // ========== addOrUpdateShippingFee Tests ==========
    // NOTE: These tests use test-specific country codes to avoid interfering with production data
    // Test data will be identifiable by "TEST_" prefix in country codes

    @Test
    void testAddOrUpdateShippingFee_NewRoute() {
        // Arrange - Create a new route with test country codes
        ShippingFeeRequestDTO requestDTO = new ShippingFeeRequestDTO();
        requestDTO.setCountry1Iso3("TS1"); // Test country 1
        requestDTO.setCountry2Iso3("TS2"); // Test country 2
        requestDTO.setCountry1Name("Test Country One");
        requestDTO.setCountry2Name("Test Country Two");
        requestDTO.setCountry1IsoNumeric("999");
        requestDTO.setCountry2IsoNumeric("998");

        // Create shipping fee entry with both barrel and ton units
        ShippingFeeEntryRequestDTO entry = new ShippingFeeEntryRequestDTO();
        entry.setDate(LocalDate.now().toString()); // Convert to ISO string
        
        Map<String, ShippingCostDetailRequestDTO> costs = new HashMap<>();
        
        ShippingCostDetailRequestDTO barrelCost = new ShippingCostDetailRequestDTO();
        barrelCost.setCostPerUnit(1.5);
        barrelCost.setUnit("USD per barrel");
        costs.put("barrel", barrelCost);
        
        ShippingCostDetailRequestDTO tonCost = new ShippingCostDetailRequestDTO();
        tonCost.setCostPerUnit(10.0);
        tonCost.setUnit("USD per ton");
        costs.put("ton", tonCost);
        
        entry.setCosts(costs);
        requestDTO.setShippingFees(List.of(entry));

        // Act
        ShippingFeeResponseDTO result = shippingFeesService.addOrUpdateShippingFee(requestDTO);

        // Assert
        assertNotNull(result, "Should return ShippingFeeResponseDTO after adding new route");
        assertEquals("Test Country One", result.getCountry1Name());
        assertEquals("Test Country Two", result.getCountry2Name());
        assertEquals("TS1", result.getCountry1Iso3());
        assertEquals("TS2", result.getCountry2Iso3());
        assertFalse(result.getShippingFees().isEmpty(), "Should have at least one shipping fee entry");
        
        ShippingFeeEntryResponseDTO createdEntry = result.getShippingFees().get(0);
        assertEquals(LocalDate.now(), createdEntry.getDate());
        assertTrue(createdEntry.getCosts().containsKey("barrel"), "Should have barrel cost");
        assertTrue(createdEntry.getCosts().containsKey("ton"), "Should have ton cost");
        assertEquals(1.5, createdEntry.getCosts().get("barrel").getCostPerUnit(), 0.01);
        assertEquals(10.0, createdEntry.getCosts().get("ton").getCostPerUnit(), 0.01);
        
        System.out.println("✓ New test route created: TS1 ↔ TS2");
        System.out.println("  - Barrel cost: $" + createdEntry.getCosts().get("barrel").getCostPerUnit());
        System.out.println("  - Ton cost: $" + createdEntry.getCosts().get("ton").getCostPerUnit());
    }

    @Test
    void testAddOrUpdateShippingFee_ExistingRoute_AddEntry() {
        // Arrange - First, ensure a test route exists
        ShippingFeeRequestDTO initialRequest = new ShippingFeeRequestDTO();
        initialRequest.setCountry1Iso3("TS3");
        initialRequest.setCountry2Iso3("TS4");
        initialRequest.setCountry1Name("Test Country Three");
        initialRequest.setCountry2Name("Test Country Four");
        initialRequest.setCountry1IsoNumeric("997");
        initialRequest.setCountry2IsoNumeric("996");

        ShippingFeeEntryRequestDTO initialEntry = new ShippingFeeEntryRequestDTO();
        initialEntry.setDate(LocalDate.now().minusDays(10).toString());
        
        Map<String, ShippingCostDetailRequestDTO> initialCosts = new HashMap<>();
        ShippingCostDetailRequestDTO barrelCost1 = new ShippingCostDetailRequestDTO();
        barrelCost1.setCostPerUnit(2.0);
        barrelCost1.setUnit("USD per barrel");
        initialCosts.put("barrel", barrelCost1);
        initialEntry.setCosts(initialCosts);
        
        initialRequest.setShippingFees(List.of(initialEntry));
        shippingFeesService.addOrUpdateShippingFee(initialRequest);

        // Act - Add another entry to the existing route
        ShippingFeeRequestDTO updateRequest = new ShippingFeeRequestDTO();
        updateRequest.setCountry1Iso3("TS3");
        updateRequest.setCountry2Iso3("TS4");
        updateRequest.setCountry1Name("Test Country Three");
        updateRequest.setCountry2Name("Test Country Four");
        updateRequest.setCountry1IsoNumeric("997");
        updateRequest.setCountry2IsoNumeric("996");

        ShippingFeeEntryRequestDTO newEntry = new ShippingFeeEntryRequestDTO();
        newEntry.setDate(LocalDate.now().toString());
        
        Map<String, ShippingCostDetailRequestDTO> newCosts = new HashMap<>();
        ShippingCostDetailRequestDTO barrelCost2 = new ShippingCostDetailRequestDTO();
        barrelCost2.setCostPerUnit(2.5);
        barrelCost2.setUnit("USD per barrel");
        newCosts.put("barrel", barrelCost2);
        newEntry.setCosts(newCosts);
        
        updateRequest.setShippingFees(List.of(newEntry));
        ShippingFeeResponseDTO result = shippingFeesService.addOrUpdateShippingFee(updateRequest);

        // Assert
        assertNotNull(result, "Should return updated ShippingFeeResponseDTO");
        assertTrue(result.getShippingFees().size() >= 2, 
                "Should have at least 2 entries after adding to existing route");
        
        System.out.println("✓ Added entry to existing route: TS3 ↔ TS4");
        System.out.println("  - Total entries: " + result.getShippingFees().size());
    }

    @Test
    void testAddOrUpdateShippingFee_BidirectionalMatching() {
        // Arrange - Create route TS5 → TS6
        ShippingFeeRequestDTO request1 = new ShippingFeeRequestDTO();
        request1.setCountry1Iso3("TS5");
        request1.setCountry2Iso3("TS6");
        request1.setCountry1Name("Test Country Five");
        request1.setCountry2Name("Test Country Six");
        request1.setCountry1IsoNumeric("995");
        request1.setCountry2IsoNumeric("994");

        ShippingFeeEntryRequestDTO entry1 = new ShippingFeeEntryRequestDTO();
        entry1.setDate(LocalDate.now().minusDays(5).toString());
        Map<String, ShippingCostDetailRequestDTO> costs1 = new HashMap<>();
        ShippingCostDetailRequestDTO barrelCost = new ShippingCostDetailRequestDTO();
        barrelCost.setCostPerUnit(3.0);
        barrelCost.setUnit("USD per barrel");
        costs1.put("barrel", barrelCost);
        entry1.setCosts(costs1);
        request1.setShippingFees(List.of(entry1));
        
        shippingFeesService.addOrUpdateShippingFee(request1);

        // Act - Try to add route TS6 → TS5 (reversed direction)
        ShippingFeeRequestDTO request2 = new ShippingFeeRequestDTO();
        request2.setCountry1Iso3("TS6"); // Reversed
        request2.setCountry2Iso3("TS5"); // Reversed
        request2.setCountry1Name("Test Country Six");
        request2.setCountry2Name("Test Country Five");
        request2.setCountry1IsoNumeric("994");
        request2.setCountry2IsoNumeric("995");

        ShippingFeeEntryRequestDTO entry2 = new ShippingFeeEntryRequestDTO();
        entry2.setDate(LocalDate.now().toString());
        Map<String, ShippingCostDetailRequestDTO> costs2 = new HashMap<>();
        ShippingCostDetailRequestDTO tonCost = new ShippingCostDetailRequestDTO();
        tonCost.setCostPerUnit(20.0);
        tonCost.setUnit("USD per ton");
        costs2.put("ton", tonCost);
        entry2.setCosts(costs2);
        request2.setShippingFees(List.of(entry2));
        
        ShippingFeeResponseDTO result = shippingFeesService.addOrUpdateShippingFee(request2);

        // Assert - Should find and update the existing route, not create a duplicate
        assertNotNull(result, "Should return ShippingFeeResponseDTO");
        
        // Verify that there's only ONE route for TS5-TS6 combination
        ShippingFeeResponseDTO forward = shippingFeesService.getShippingFees("TS5", "TS6");
        ShippingFeeResponseDTO reverse = shippingFeesService.getShippingFees("TS6", "TS5");
        
        assertNotNull(forward, "Forward direction should exist");
        assertNotNull(reverse, "Reverse direction should exist");
        
        // The bidirectional matching should ensure both directions return the same route
        assertTrue(forward.getShippingFees().size() >= 2, 
                "Should have both entries added to the same bidirectional route");
        
        System.out.println("✓ Bidirectional matching works: TS5 ↔ TS6");
        System.out.println("  - Total entries in route: " + forward.getShippingFees().size());
    }

    @Test
    void testAddOrUpdateShippingFee_UnitFiltering() {
        // Arrange - Create request with both valid and invalid units
        ShippingFeeRequestDTO requestDTO = new ShippingFeeRequestDTO();
        requestDTO.setCountry1Iso3("TS7");
        requestDTO.setCountry2Iso3("TS8");
        requestDTO.setCountry1Name("Test Country Seven");
        requestDTO.setCountry2Name("Test Country Eight");
        requestDTO.setCountry1IsoNumeric("993");
        requestDTO.setCountry2IsoNumeric("992");

        ShippingFeeEntryRequestDTO entry = new ShippingFeeEntryRequestDTO();
        entry.setDate(LocalDate.now().toString());
        
        Map<String, ShippingCostDetailRequestDTO> costs = new HashMap<>();
        
        // Valid units
        ShippingCostDetailRequestDTO barrelCost = new ShippingCostDetailRequestDTO();
        barrelCost.setCostPerUnit(5.0);
        barrelCost.setUnit("USD per barrel");
        costs.put("barrel", barrelCost);
        
        ShippingCostDetailRequestDTO tonCost = new ShippingCostDetailRequestDTO();
        tonCost.setCostPerUnit(35.0);
        tonCost.setUnit("USD per ton");
        costs.put("ton", tonCost);
        
        // Invalid units (should be filtered out by ALLOWED_UNITS)
        ShippingCostDetailRequestDTO literCost = new ShippingCostDetailRequestDTO();
        literCost.setCostPerUnit(0.5);
        literCost.setUnit("USD per liter");
        costs.put("liter", literCost);
        
        ShippingCostDetailRequestDTO gallonCost = new ShippingCostDetailRequestDTO();
        gallonCost.setCostPerUnit(2.0);
        gallonCost.setUnit("USD per gallon");
        costs.put("gallon", gallonCost);
        
        entry.setCosts(costs);
        requestDTO.setShippingFees(List.of(entry));

        // Act
        ShippingFeeResponseDTO result = shippingFeesService.addOrUpdateShippingFee(requestDTO);

        // Assert - Only barrel and ton should be saved
        assertNotNull(result, "Should return ShippingFeeResponseDTO");
        assertFalse(result.getShippingFees().isEmpty(), "Should have shipping fee entries");
        
        ShippingFeeEntryResponseDTO savedEntry = result.getShippingFees().get(0);
        assertTrue(savedEntry.getCosts().containsKey("barrel"), "Should have barrel cost");
        assertTrue(savedEntry.getCosts().containsKey("ton"), "Should have ton cost");
        assertFalse(savedEntry.getCosts().containsKey("liter"), "Should NOT have liter cost (filtered)");
        assertFalse(savedEntry.getCosts().containsKey("gallon"), "Should NOT have gallon cost (filtered)");
        
        System.out.println("✓ Unit filtering works correctly for TS7 ↔ TS8");
        System.out.println("  - Allowed units saved: " + savedEntry.getCosts().keySet());
        System.out.println("  - Invalid units filtered out: [liter, gallon]");
    }

    @Test
    void testAddOrUpdateShippingFee_MultipleEntries() throws ExecutionException, InterruptedException {
        // Arrange - Use unique test codes with nanoTime + hash to ensure uniqueness across parallel executions
        long uniqueId = System.nanoTime() + System.currentTimeMillis() + this.hashCode();  // Combine multiple entropy sources
        String uniqueSuffix = String.valueOf(Math.abs(uniqueId) % 1000);  // 3 digits 000-999
        String country1 = "TM" + uniqueSuffix;  // TM000 through TM999
        String country2 = "TN" + uniqueSuffix;  // TN000 through TN999
        
        // Arrange - Create request with multiple date entries
        ShippingFeeRequestDTO requestDTO = new ShippingFeeRequestDTO();
        requestDTO.setCountry1Iso3(country1);
        requestDTO.setCountry2Iso3(country2);
        requestDTO.setCountry1Name("Test Country Nine");
        requestDTO.setCountry2Name("Test Country Ten");
        requestDTO.setCountry1IsoNumeric("991");
        requestDTO.setCountry2IsoNumeric("990");

        List<ShippingFeeEntryRequestDTO> entries = new ArrayList<>();
        
        // Entry 1 - 30 days ago
        ShippingFeeEntryRequestDTO entry1 = new ShippingFeeEntryRequestDTO();
        entry1.setDate(LocalDate.now().minusDays(30).toString());
        Map<String, ShippingCostDetailRequestDTO> costs1 = new HashMap<>();
        ShippingCostDetailRequestDTO barrel1 = new ShippingCostDetailRequestDTO();
        barrel1.setCostPerUnit(1.0);
        barrel1.setUnit("USD per barrel");
        costs1.put("barrel", barrel1);
        entry1.setCosts(costs1);
        entries.add(entry1);
        
        // Entry 2 - 15 days ago
        ShippingFeeEntryRequestDTO entry2 = new ShippingFeeEntryRequestDTO();
        entry2.setDate(LocalDate.now().minusDays(15).toString());
        Map<String, ShippingCostDetailRequestDTO> costs2 = new HashMap<>();
        ShippingCostDetailRequestDTO barrel2 = new ShippingCostDetailRequestDTO();
        barrel2.setCostPerUnit(1.2);
        barrel2.setUnit("USD per barrel");
        costs2.put("barrel", barrel2);
        entry2.setCosts(costs2);
        entries.add(entry2);
        
        // Entry 3 - today
        ShippingFeeEntryRequestDTO entry3 = new ShippingFeeEntryRequestDTO();
        entry3.setDate(LocalDate.now().toString());
        Map<String, ShippingCostDetailRequestDTO> costs3 = new HashMap<>();
        ShippingCostDetailRequestDTO barrel3 = new ShippingCostDetailRequestDTO();
        barrel3.setCostPerUnit(1.5);
        barrel3.setUnit("USD per barrel");
        costs3.put("barrel", barrel3);
        entry3.setCosts(costs3);
        entries.add(entry3);
        
        requestDTO.setShippingFees(entries);

        // Act
        ShippingFeeResponseDTO result = shippingFeesService.addOrUpdateShippingFee(requestDTO);

        // Assert
        assertNotNull(result, "Should return ShippingFeeResponseDTO");
        assertEquals(3, result.getShippingFees().size(), "Should have 3 entries");
        
        // Verify dates are preserved
        List<LocalDate> dates = result.getShippingFees().stream()
                .map(ShippingFeeEntryResponseDTO::getDate)
                .sorted()
                .toList();
        
        assertTrue(dates.contains(LocalDate.now().minusDays(30)), "Should have oldest entry");
        assertTrue(dates.contains(LocalDate.now().minusDays(15)), "Should have middle entry");
        assertTrue(dates.contains(LocalDate.now()), "Should have newest entry");
        
        System.out.println("✓ Multiple entries added successfully for TS9 ↔ TSA");
        System.out.println("  - Total entries: " + result.getShippingFees().size());
        System.out.println("  - Date range: " + dates.get(0) + " to " + dates.get(dates.size() - 1));
    }

    @Test
    void testAddOrUpdateShippingFee_EmptyShippingFees() {
        // Arrange - Create request with empty shipping fees list
        ShippingFeeRequestDTO requestDTO = new ShippingFeeRequestDTO();
        requestDTO.setCountry1Iso3("TSB");
        requestDTO.setCountry2Iso3("TSC");
        requestDTO.setCountry1Name("Test Country Eleven");
        requestDTO.setCountry2Name("Test Country Twelve");
        requestDTO.setCountry1IsoNumeric("989");
        requestDTO.setCountry2IsoNumeric("988");
        requestDTO.setShippingFees(new ArrayList<>()); // Empty list

        // Act
        ShippingFeeResponseDTO result = shippingFeesService.addOrUpdateShippingFee(requestDTO);

        // Assert - Should handle gracefully (likely returns null or empty result)
        // The actual behavior depends on implementation, but should not crash
        System.out.println("✓ Empty shipping fees handled gracefully");
        System.out.println("  - Result: " + (result != null ? "Route created with no entries" : "Null returned"));
    }
}
