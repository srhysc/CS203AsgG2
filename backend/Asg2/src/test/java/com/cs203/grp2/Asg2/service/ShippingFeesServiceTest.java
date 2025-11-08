package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.models.ShippingFees;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShippingFeesServiceTest {

    private ShippingFeesService service;

    @BeforeEach
    void setUp() {
        service = new ShippingFeesService();
    }

    @Test
    void testGetFee_ExistingRoute_FranceToGermany() {
        // Act
        ShippingFees fees = service.getFee("France", "Germany");

        // Assert
        assertNotNull(fees);
        assertEquals(5.0, fees.getFee());
        assertEquals("France", fees.getImportingCountry());
        assertEquals("Germany", fees.getExportingCountry());
    }

    @Test
    void testGetFee_ExistingRoute_GermanyToFrance() {
        // Act
        ShippingFees fees = service.getFee("Germany", "France");

        // Assert
        assertNotNull(fees);
        assertEquals(7.5, fees.getFee());
        assertEquals("Germany", fees.getImportingCountry());
        assertEquals("France", fees.getExportingCountry());
    }

    @Test
    void testGetFee_ExistingRoute_USAToCanada() {
        // Act
        ShippingFees fees = service.getFee("USA", "Canada");

        // Assert
        assertNotNull(fees);
        assertEquals(10.0, fees.getFee());
        assertEquals("USA", fees.getImportingCountry());
        assertEquals("Canada", fees.getExportingCountry());
    }

    @Test
    void testGetFee_ExistingRoute_CanadaToUSA() {
        // Act
        ShippingFees fees = service.getFee("Canada", "USA");

        // Assert
        assertNotNull(fees);
        assertEquals(9.0, fees.getFee());
        assertEquals("Canada", fees.getImportingCountry());
        assertEquals("USA", fees.getExportingCountry());
    }

    @Test
    void testGetFee_NonExistingRoute() {
        // Act
        ShippingFees fees = service.getFee("China", "Japan");

        // Assert
        assertNull(fees);
    }

    @Test
    void testGetFee_CaseInsensitive_Uppercase() {
        // Act
        ShippingFees fees = service.getFee("FRANCE", "GERMANY");

        // Assert
        assertNotNull(fees);
        assertEquals(5.0, fees.getFee());
    }

    @Test
    void testGetFee_CaseInsensitive_Lowercase() {
        // Act
        ShippingFees fees = service.getFee("france", "germany");

        // Assert
        assertNotNull(fees);
        assertEquals(5.0, fees.getFee());
    }

    @Test
    void testGetFee_CaseInsensitive_MixedCase() {
        // Act
        ShippingFees fees = service.getFee("FrAnCe", "GeRmAnY");

        // Assert
        assertNotNull(fees);
        assertEquals(5.0, fees.getFee());
    }

    @Test
    void testGetFee_ReverseRouteHasDifferentFee() {
        // Act
        ShippingFees franceToGermany = service.getFee("France", "Germany");
        ShippingFees germanyToFrance = service.getFee("Germany", "France");

        // Assert
        assertNotNull(franceToGermany);
        assertNotNull(germanyToFrance);
        assertNotEquals(franceToGermany.getFee(), germanyToFrance.getFee());
        assertEquals(5.0, franceToGermany.getFee());
        assertEquals(7.5, germanyToFrance.getFee());
    }

    @Test
    void testAddShippingFee_NewRoute() {
        // Arrange
        ShippingFees newFees = new ShippingFees(15.0, "China", "Japan");

        // Act
        service.addShippingFee(newFees);
        ShippingFees retrieved = service.getFee("China", "Japan");

        // Assert
        assertNotNull(retrieved);
        assertEquals(15.0, retrieved.getFee());
        assertEquals("China", retrieved.getImportingCountry());
        assertEquals("Japan", retrieved.getExportingCountry());
    }

    @Test
    void testAddShippingFee_UpdateExistingRoute() {
        // Arrange
        ShippingFees updatedFees = new ShippingFees(12.0, "France", "Germany");

        // Act
        service.addShippingFee(updatedFees);
        ShippingFees retrieved = service.getFee("France", "Germany");

        // Assert
        assertNotNull(retrieved);
        assertEquals(12.0, retrieved.getFee()); // Updated from 5.0 to 12.0
    }

    @Test
    void testAddShippingFee_CaseInsensitive() {
        // Arrange
        ShippingFees newFees = new ShippingFees(20.0, "AUSTRALIA", "NEW ZEALAND");

        // Act
        service.addShippingFee(newFees);
        ShippingFees retrieved = service.getFee("australia", "new zealand");

        // Assert
        assertNotNull(retrieved);
        assertEquals(20.0, retrieved.getFee());
    }

    @Test
    void testMultipleRoutes() {
        // Arrange
        service.addShippingFee(new ShippingFees(15.0, "China", "Japan"));
        service.addShippingFee(new ShippingFees(18.0, "Japan", "China"));
        service.addShippingFee(new ShippingFees(25.0, "Australia", "Singapore"));

        // Act & Assert
        assertEquals(15.0, service.getFee("China", "Japan").getFee());
        assertEquals(18.0, service.getFee("Japan", "China").getFee());
        assertEquals(25.0, service.getFee("Australia", "Singapore").getFee());
        
        // Original routes still exist
        assertNotNull(service.getFee("France", "Germany"));
        assertNotNull(service.getFee("USA", "Canada"));
    }

    @Test
    void testGetFee_NullHandling() {
        // Act
        ShippingFees result1 = service.getFee("NonExistent", "Country");
        ShippingFees result2 = service.getFee("Country", "NonExistent");

        // Assert
        assertNull(result1);
        assertNull(result2);
    }

    @Test
    void testGetFee_AllPredefinedRoutes() {
        // Test all 6 predefined routes
        assertNotNull(service.getFee("France", "Germany"));
        assertNotNull(service.getFee("Germany", "France"));
        assertNotNull(service.getFee("France", "Italy"));
        assertNotNull(service.getFee("Italy", "France"));
        assertNotNull(service.getFee("USA", "Canada"));
        assertNotNull(service.getFee("Canada", "USA"));
    }
}
