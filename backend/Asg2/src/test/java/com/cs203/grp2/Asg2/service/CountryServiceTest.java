package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.exceptions.CountryNotFoundException;
import com.cs203.grp2.Asg2.models.Country;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CountryService
 * Tests country lookup operations from in-memory cache
 */
@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {

    @Mock
    private FirebaseDatabase firebaseDatabase;

    @Mock
    private DatabaseReference databaseReference;

    @Mock
    private DataSnapshot rootSnapshot;
    
    @Mock
    private DataSnapshot countrySnapshot1;
    
    @Mock
    private DataSnapshot countrySnapshot2;
    
    @Mock
    private DataSnapshot codeSnapshot;
    
    @Mock
    private DataSnapshot iso3Snapshot;
    
    @Mock
    private DataSnapshot vatRatesSnapshot;

    private CountryService countryService;

    private Country testCountry1;
    private Country testCountry2;

    @BeforeEach
    void setUp() {
        // Create test countries
        testCountry1 = new Country();
        testCountry1.setCode("702");
        testCountry1.setName("Singapore");
        testCountry1.setISO3("SGP");
        
        testCountry2 = new Country();
        testCountry2.setCode("458");
        testCountry2.setName("Malaysia");
        testCountry2.setISO3("MYS");

        // Setup Firebase mocks
        when(firebaseDatabase.getReference("/Country_NEW")).thenReturn(databaseReference);
        when(rootSnapshot.exists()).thenReturn(true);
        
        // Mock children for root snapshot
        List<DataSnapshot> children = Arrays.asList(countrySnapshot1, countrySnapshot2);
        when(rootSnapshot.getChildren()).thenReturn(children);
        
        // Mock first country snapshot
        when(countrySnapshot1.getValue(Country.class)).thenReturn(testCountry1);
        when(countrySnapshot1.getKey()).thenReturn("Singapore");
        when(countrySnapshot1.child("code")).thenReturn(codeSnapshot);
        when(countrySnapshot1.child("iso3n")).thenReturn(iso3Snapshot);
        when(countrySnapshot1.child("vat_rates")).thenReturn(vatRatesSnapshot);
        
        // Mock second country snapshot
        when(countrySnapshot2.getValue(Country.class)).thenReturn(testCountry2);
        when(countrySnapshot2.getKey()).thenReturn("Malaysia");
        when(countrySnapshot2.child("code")).thenReturn(codeSnapshot);
        when(countrySnapshot2.child("iso3n")).thenReturn(iso3Snapshot);
        when(countrySnapshot2.child("vat_rates")).thenReturn(vatRatesSnapshot);
        
        // Mock code and iso3 values
        when(codeSnapshot.getValue(String.class)).thenReturn("702", "458");
        when(iso3Snapshot.getValue(String.class)).thenReturn("SGP", "MYS");
        when(vatRatesSnapshot.getChildren()).thenReturn(Arrays.asList());

        // Mock the ValueEventListener callback
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(rootSnapshot);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Create the service - this will call init() and load the mock data
        countryService = new CountryService(firebaseDatabase);
    }

    @Test
    void testGetAll_ReturnsCountryList() {
        // When
        List<Country> countries = countryService.getAll();

        // Then
        assertNotNull(countries);
        assertEquals(2, countries.size());
        assertTrue(countries.stream().anyMatch(c -> "Singapore".equals(c.getName())));
        assertTrue(countries.stream().anyMatch(c -> "Malaysia".equals(c.getName())));
    }

    @Test
    void testGetCountryByCode_Found() {
        // When
        Country country = countryService.getCountryByCode("702");

        // Then
        assertNotNull(country);
        assertEquals("Singapore", country.getName());
        assertEquals("702", country.getCode());
    }

    @Test
    void testGetCountryByCode_NotFound() {
        // When/Then
        assertThrows(CountryNotFoundException.class, () -> {
            countryService.getCountryByCode("999");
        });
    }

    @Test
    void testGetCountryByName_Found() {
        // When
        Country country = countryService.getCountryByName("Singapore");

        // Then
        assertNotNull(country);
        assertEquals("Singapore", country.getName());
        assertEquals("702", country.getCode());
    }

    @Test
    void testGetCountryByName_CaseInsensitive() {
        // When
        Country country = countryService.getCountryByName("SINGAPORE");

        // Then
        assertNotNull(country);
        assertEquals("Singapore", country.getName());
    }

    @Test
    void testGetCountryByName_NotFound() {
        // When/Then
        assertThrows(CountryNotFoundException.class, () -> {
            countryService.getCountryByName("NonExistentCountry");
        });
    }

    @Test
    void testGetCountryByISO3_Found() {
        // When
        Country country = countryService.getCountryByISO3("SGP");

        // Then
        assertNotNull(country);
        assertEquals("Singapore", country.getName());
        assertEquals("SGP", country.getISO3());
    }

    @Test
    void testGetCountryByISO3_NotFound() {
        // When/Then
        assertThrows(CountryNotFoundException.class, () -> {
            countryService.getCountryByISO3("XXX");
        });
    }

    @Test
    void testInit_DatabaseError() {
        // Given - setup a new mock that throws error
        FirebaseDatabase errorFirebase = mock(FirebaseDatabase.class);
        DatabaseReference errorRef = mock(DatabaseReference.class);
        when(errorFirebase.getReference("/Country_NEW")).thenReturn(errorRef);
        
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DatabaseError error = mock(DatabaseError.class);
            when(error.getMessage()).thenReturn("Database connection failed");
            listener.onCancelled(error);
            return null;
        }).when(errorRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            new CountryService(errorFirebase);
        });
        
        assertTrue(exception.getMessage().contains("Failed to load /Country from Firebase"));
    }

    @Test
    void testGetCountryByName_NullParameter() {
        // Given/When/Then - null name should throw CountryNotFoundException
        CountryNotFoundException exception = assertThrows(
            CountryNotFoundException.class,
            () -> countryService.getCountryByName(null)
        );
        
        assertEquals("No country with name=null", exception.getMessage());
    }

    @Test
    void testGetCountryByISO3_NullParameter() {
        // Given/When/Then - null ISO3 should throw CountryNotFoundException
        CountryNotFoundException exception = assertThrows(
            CountryNotFoundException.class,
            () -> countryService.getCountryByISO3(null)
        );
        
        assertEquals("No country with iso3=null", exception.getMessage());
    }

    @Test
    void testGetCountryByISO3_CaseInsensitive() {
        // Given - countries loaded in setUp()
        
        // When/Then - test lowercase
        Country result1 = countryService.getCountryByISO3("sgp");
        assertEquals("Singapore", result1.getName());
        assertEquals("SGP", result1.getISO3());
        
        // When/Then - test mixed case
        Country result2 = countryService.getCountryByISO3("SgP");
        assertEquals("Singapore", result2.getName());
        
        // When/Then - test uppercase
        Country result3 = countryService.getCountryByISO3("MYS");
        assertEquals("Malaysia", result3.getName());
    }
}
