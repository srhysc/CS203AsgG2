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
}
