package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.models.Country;
import com.cs203.grp2.Asg2.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryControllerTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setCode("840");
        testCountry.setISO3("USA");
        testCountry.setName("United States");
    }

    @Test
    void testGetAllCountries_ShouldReturnListOfCountries() throws Exception {
        // Arrange
        Country country1 = new Country();
        country1.setCode("840");
        country1.setISO3("USA");
        country1.setName("United States");

        Country country2 = new Country();
        country2.setCode("156");
        country2.setISO3("CHN");
        country2.setName("China");

        List<Country> countries = Arrays.asList(country1, country2);
        when(countryService.getAll()).thenReturn(countries);

        // Act
        List<Country> result = countryController.getAllCountries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("United States", result.get(0).getName());
        assertEquals("China", result.get(1).getName());
        verify(countryService).getAll();
    }

    @Test
    void testGetAllCountries_WhenExceptionThrown_ShouldReturnNull() throws Exception {
        // Arrange
        when(countryService.getAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        // The controller now throws CountryNotFoundException instead of returning null
        assertThrows(com.cs203.grp2.Asg2.exceptions.CountryNotFoundException.class, () -> {
            countryController.getAllCountries();
        });
        verify(countryService).getAll();
    }

    @Test
    void testGetCountryByCode_WithValidCode_ShouldReturnCountry() {
        // Arrange
        when(countryService.getCountryByCode("840")).thenReturn(testCountry);

        // Act
        Country result = countryController.getCountryByCode(840);

        // Assert
        assertNotNull(result);
        assertEquals("840", result.getCode());
        assertEquals("USA", result.getISO3());
        assertEquals("United States", result.getName());
        verify(countryService).getCountryByCode("840");
    }

    @Test
    void testGetCountryByCode_WithSingleDigitCode_ShouldConvertToString() {
        // Arrange
        Country country = new Country();
        country.setCode("4");
        country.setName("Afghanistan");
        
        when(countryService.getCountryByCode("4")).thenReturn(country);

        // Act
        Country result = countryController.getCountryByCode(4);

        // Assert
        assertNotNull(result);
        assertEquals("4", result.getCode());
        verify(countryService).getCountryByCode("4");
    }

    @Test
    void testGetCountryByCode_WithMaxValue_ShouldReturnCountry() {
        // Arrange
        Country country = new Country();
        country.setCode("999");
        
        when(countryService.getCountryByCode("999")).thenReturn(country);

        // Act
        Country result = countryController.getCountryByCode(999);

        // Assert
        assertNotNull(result);
        assertEquals("999", result.getCode());
        verify(countryService).getCountryByCode("999");
    }

    @Test
    void testGetCountryByCode_WithMinValue_ShouldReturnCountry() {
        // Arrange
        Country country = new Country();
        country.setCode("1");
        
        when(countryService.getCountryByCode("1")).thenReturn(country);

        // Act
        Country result = countryController.getCountryByCode(1);

        // Assert
        assertNotNull(result);
        verify(countryService).getCountryByCode("1");
    }

    @Test
    void testGetAllCountries_WhenServiceReturnsEmptyList_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(countryService.getAll()).thenReturn(Arrays.asList());

        // Act
        List<Country> result = countryController.getAllCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(countryService).getAll();
    }

    @Test
    void testGetVatRateForCountryAndDate_WithValidData_ShouldReturnVATRate() {
        // Arrange
        Country country = new Country();
        country.setName("Singapore");
        
        com.cs203.grp2.Asg2.models.VATRate rate1 = new com.cs203.grp2.Asg2.models.VATRate(
            java.time.LocalDate.of(2020, 1, 1), 7.0);
        com.cs203.grp2.Asg2.models.VATRate rate2 = new com.cs203.grp2.Asg2.models.VATRate(
            java.time.LocalDate.of(2023, 1, 1), 8.0);
        
        country.setVatRates(Arrays.asList(rate1, rate2));
        
        when(countryService.getCountryByName("Singapore")).thenReturn(country);

        // Act
        com.cs203.grp2.Asg2.models.VATRate result = 
            countryController.getVatRateForCountryAndDate("Singapore", "2024-01-01");

        // Assert
        assertNotNull(result);
        assertEquals(8.0, result.getVATRate());
        assertEquals(java.time.LocalDate.of(2023, 1, 1), result.getDate());
        verify(countryService).getCountryByName("Singapore");
    }

    @Test
    void testGetVatRateForCountryAndDate_WithNoVATRates_ShouldThrowException() {
        // Arrange
        Country country = new Country();
        country.setName("Singapore");
        country.setVatRates(Arrays.asList());
        
        when(countryService.getCountryByName("Singapore")).thenReturn(country);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            countryController.getVatRateForCountryAndDate("Singapore", "2024-01-01");
        });
        
        assertTrue(exception.getMessage().contains("No VAT rates found for country"));
        verify(countryService).getCountryByName("Singapore");
    }

    @Test
    void testGetVatRateForCountryAndDate_WithNullVATRates_ShouldThrowException() {
        // Arrange
        Country country = new Country();
        country.setName("Singapore");
        country.setVatRates(null);
        
        when(countryService.getCountryByName("Singapore")).thenReturn(country);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            countryController.getVatRateForCountryAndDate("Singapore", "2024-01-01");
        });
        
        assertTrue(exception.getMessage().contains("No VAT rates found for country"));
        verify(countryService).getCountryByName("Singapore");
    }

    @Test
    void testGetVatRateForCountryAndDate_NoRateBeforeDate_ShouldThrowException() {
        // Arrange
        Country country = new Country();
        country.setName("Singapore");
        
        com.cs203.grp2.Asg2.models.VATRate rate1 = new com.cs203.grp2.Asg2.models.VATRate(
            java.time.LocalDate.of(2025, 1, 1), 7.0);
        
        country.setVatRates(Arrays.asList(rate1));
        
        when(countryService.getCountryByName("Singapore")).thenReturn(country);

        // Act & Assert
        // The controller now throws CountryNotFoundException instead of RuntimeException
        com.cs203.grp2.Asg2.exceptions.CountryNotFoundException exception = assertThrows(
            com.cs203.grp2.Asg2.exceptions.CountryNotFoundException.class, () -> {
            countryController.getVatRateForCountryAndDate("Singapore", "2020-01-01");
        });
        
        assertTrue(exception.getMessage().contains("No VAT rate found"));
        verify(countryService).getCountryByName("Singapore");
    }

    @Test
    void testGetVatRateForCountryAndDate_WithExactDate_ShouldReturnRate() {
        // Arrange
        Country country = new Country();
        country.setName("Singapore");
        
        com.cs203.grp2.Asg2.models.VATRate rate = new com.cs203.grp2.Asg2.models.VATRate(
            java.time.LocalDate.of(2023, 1, 1), 9.0);
        
        country.setVatRates(Arrays.asList(rate));
        
        when(countryService.getCountryByName("Singapore")).thenReturn(country);

        // Act
        com.cs203.grp2.Asg2.models.VATRate result = 
            countryController.getVatRateForCountryAndDate("Singapore", "2023-01-01");

        // Assert
        assertNotNull(result);
        assertEquals(9.0, result.getVATRate());
        verify(countryService).getCountryByName("Singapore");
    }
}
