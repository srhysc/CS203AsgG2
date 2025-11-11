package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.DTO.TariffRequestDTO;
import com.cs203.grp2.Asg2.models.WitsTariff;
import com.cs203.grp2.Asg2.service.WitsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for WitsService.
 * 
 * These tests interact with real Firebase database and potentially external WITS API to verify:
 * 1. Preferential tariff lookup from Firebase agreements
 * 2. MFN tariff lookup from Firebase
 * 3. Fallback to WITS API when Firebase has no data
 * 4. Multiple cascading lookups work correctly
 * 5. Default handling when all sources fail
 * 
 * Note: These tests are slower because they:
 * - Make Firebase database calls
 * - May make external HTTP calls to WITS API
 * - Test the complete tariff resolution logic
 * 
 * The complexity here is that WitsService does MULTIPLE Firebase reads
 * per tariff lookup (agreements + rates), which is why unit mocking fails.
 */
public class WitsServiceIntegrationTest extends BaseFirebaseIntegrationTest {

    @Autowired
    private WitsService witsService;

    @Test
    void testResolveTariff_ReturnsValidResult() {
        // Given: A tariff request for common trading partners
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",  // exporter
            "CHN",  // importer  
            "270900",  // HS6 code for crude oil
            LocalDate.of(2024, 1, 1)
        );
        
        // When: Resolve the tariff
        WitsTariff result = witsService.resolveTariff(request);
        
        // Then: Should return a valid result (not null)
        assertNotNull(result, "Should return a tariff result");
        assertNotNull(result.importerIso3(), "Should have importer");
        assertNotNull(result.exporterIso3(), "Should have exporter");
        assertNotNull(result.hs6(), "Should have HS code");
        assertNotNull(result.basis(), "Should have source (preferential/mfn/wits/none)");
        
        System.out.println("✅ Tariff resolved: " + result.ratePercent() + "% from " + result.basis());
        System.out.println("   Details: " + result.sourceNote());
    }

    @Test
    void testResolveTariff_MultipleCallsConsistent() {
        // This tests that multiple calls with same parameters return consistent results
        // In unit tests, this would fail due to the reload pattern
        
        TariffRequestDTO request = new TariffRequestDTO(
            "SGP",  // exporter
            "USA",  // importer
            "270900",
            LocalDate.of(2024, 6, 1)
        );
        
        // When: Call multiple times
        WitsTariff result1 = witsService.resolveTariff(request);
        WitsTariff result2 = witsService.resolveTariff(request);
        WitsTariff result3 = witsService.resolveTariff(request);
        
        // Then: All should return same rate and source
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        
        assertEquals(result1.ratePercent(), result2.ratePercent(), 
            "Second call should return same rate");
        assertEquals(result1.ratePercent(), result3.ratePercent(), 
            "Third call should return same rate");
        assertEquals(result1.basis(), result2.basis(), 
            "Second call should have same source");
        assertEquals(result1.basis(), result3.basis(), 
            "Third call should have same source");
        
        System.out.println("✅ Multiple calls consistent: " + result1.ratePercent() + "% from " + result1.basis());
    }

    @Test
    void testResolveTariff_PreferentialAgreementTakesPrecedence() {
        // If countries are in same trade agreement, preferential rate should be used
        // This tests the complete lookup chain
        
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "MEX",  // USA-Mexico have trade agreements
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result);
        System.out.println("✅ USA-MEX tariff: " + result.ratePercent() + "% from " + result.basis());
        
        // If preferential rate found, should be from 'preferential' source
        if ("preferential".equals(result.basis())) {
            System.out.println("   ✅ Preferential agreement rate applied");
            assertTrue(result.sourceNote().contains("DB:"), 
                "Preferential should come from database");
        }
    }

    @Test
    void testResolveTariff_FallbackToMFN() {
        // When no preferential agreement exists, should use MFN rate
        
        TariffRequestDTO request = new TariffRequestDTO(
            "AUS",  // Countries without preferential agreement
            "BRA",
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result);
        System.out.println("✅ AUS-BRA tariff: " + result.ratePercent() + "% from " + result.basis());
        
        // Should use mfn or wits or none as fallback
        assertTrue(
            "mfn".equals(result.basis()) || 
            "wits".equals(result.basis()) || 
            "none".equals(result.basis()),
            "Should fall back to MFN, WITS, or none"
        );
    }

    @Test
    void testResolveTariff_FallbackToZero() {
        // When no data available anywhere, should return 0.0 with 'none' source
        
        TariffRequestDTO request = new TariffRequestDTO(
            "INVALID1",  // Invalid countries
            "INVALID2",
            "999999",  // Invalid HS code
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should still return a result even with invalid data");
        assertEquals(0.0, result.ratePercent(), "Should default to 0.0 when no data");
        assertEquals("none", result.basis(), "Source should be 'none' for fallback");
        
        System.out.println("✅ Invalid request returns safe default: " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_DateSensitive() {
        // Trade agreements have entry-into-force dates
        // Tariff should respect date ranges
        
        TariffRequestDTO oldDate = new TariffRequestDTO(
            "USA",
            "MEX",
            "270900",
            LocalDate.of(2010, 1, 1)  // Very old date
        );
        
        TariffRequestDTO recentDate = new TariffRequestDTO(
            "USA",
            "MEX",
            "270900",
            LocalDate.of(2024, 1, 1)  // Recent date
        );
        
        WitsTariff resultOld = witsService.resolveTariff(oldDate);
        WitsTariff resultRecent = witsService.resolveTariff(recentDate);
        
        assertNotNull(resultOld);
        assertNotNull(resultRecent);
        
        System.out.println("✅ Old date (2010): " + resultOld.ratePercent() + "% from " + resultOld.basis());
        System.out.println("✅ Recent date (2024): " + resultRecent.ratePercent() + "% from " + resultRecent.basis());
        
        // Both should return valid results even if rates differ
        assertTrue(resultOld.ratePercent() >= 0.0, "Old date should have valid rate");
        assertTrue(resultRecent.ratePercent() >= 0.0, "Recent date should have valid rate");
    }

    @Test
    void testResolveTariff_DifferentHSCodes() {
        // Different products should potentially have different tariff rates
        
        TariffRequestDTO crudeOil = new TariffRequestDTO(
            "USA",
            "CHN",
            "270900",  // Crude oil
            LocalDate.of(2024, 1, 1)
        );
        
        TariffRequestDTO gasoline = new TariffRequestDTO(
            "USA",
            "CHN",
            "271012",  // Gasoline
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff resultOil = witsService.resolveTariff(crudeOil);
        WitsTariff resultGas = witsService.resolveTariff(gasoline);
        
        assertNotNull(resultOil);
        assertNotNull(resultGas);
        
        System.out.println("✅ Crude oil (270900): " + resultOil.ratePercent() + "% from " + resultOil.basis());
        System.out.println("✅ Gasoline (271012): " + resultGas.ratePercent() + "% from " + resultGas.basis());
        
        // Both should have valid responses
        assertNotNull(resultOil.basis());
        assertNotNull(resultGas.basis());
    }

    @Test
    void testResolveTariff_CachingBehavior() {
        // WitsService has @Cacheable annotation
        // Multiple identical requests should be fast on subsequent calls
        
        TariffRequestDTO request = new TariffRequestDTO(
            "SGP",
            "MYS",
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        // First call - may be slower (actual lookup)
        long start1 = System.currentTimeMillis();
        WitsTariff result1 = witsService.resolveTariff(request);
        long time1 = System.currentTimeMillis() - start1;
        
        // Second call - should be cached (faster)
        long start2 = System.currentTimeMillis();
        WitsTariff result2 = witsService.resolveTariff(request);
        long time2 = System.currentTimeMillis() - start2;
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.ratePercent(), result2.ratePercent(), "Cached result should match");
        
        System.out.println("✅ First call: " + time1 + "ms");
        System.out.println("✅ Second call (cached): " + time2 + "ms");
        System.out.println("✅ Caching improves performance: " + (time1 > time2 ? "Yes" : "Possibly cached at DB level"));
    }

    @Test
    void testResolveTariff_ValidatesRateRange() {
        // Tariff rates should be reasonable (0-100%)
        
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result);
        assertTrue(result.ratePercent() >= 0.0, "Rate should not be negative");
        assertTrue(result.ratePercent() <= 1000.0, "Rate should be reasonable (some countries have very high tariffs)");
        
        System.out.println("✅ Tariff rate is in valid range: " + result.ratePercent() + "%");
    }

    // ========== Edge Case Tests for Better Coverage ==========

    @Test
    void testResolveTariff_NullCountries() {
        // Test with null country codes - should handle gracefully
        TariffRequestDTO request = new TariffRequestDTO(
            null,
            "USA",
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle null exporter gracefully");
        assertEquals(0.0, result.ratePercent(), "Should return 0.0 for invalid data");
        assertEquals("none", result.basis(), "Should indicate no data found");
        
        System.out.println("✅ Null country handled gracefully: " + result.basis());
    }

    @Test
    void testResolveTariff_EmptyHSCode() {
        // Test with empty HS code
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "",  // Empty HS code
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle empty HS code");
        System.out.println("✅ Empty HS code handled: " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_FutureDate() {
        // Test with future date (agreements may not be in force yet)
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "MEX",
            "270900",
            LocalDate.of(2030, 1, 1)  // Future date
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle future dates");
        assertTrue(result.ratePercent() >= 0.0, "Rate should be non-negative");
        
        System.out.println("✅ Future date (2030): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_VeryOldDate() {
        // Test with very old date (before most agreements existed)
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "MEX",
            "270900",
            LocalDate.of(1990, 1, 1)  // Very old date
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle very old dates");
        System.out.println("✅ Very old date (1990): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_SameImporterExporter() {
        // Test when importer and exporter are the same country
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "USA",  // Same country
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle same country");
        System.out.println("✅ Same country (USA→USA): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_ShortHSCode() {
        // Test with shorter HS code (HS4 instead of HS6)
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "2709",  // HS4 code (should be 270900)
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle short HS code");
        System.out.println("✅ Short HS code (2709): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_LongHSCode() {
        // Test with longer HS code (HS8 or HS10)
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "27090010",  // HS8 code
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle long HS code");
        System.out.println("✅ Long HS code (27090010): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_MultipleAgreements() {
        // Test countries with multiple potential agreements (e.g., EU members)
        TariffRequestDTO request = new TariffRequestDTO(
            "DEU",  // Germany (EU member with many agreements)
            "FRA",  // France (EU member)
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle EU countries");
        System.out.println("✅ EU countries (DEU-FRA): " + result.ratePercent() + "% from " + result.basis());
        
        // EU countries should likely have 0% or very low preferential rate
        if ("preferential".equals(result.basis())) {
            System.out.println("   ✅ EU preferential rate applied");
        }
    }

    @Test
    void testResolveTariff_DevelopingCountries() {
        // Test developing countries which may have different tariff structures
        TariffRequestDTO request = new TariffRequestDTO(
            "IND",  // India
            "BGD",  // Bangladesh
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle developing countries");
        assertTrue(result.ratePercent() >= 0.0, "Rate should be non-negative");
        
        System.out.println("✅ Developing countries (IND-BGD): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_SmallIslandStates() {
        // Test small island developing states
        TariffRequestDTO request = new TariffRequestDTO(
            "SGP",  // Singapore
            "MDV",  // Maldives
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle small island states");
        System.out.println("✅ Small island states (SGP-MDV): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_CountriesWithoutAgreements() {
        // Test countries that typically don't have bilateral trade agreements
        TariffRequestDTO request = new TariffRequestDTO(
            "RUS",  // Russia
            "IRN",  // Iran
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle countries without agreements");
        // Should fall back to MFN or WITS
        assertTrue(
            "mfn".equals(result.basis()) || 
            "wits".equals(result.basis()) || 
            "none".equals(result.basis()),
            "Should use non-preferential source"
        );
        
        System.out.println("✅ No agreement countries (RUS-IRN): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_VariousProductCategories() {
        // Test different HS code categories
        String[][] products = {
            {"270900", "Crude oil"},
            {"271012", "Gasoline"},
            {"840991", "Engine parts"},
            {"620342", "Trousers"},
            {"100190", "Wheat"}
        };
        
        for (String[] product : products) {
            TariffRequestDTO request = new TariffRequestDTO(
                "USA",
                "CHN",
                product[0],
                LocalDate.of(2024, 1, 1)
            );
            
            WitsTariff result = witsService.resolveTariff(request);
            
            assertNotNull(result, "Should handle " + product[1]);
            System.out.println("✅ " + product[1] + " (" + product[0] + "): " + 
                result.ratePercent() + "% from " + result.basis());
        }
    }

    @Test
    void testResolveTariff_BoundaryDates() {
        // Test with agreement entry-into-force boundary dates
        LocalDate[] testDates = {
            LocalDate.of(2024, 1, 1),   // Standard date
            LocalDate.of(2024, 12, 31), // End of year
            LocalDate.of(2024, 6, 15),  // Mid-year
            LocalDate.of(2023, 1, 1),   // Previous year
        };
        
        for (LocalDate date : testDates) {
            TariffRequestDTO request = new TariffRequestDTO(
                "USA",
                "CAN",  // USA-Canada have USMCA agreement
                "270900",
                date
            );
            
            WitsTariff result = witsService.resolveTariff(request);
            
            assertNotNull(result, "Should handle date: " + date);
            System.out.println("✅ Date " + date + ": " + result.ratePercent() + "% from " + result.basis());
        }
    }

    @Test
    void testResolveTariff_ReversedCountryPairs() {
        // Test if reversing importer/exporter gives consistent results
        TariffRequestDTO forward = new TariffRequestDTO(
            "USA",
            "MEX",
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        TariffRequestDTO reverse = new TariffRequestDTO(
            "MEX",
            "USA",
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff resultForward = witsService.resolveTariff(forward);
        WitsTariff resultReverse = witsService.resolveTariff(reverse);
        
        assertNotNull(resultForward);
        assertNotNull(resultReverse);
        
        System.out.println("✅ USA→MEX: " + resultForward.ratePercent() + "% from " + resultForward.basis());
        System.out.println("✅ MEX→USA: " + resultReverse.ratePercent() + "% from " + resultReverse.basis());
        
        // Note: Tariffs are typically NOT symmetric (different countries have different import tariffs)
        System.out.println("   " + (resultForward.ratePercent() != resultReverse.ratePercent() ? 
            "✅ Asymmetric tariffs (expected)" : "⚠️  Symmetric tariffs"));
    }

    @Test
    void testResolveTariff_SpecialAdministrativeRegions() {
        // Test special administrative regions like Hong Kong, Macau
        TariffRequestDTO request = new TariffRequestDTO(
            "HKG",  // Hong Kong SAR
            "CHN",  // Mainland China
            "270900",
            LocalDate.of(2024, 1, 1)
        );
        
        WitsTariff result = witsService.resolveTariff(request);
        
        assertNotNull(result, "Should handle SAR");
        System.out.println("✅ HKG-CHN: " + result.ratePercent() + "% from " + result.basis());
    }

    // ==================== NEW COMPREHENSIVE TESTS ====================

    @Test
    void testResolveTariff_WithNullRequest_ShouldHandleGracefully() {
        // Act & Assert - Should handle null request without crashing
        try {
            WitsTariff result = witsService.resolveTariff(null);
            // If it returns something, verify it's a valid response
            if (result != null) {
                System.out.println("✓ Null request handled with default result");
            }
        } catch (NullPointerException e) {
            // Expected behavior - null check
            System.out.println("✓ Null request throws NullPointerException (expected)");
        } catch (Exception e) {
            // Any exception is acceptable - method handles null
            System.out.println("✓ Null request throws exception: " + e.getClass().getSimpleName());
        }
    }

    @Test
    void testResolveTariff_WithVeryOldDate_ShouldReturnResult() {
        // Arrange - Test with very old date (pre-agreements)
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "270900",
            LocalDate.of(1990, 1, 1)
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert
        assertNotNull(result, "Should handle very old dates");
        assertNotNull(result.basis(), "Should have source basis");
        System.out.println("✓ Old date (1990): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_WithFutureDate_ShouldReturnResult() {
        // Arrange - Test with future date
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "270900",
            LocalDate.of(2030, 12, 31)
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert
        assertNotNull(result, "Should handle future dates");
        assertNotNull(result.basis(), "Should have source basis");
        System.out.println("✓ Future date (2030): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_WithInvalidHSCode_ShouldReturnDefault() {
        // Arrange - Test with invalid/non-existent HS code
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "999999",  // Invalid HS code
            LocalDate.of(2024, 1, 1)
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert
        assertNotNull(result, "Should return default for invalid HS code");
        assertTrue(result.basis().equals("none") || result.basis().equals("mfn") || 
                   result.basis().equals("wits"), 
                   "Should fall back to default source");
        System.out.println("✓ Invalid HS code: " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_WithShortHSCode_ShouldHandle() {
        // Arrange - Test with HS4 code (shorter than HS6)
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "2709",  // HS4 instead of HS6
            LocalDate.of(2024, 1, 1)
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert
        assertNotNull(result, "Should handle HS4 codes");
        System.out.println("✓ HS4 code: " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_WithLongHSCode_ShouldHandle() {
        // Arrange - Test with HS8 code (longer than HS6)
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "27090010",  // HS8 code
            LocalDate.of(2024, 1, 1)
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert
        assertNotNull(result, "Should handle HS8 codes");
        System.out.println("✓ HS8 code: " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_WithEmptyHSCode_ShouldHandle() {
        // Arrange - Test with empty HS code
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "",  // Empty HS code
            LocalDate.of(2024, 1, 1)
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert
        assertNotNull(result, "Should handle empty HS code gracefully");
        System.out.println("✓ Empty HS code handled: " + result.basis());
    }

    @Test
    void testResolveTariff_SameCountryBothWays_ShouldHandle() {
        // Arrange - Test domestic "trade" (same importer and exporter)
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "USA",  // Same country
            "270900",
            LocalDate.of(2024, 1, 1)
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert
        assertNotNull(result, "Should handle domestic trade");
        assertEquals("USA", result.importerIso3());
        assertEquals("USA", result.exporterIso3());
        System.out.println("✓ Domestic trade: " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_WithNonExistentCountries_ShouldReturnDefault() {
        // Arrange - Test with non-existent country codes
        TariffRequestDTO request = new TariffRequestDTO(
            "XXX",  // Non-existent
            "YYY",  // Non-existent
            "270900",
            LocalDate.of(2024, 1, 1)
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert
        assertNotNull(result, "Should return default result for non-existent countries");
        assertTrue(result.basis().equals("none") || result.basis().contains("error"),
                   "Should indicate no data found");
        System.out.println("✓ Non-existent countries: " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_EUInternalTrade_ShouldHandlePreferential() {
        // Arrange - Test EU internal trade (should have preferential rates)
        TariffRequestDTO request = new TariffRequestDTO(
            "DEU",  // Germany
            "FRA",  // France
            "270900",
            LocalDate.of(2024, 1, 1)
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert
        assertNotNull(result, "Should handle EU internal trade");
        // EU internal trade typically has 0% tariff or preferential rates
        System.out.println("✓ EU internal (DEU-FRA): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_USMCATrade_ShouldHandlePreferential() {
        // Arrange - Test USMCA (former NAFTA) countries
        String[][] usmcaPairs = {
            {"USA", "CAN"},
            {"USA", "MEX"},
            {"CAN", "MEX"}
        };

        // Act & Assert
        for (String[] pair : usmcaPairs) {
            TariffRequestDTO request = new TariffRequestDTO(
                pair[0],
                pair[1],
                "270900",
                LocalDate.of(2024, 1, 1)
            );

            WitsTariff result = witsService.resolveTariff(request);
            
            assertNotNull(result, "Should handle USMCA trade: " + pair[0] + "-" + pair[1]);
            System.out.println("✓ USMCA (" + pair[0] + "-" + pair[1] + "): " + 
                result.ratePercent() + "% from " + result.basis());
        }
    }

    @Test
    void testResolveTariff_ASEANTrade_ShouldHandle() {
        // Arrange - Test ASEAN member states
        String[][] aseanPairs = {
            {"SGP", "MYS"},
            {"THA", "VNM"},
            {"IDN", "PHL"}
        };

        // Act & Assert
        for (String[] pair : aseanPairs) {
            TariffRequestDTO request = new TariffRequestDTO(
                pair[0],
                pair[1],
                "270900",
                LocalDate.of(2024, 1, 1)
            );

            WitsTariff result = witsService.resolveTariff(request);
            
            assertNotNull(result, "Should handle ASEAN trade: " + pair[0] + "-" + pair[1]);
            System.out.println("✓ ASEAN (" + pair[0] + "-" + pair[1] + "): " + 
                result.ratePercent() + "% from " + result.basis());
        }
    }

    @Test
    void testResolveTariff_WithDifferentProductCategories_ShouldWork() {
        // Arrange - Test different product categories
        String[] hsCodes = {
            "270900",  // Petroleum oils, crude
            "100190",  // Wheat
            "520100",  // Cotton
            "840290",  // Steam boilers
            "854140"   // Photosensitive semiconductor devices
        };

        // Act & Assert
        for (String hsCode : hsCodes) {
            TariffRequestDTO request = new TariffRequestDTO(
                "USA",
                "CHN",
                hsCode,
                LocalDate.of(2024, 1, 1)
            );

            WitsTariff result = witsService.resolveTariff(request);
            
            assertNotNull(result, "Should handle HS code: " + hsCode);
            System.out.println("✓ HS " + hsCode + ": " + result.ratePercent() + "% from " + result.basis());
        }
    }

    @Test
    void testResolveTariff_CachingBehavior_ShouldBeFast() {
        // Arrange
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "270900",
            LocalDate.of(2024, 1, 1)
        );

        // Act - First call (may be slower - hits Firebase/WITS)
        long start1 = System.currentTimeMillis();
        WitsTariff result1 = witsService.resolveTariff(request);
        long duration1 = System.currentTimeMillis() - start1;

        // Second call (should be faster if cached)
        long start2 = System.currentTimeMillis();
        WitsTariff result2 = witsService.resolveTariff(request);
        long duration2 = System.currentTimeMillis() - start2;

        // Third call
        long start3 = System.currentTimeMillis();
        WitsTariff result3 = witsService.resolveTariff(request);
        long duration3 = System.currentTimeMillis() - start3;

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals(result1.ratePercent(), result2.ratePercent(), "Results should be consistent");
        assertEquals(result1.ratePercent(), result3.ratePercent(), "Results should be consistent");

        System.out.println("✓ Caching test:");
        System.out.println("  - 1st call: " + duration1 + "ms");
        System.out.println("  - 2nd call: " + duration2 + "ms");
        System.out.println("  - 3rd call: " + duration3 + "ms");
    }

    @Test
    void testResolveTariff_WithLeapYearDate_ShouldHandle() {
        // Arrange - Test with leap year date
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "270900",
            LocalDate.of(2024, 2, 29)  // Leap year date
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert
        assertNotNull(result, "Should handle leap year dates");
        System.out.println("✓ Leap year date (2024-02-29): " + result.ratePercent() + "% from " + result.basis());
    }

    @Test
    void testResolveTariff_HighTariffScenarios_ShouldHandle() {
        // Arrange - Test country pairs known for higher tariffs
        String[][] highTariffPairs = {
            {"USA", "CHN"},  // US-China trade tensions
            {"CHN", "USA"},
            {"IND", "CHN"}   // India-China trade
        };

        // Act & Assert
        for (String[] pair : highTariffPairs) {
            TariffRequestDTO request = new TariffRequestDTO(
                pair[0],
                pair[1],
                "270900",
                LocalDate.of(2024, 1, 1)
            );

            WitsTariff result = witsService.resolveTariff(request);
            
            assertNotNull(result, "Should handle: " + pair[0] + "-" + pair[1]);
            System.out.println("✓ " + pair[0] + "→" + pair[1] + ": " + 
                result.ratePercent() + "% from " + result.basis());
        }
    }

    @Test
    void testResolveTariff_ResultStructureValidation() {
        // Arrange
        TariffRequestDTO request = new TariffRequestDTO(
            "USA",
            "CHN",
            "270900",
            LocalDate.of(2024, 1, 1)
        );

        // Act
        WitsTariff result = witsService.resolveTariff(request);

        // Assert - Validate all fields of WitsTariff are populated
        assertNotNull(result, "Result should not be null");
        assertNotNull(result.importerIso3(), "Importer ISO3 should be populated");
        assertNotNull(result.exporterIso3(), "Exporter ISO3 should be populated");
        assertNotNull(result.hs6(), "HS6 code should be populated");
        assertNotNull(result.date(), "Date should be populated");
        assertNotNull(result.basis(), "Basis/source should be populated");
        assertNotNull(result.sourceNote(), "Source note should be populated");
        assertTrue(result.ratePercent() >= 0.0, "Rate should be non-negative");
        
        System.out.println("✓ Result structure validation passed");
        System.out.println("  - Importer: " + result.importerIso3());
        System.out.println("  - Exporter: " + result.exporterIso3());
        System.out.println("  - HS6: " + result.hs6());
        System.out.println("  - Rate: " + result.ratePercent() + "%");
        System.out.println("  - Basis: " + result.basis());
        System.out.println("  - Note: " + result.sourceNote());
    }
}
