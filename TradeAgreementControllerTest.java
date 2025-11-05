package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.exceptions.TradeAgreementNotFoundException;
import com.cs203.grp2.Asg2.models.TradeAgreement;
import com.cs203.grp2.Asg2.service.TradeAgreementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeAgreementControllerTest {

    @Mock
    private TradeAgreementService tradeAgreementService;

    @InjectMocks
    private TradeAgreementController tradeAgreementController;

    @Test
    void testGetAllAgreements_ShouldReturnListOfAgreements() {
        // Act
        List<TradeAgreement> result = tradeAgreementController.getAllAgreements();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("USMCA", result.get(0).getAgreementName());
        assertEquals("CPTPP", result.get(1).getAgreementName());
        assertEquals("ASEAN Free Trade Area", result.get(2).getAgreementName());
        assertEquals("EU Single Market", result.get(3).getAgreementName());
    }

    @Test
    void testGetAgreementByName_WithValidName_ShouldReturnAgreement() {
        // Arrange
        TradeAgreement testAgreement = new TradeAgreement("USMCA", "USA", "Canada");
        when(tradeAgreementService.getByAgreementName("USMCA"))
            .thenReturn(Optional.of(testAgreement));

        // Act
        TradeAgreement result = tradeAgreementController.getAgreementByName("USMCA");

        // Assert
        assertNotNull(result);
        assertEquals("USMCA", result.getAgreementName());
        assertEquals("USA", result.getCountry1());
        assertEquals("Canada", result.getCountry2());
        verify(tradeAgreementService).getByAgreementName("USMCA");
    }

    @Test
    void testGetAgreementByName_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(tradeAgreementService.getByAgreementName("NonExistent"))
            .thenReturn(Optional.empty());

        // Act & Assert
        TradeAgreementNotFoundException exception = assertThrows(
            TradeAgreementNotFoundException.class,
            () -> tradeAgreementController.getAgreementByName("NonExistent")
        );

        assertTrue(exception.getMessage().contains("Trade agreement not found: NonExistent"));
        verify(tradeAgreementService).getByAgreementName("NonExistent");
    }

    @Test
    void testAddAgreement_WithValidAgreement_ShouldCallService() {
        // Arrange
        TradeAgreement newAgreement = new TradeAgreement("RCEP", "China", "Japan");
        doNothing().when(tradeAgreementService).addTradeAgreement(any(TradeAgreement.class));

        // Act
        tradeAgreementController.addAgreement(newAgreement);

        // Assert
        verify(tradeAgreementService).addTradeAgreement(newAgreement);
    }

    @Test
    void testUpdateAgreement_WithValidAgreement_ShouldUpdateSuccessfully() {
        // Arrange
        TradeAgreement updatedAgreement = new TradeAgreement("USMCA", "USA", "Mexico");
        when(tradeAgreementService.updateAgreement("USMCA", updatedAgreement))
            .thenReturn(true);

        // Act
        tradeAgreementController.updateAgreement("USMCA", updatedAgreement);

        // Assert
        verify(tradeAgreementService).updateAgreement("USMCA", updatedAgreement);
    }

    @Test
    void testUpdateAgreement_WhenNotFound_ShouldThrowException() {
        // Arrange
        TradeAgreement updatedAgreement = new TradeAgreement("NonExistent", "Country1", "Country2");
        when(tradeAgreementService.updateAgreement("NonExistent", updatedAgreement))
            .thenReturn(false);

        // Act & Assert
        TradeAgreementNotFoundException exception = assertThrows(
            TradeAgreementNotFoundException.class,
            () -> tradeAgreementController.updateAgreement("NonExistent", updatedAgreement)
        );

        assertTrue(exception.getMessage().contains("Trade agreement not found: NonExistent"));
        verify(tradeAgreementService).updateAgreement("NonExistent", updatedAgreement);
    }

    @Test
    void testDeleteAgreement_WithValidName_ShouldDeleteSuccessfully() {
        // Arrange
        when(tradeAgreementService.deleteAgreement("USMCA")).thenReturn(true);

        // Act
        tradeAgreementController.deleteAgreement("USMCA");

        // Assert
        verify(tradeAgreementService).deleteAgreement("USMCA");
    }

    @Test
    void testDeleteAgreement_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(tradeAgreementService.deleteAgreement("NonExistent")).thenReturn(false);

        // Act & Assert
        TradeAgreementNotFoundException exception = assertThrows(
            TradeAgreementNotFoundException.class,
            () -> tradeAgreementController.deleteAgreement("NonExistent")
        );

        assertTrue(exception.getMessage().contains("Trade agreement not found: NonExistent"));
        verify(tradeAgreementService).deleteAgreement("NonExistent");
    }

    @Test
    void testGetAllAgreements_ShouldReturnFourPredefinedAgreements() {
        // Act
        List<TradeAgreement> result = tradeAgreementController.getAllAgreements();

        // Assert
        assertEquals(4, result.size());
        verify(tradeAgreementService, never()).getAllAgreements();
    }
}
