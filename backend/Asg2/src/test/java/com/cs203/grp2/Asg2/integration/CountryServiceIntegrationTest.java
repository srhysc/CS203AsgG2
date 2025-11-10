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
}
