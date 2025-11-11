package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.models.TradeAgreement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TradeAgreementServiceTest {

    private TradeAgreementService service;

    @BeforeEach
    void setUp() {
        service = new TradeAgreementService();
    }

    @Test
    void testAddTradeAgreement() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("USMCA", "USA", "CAN");

        // Act
        TradeAgreement result = service.addTradeAgreement(agreement);

        // Assert
        assertNotNull(result);
        assertEquals("USMCA", result.getAgreementName());
        assertEquals(1, service.getAllAgreements().size());
    }

    @Test
    void testGetAllAgreements_Empty() {
        // Act
        List<TradeAgreement> agreements = service.getAllAgreements();

        // Assert
        assertNotNull(agreements);
        assertTrue(agreements.isEmpty());
    }

    @Test
    void testGetAllAgreements_MultipleAgreements() {
        // Arrange
        service.addTradeAgreement(new TradeAgreement("USMCA", "USA", "CAN"));
        service.addTradeAgreement(new TradeAgreement("EU-UK", "FRA", "GBR"));
        service.addTradeAgreement(new TradeAgreement("ASEAN", "SGP", "MYS"));

        // Act
        List<TradeAgreement> agreements = service.getAllAgreements();

        // Assert
        assertEquals(3, agreements.size());
    }

    @Test
    void testGetByAgreementName_Found() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement("USMCA", "USA", "CAN");
        service.addTradeAgreement(agreement);

        // Act
        Optional<TradeAgreement> result = service.getByAgreementName("USMCA");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("USMCA", result.get().getAgreementName());
        assertEquals("USA", result.get().getCountryA());
        assertEquals("CAN", result.get().getCountryB());
    }

    @Test
    void testGetByAgreementName_NotFound() {
        // Act
        Optional<TradeAgreement> result = service.getByAgreementName("NonExistent");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAgreementName_CaseInsensitive() {
        // Arrange
        service.addTradeAgreement(new TradeAgreement("USMCA", "USA", "CAN"));

        // Act
        Optional<TradeAgreement> result = service.getByAgreementName("usmca");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("USMCA", result.get().getAgreementName());
    }

    @Test
    void testUpdateAgreement_Success() {
        // Arrange
        service.addTradeAgreement(new TradeAgreement("USMCA", "USA", "CAN"));
        TradeAgreement updatedAgreement = new TradeAgreement("USMCA-Updated", "USA", "MEX");

        // Act
        boolean result = service.updateAgreement("USMCA", updatedAgreement);

        // Assert
        assertTrue(result);
        Optional<TradeAgreement> updated = service.getByAgreementName("USMCA-Updated");
        assertTrue(updated.isPresent());
        assertEquals("MEX", updated.get().getCountryB());
    }

    @Test
    void testUpdateAgreement_NotFound() {
        // Arrange
        TradeAgreement updatedAgreement = new TradeAgreement("NewName", "USA", "MEX");

        // Act
        boolean result = service.updateAgreement("NonExistent", updatedAgreement);

        // Assert
        assertFalse(result);
    }

    @Test
    void testUpdateAgreement_CaseInsensitive() {
        // Arrange
        service.addTradeAgreement(new TradeAgreement("USMCA", "USA", "CAN"));
        TradeAgreement updatedAgreement = new TradeAgreement("USMCA-Updated", "USA", "MEX");

        // Act
        boolean result = service.updateAgreement("usmca", updatedAgreement);

        // Assert
        assertTrue(result);
    }

    @Test
    void testDeleteAgreement_Success() {
        // Arrange
        service.addTradeAgreement(new TradeAgreement("USMCA", "USA", "CAN"));
        service.addTradeAgreement(new TradeAgreement("EU-UK", "FRA", "GBR"));

        // Act
        boolean result = service.deleteAgreement("USMCA");

        // Assert
        assertTrue(result);
        assertEquals(1, service.getAllAgreements().size());
        assertFalse(service.getByAgreementName("USMCA").isPresent());
    }

    @Test
    void testDeleteAgreement_NotFound() {
        // Act
        boolean result = service.deleteAgreement("NonExistent");

        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteAgreement_CaseInsensitive() {
        // Arrange
        service.addTradeAgreement(new TradeAgreement("USMCA", "USA", "CAN"));

        // Act
        boolean result = service.deleteAgreement("usmca");

        // Assert
        assertTrue(result);
        assertTrue(service.getAllAgreements().isEmpty());
    }

    @Test
    void testMultipleOperations() {
        // Add multiple agreements
        service.addTradeAgreement(new TradeAgreement("USMCA", "USA", "CAN"));
        service.addTradeAgreement(new TradeAgreement("EU-UK", "FRA", "GBR"));
        service.addTradeAgreement(new TradeAgreement("ASEAN", "SGP", "MYS"));

        // Update one
        TradeAgreement updated = new TradeAgreement("USMCA-2.0", "USA", "MEX");
        assertTrue(service.updateAgreement("USMCA", updated));

        // Delete one
        assertTrue(service.deleteAgreement("ASEAN"));

        // Verify final state
        assertEquals(2, service.getAllAgreements().size());
        assertTrue(service.getByAgreementName("USMCA-2.0").isPresent());
        assertTrue(service.getByAgreementName("EU-UK").isPresent());
        assertFalse(service.getByAgreementName("ASEAN").isPresent());
    }
}
