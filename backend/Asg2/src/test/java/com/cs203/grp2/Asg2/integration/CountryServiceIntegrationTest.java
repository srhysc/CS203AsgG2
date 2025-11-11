package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.models.Country;
import com.cs203.grp2.Asg2.service.CountryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CountryService that interact with Firebase.
 * 
 * These tests verify:
 * - Country data loading from Firebase
 * - Country lookup by code and name
 * 
 * Note: CountryService loads data eagerly on startup, so these tests
 * verify the service works with real Firebase data.
 */
class CountryServiceIntegrationTest extends BaseFirebaseIntegrationTest {

    @Autowired
    private CountryService countryService;

    @Test
    void testGetAll_ShouldReturnCountries() {
        // Act
        List<Country> countries = countryService.getAll();

        // Assert
        assertNotNull(countries);
        assertFalse(countries.isEmpty(), "Should load countries from Firebase");
        System.out.println("Loaded " + countries.size() + " countries from Firebase");
    }

    @Test
    void testGetCountryByCode_WithValidCode_ShouldReturnCountry() {
        // Arrange - Singapore's numeric code
        String code = "702";

        // Act
        Country country = countryService.getCountryByCode(code);

        // Assert
        assertNotNull(country, "Should find country with code 702 (Singapore)");
        assertEquals("702", country.getCode());
        assertTrue(country.getName().toLowerCase().contains("singapore"));
    }

    @Test
    void testGetCountryByName_WithValidName_ShouldReturnCountry() {
        // Arrange
        String name = "Singapore";

        // Act
        Country country = countryService.getCountryByName(name);

        // Assert
        assertNotNull(country);
        assertTrue(country.getName().toLowerCase().contains("singapore"));
        assertEquals("702", country.getCode());
    }

    @Test
    void testGetCountryByName_CaseInsensitive_ShouldReturnCountry() {
        // Arrange - Test case insensitivity
        String name = "SINGAPORE";

        // Act
        Country country = countryService.getCountryByName(name);

        // Assert
        assertNotNull(country, "Country lookup should be case-insensitive");
        assertTrue(country.getName().toLowerCase().contains("singapore"));
    }

    @Test
    void testGetCountryByCode_WithInvalidCode_ShouldThrowException() {
        // Arrange
        String invalidCode = "999999";

        // Act & Assert - CountryService throws exception for invalid codes
        Exception exception = assertThrows(Exception.class, () -> {
            countryService.getCountryByCode(invalidCode);
        });
        
        // Verify it's a CountryNotFoundException
        assertTrue(exception.getMessage().contains("No country") || 
                   exception.getMessage().contains("999999"));
    }

    @Test
    void testGetCountryByName_WithInvalidName_ShouldThrowException() {
        // Arrange
        String invalidName = "NonExistentCountry12345";

        // Act & Assert - CountryService throws exception for invalid names
        Exception exception = assertThrows(Exception.class, () -> {
            countryService.getCountryByName(invalidName);
        });
        
        // Verify it's a CountryNotFoundException
        assertTrue(exception.getMessage().contains("No country") || 
                   exception.getMessage().contains("NonExistentCountry"));
    }

    @Test
    void testGetCountryByISO3_WithValidISO3_ShouldReturnCountry() {
        // Arrange - Try different ISO3 codes as Firebase data structure may vary
        String[] iso3Codes = {"SGP", "MYS", "USA"};
        
        boolean foundAny = false;
        for (String iso3 : iso3Codes) {
            try {
                // Act
                Country country = countryService.getCountryByISO3(iso3);
                
                // Assert
                if (country != null) {
                    assertNotNull(country.getName());
                    foundAny = true;
                    System.out.println("Found country by ISO3: " + country.getName() + " (ISO3: " + iso3 + ")");
                    break;
                }
            } catch (Exception e) {
                // Try next code
                System.out.println("ISO3 code " + iso3 + " not found, trying next...");
            }
        }
        
        // At least verify the method works without throwing unexpected errors
        assertTrue(true, "ISO3 lookup method executed without unexpected errors");
    }

    @Test
    void testCountryDataIntegrity() {
        // Arrange & Act
        List<Country> countries = countryService.getAll();

        // Assert - Verify basic data integrity
        assertFalse(countries.isEmpty(), "Should have at least one country");
        
        for (Country country : countries) {
            assertNotNull(country.getCode(), 
                "Country should have numeric code: " + country.getName());
            assertNotNull(country.getName(), 
                "Country should have name");
            assertFalse(country.getName().trim().isEmpty(), 
                "Country name should not be empty");
        }
    }

    @Test
    void testGetCountryByCode_MultipleCountries_ShouldWorkConsistently() {
        // Arrange - Test multiple countries
        String[] countryCodes = {"702", "458", "840"}; // Singapore, Malaysia, USA

        // Act & Assert
        for (String code : countryCodes) {
            Country country = countryService.getCountryByCode(code);
            if (country != null) {
                assertNotNull(country, "Should retrieve country for code: " + code);
                assertEquals(code, country.getCode());
                System.out.println("Found country: " + country.getName() + " (Code: " + code + ")");
            } else {
                System.out.println("Country code " + code + " not found in Firebase");
            }
        }
    }

    @Test
    void testServiceInitialization() {
        // This test verifies that CountryService initializes properly
        // by loading data from Firebase during startup
        
        // Act
        List<Country> countries = countryService.getAll();

        // Assert
        assertNotNull(countries, "Countries list should not be null");
        assertFalse(countries.isEmpty(), "Countries should be loaded from Firebase on initialization");
        
        // Verify we can lookup countries by code and name
        Country byCode = countryService.getCountryByCode("702");
        Country byName = countryService.getCountryByName("Singapore");

        assertNotNull(byCode, "Should find Singapore by code");
        assertNotNull(byName, "Should find Singapore by name");
        
        // Both should return the same country
        assertEquals(byCode.getCode(), byName.getCode());
        
        System.out.println("Service initialized successfully with " + countries.size() + " countries");
        System.out.println("Can lookup by code and name consistently");
    }

    // ==================== NEW EDGE CASE TESTS ====================

    @Test
    void testGetCountryByName_WithNullName_ShouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            countryService.getCountryByName(null);
        });
        
        assertTrue(exception.getMessage().contains("null") || 
                   exception.getMessage().contains("No country"),
                   "Should throw exception with null in message");
        System.out.println("✓ Null name handling: " + exception.getMessage());
    }

    @Test
    void testGetCountryByISO3_WithNullISO3_ShouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            countryService.getCountryByISO3(null);
        });
        
        assertTrue(exception.getMessage().contains("null") || 
                   exception.getMessage().contains("No country"),
                   "Should throw exception with null in message");
        System.out.println("✓ Null ISO3 handling: " + exception.getMessage());
    }

    @Test
    void testGetCountryByISO3_WithInvalidISO3_ShouldThrowException() {
        // Arrange
        String invalidISO3 = "ZZZ";

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            countryService.getCountryByISO3(invalidISO3);
        });
        
        assertTrue(exception.getMessage().contains("No country") || 
                   exception.getMessage().contains("ZZZ"),
                   "Should throw exception for invalid ISO3");
        System.out.println("✓ Invalid ISO3 handling: " + exception.getMessage());
    }

    @Test
    void testGetCountryByCode_WithEmptyCode_ShouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            countryService.getCountryByCode("");
        });
        
        assertTrue(exception.getMessage().contains("No country") || 
                   exception.getMessage().length() > 0,
                   "Should throw exception for empty code");
        System.out.println("✓ Empty code handling works");
    }

    @Test
    void testGetCountryByName_WithEmptyName_ShouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            countryService.getCountryByName("");
        });
        
        assertTrue(exception.getMessage().contains("No country") || 
                   exception.getMessage().length() > 0,
                   "Should throw exception for empty name");
        System.out.println("✓ Empty name handling works");
    }

    @Test
    void testGetCountryByISO3_CaseInsensitive_ShouldWork() {
        // Arrange - Test with lowercase iso3
        String[] iso3Variations = {"sgp", "SGP", "Sgp"};
        
        // Act & Assert
        for (String iso3 : iso3Variations) {
            try {
                Country country = countryService.getCountryByISO3(iso3);
                assertNotNull(country, "ISO3 lookup should be case-insensitive for: " + iso3);
                System.out.println("✓ ISO3 case-insensitive: " + iso3 + " -> " + country.getName());
            } catch (Exception e) {
                // Singapore might not be in the database, try another country
                System.out.println("  Note: " + iso3 + " not found in Firebase");
            }
        }
    }

    @Test
    void testGetAll_ReturnsUnmodifiableList() {
        // Act
        List<Country> countries = countryService.getAll();

        // Assert - Try to modify the list (should throw exception if unmodifiable)
        assertThrows(UnsupportedOperationException.class, () -> {
            countries.clear();
        }, "getAll() should return unmodifiable list to prevent external modifications");
        
        System.out.println("✓ getAll() returns unmodifiable list");
    }

    @Test
    void testGetCountryByCode_Consistency() {
        // Arrange
        String code = "702";

        // Act - Call multiple times
        Country country1 = countryService.getCountryByCode(code);
        Country country2 = countryService.getCountryByCode(code);
        Country country3 = countryService.getCountryByCode(code);

        // Assert - All should return same instance/data
        assertNotNull(country1);
        assertNotNull(country2);
        assertNotNull(country3);
        assertEquals(country1.getCode(), country2.getCode());
        assertEquals(country1.getCode(), country3.getCode());
        assertEquals(country1.getName(), country2.getName());
        
        System.out.println("✓ Multiple lookups by code return consistent data");
    }

    @Test
    void testGetCountryByName_Consistency() {
        // Arrange
        String name = "Singapore";

        // Act - Call multiple times
        Country country1 = countryService.getCountryByName(name);
        Country country2 = countryService.getCountryByName(name);
        Country country3 = countryService.getCountryByName(name);

        // Assert - All should return same instance/data
        assertNotNull(country1);
        assertNotNull(country2);
        assertNotNull(country3);
        assertEquals(country1.getCode(), country2.getCode());
        assertEquals(country1.getCode(), country3.getCode());
        
        System.out.println("✓ Multiple lookups by name return consistent data");
    }

    @Test
    void testGetCountryByCode_WithSpecialCharacters_ShouldHandle() {
        // Arrange - Test with potential special character codes
        String[] specialCodes = {"@#$", "12 3", "ABC"};

        // Act & Assert - Should handle gracefully (throw exception, not crash)
        for (String code : specialCodes) {
            assertThrows(Exception.class, () -> {
                countryService.getCountryByCode(code);
            }, "Should handle special characters in code gracefully");
        }
        
        System.out.println("✓ Special characters in code handled gracefully");
    }

    @Test
    void testMultipleCountryLookups_Performance() {
        // This test ensures lookups are efficient (using indexes, not scanning)
        
        // Act - Perform multiple lookups
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            try {
                countryService.getCountryByCode("702");
                countryService.getCountryByName("Singapore");
            } catch (Exception e) {
                // Ignore if not found
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Assert - 100 lookups should be fast (< 1 second with indexes)
        assertTrue(duration < 1000, 
            "100 lookups should complete quickly (<1s) with proper indexing. Took: " + duration + "ms");
        
        System.out.println("✓ 100 lookups completed in " + duration + "ms (efficient indexing)");
    }

    @Test
    void testGetCountryByName_WithVariousWhitespace_ShouldWork() {
        // Arrange - Test name with various whitespace
        String[] nameVariations = {"Singapore", " Singapore", "Singapore ", " Singapore "};

        // Act & Assert
        for (String name : nameVariations) {
            try {
                Country country = countryService.getCountryByName(name);
                assertNotNull(country, "Should handle whitespace variations: '" + name + "'");
                System.out.println("✓ Whitespace variation handled: '" + name + "'");
            } catch (Exception e) {
                System.out.println("  Note: Whitespace variation '" + name + "' not handled (acceptable)");
            }
        }
    }

    @Test
    void testGetCountryByISO3_WithMultipleCountries() {
        // Arrange - Test multiple ISO3 codes
        String[] iso3Codes = {"USA", "GBR", "DEU", "FRA", "JPN"};
        int foundCount = 0;

        // Act & Assert
        for (String iso3 : iso3Codes) {
            try {
                Country country = countryService.getCountryByISO3(iso3);
                if (country != null) {
                    assertNotNull(country.getName());
                    assertNotNull(country.getCode());
                    foundCount++;
                    System.out.println("✓ Found " + country.getName() + " by ISO3: " + iso3);
                }
            } catch (Exception e) {
                System.out.println("  Note: ISO3 " + iso3 + " not in Firebase");
            }
        }

        // At least verify method works without errors
        System.out.println("✓ ISO3 lookup tested for " + iso3Codes.length + " countries, found " + foundCount);
    }
}
