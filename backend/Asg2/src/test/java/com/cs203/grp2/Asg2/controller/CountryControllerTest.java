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

        // Act
        List<Country> result = countryController.getAllCountries();

        // Assert
        assertNull(result);
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
}
