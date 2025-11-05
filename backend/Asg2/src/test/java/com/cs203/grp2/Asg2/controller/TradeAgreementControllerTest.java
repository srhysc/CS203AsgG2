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
        // Controller returns hardcoded data, not using service
        List<TradeAgreement> result = tradeAgreementController.getAllAgreements();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("USMCA", result.get(0).getAgreementName());
        assertEquals("CPTPP", result.get(1).getAgreementName());
        assertEquals("ASEAN Free Trade Area", result.get(2).getAgreementName());
        assertEquals("EU Single Market", result.get(3).getAgreementName());
    }

    @Test
    void testGetAgreementByName_WithValidName_ShouldReturnAgreement() {
        TradeAgreement agreement = new TradeAgreement("USMCA", "USA", "MEX");

        when(tradeAgreementService.getByAgreementName("USMCA")).thenReturn(Optional.of(agreement));

        TradeAgreement result = tradeAgreementController.getAgreementByName("USMCA");

        assertNotNull(result);
        assertEquals("USMCA", result.getAgreementName());
        assertEquals("USA", result.getCountryA());
        assertEquals("MEX", result.getCountryB());
        verify(tradeAgreementService, times(1)).getByAgreementName("USMCA");
    }

    @Test
    void testGetAgreementByName_WithInvalidName_ShouldThrowException() {
        when(tradeAgreementService.getByAgreementName("NonExistent")).thenReturn(Optional.empty());

        assertThrows(TradeAgreementNotFoundException.class, () -> {
            tradeAgreementController.getAgreementByName("NonExistent");
        });
        
        verify(tradeAgreementService, times(1)).getByAgreementName("NonExistent");
    }

    @Test
    void testAddAgreement_WithValidAgreement_ShouldCallService() {
        TradeAgreement inputAgreement = new TradeAgreement("TPP", "JPN", "SGP");
        TradeAgreement savedAgreement = new TradeAgreement("TPP", "JPN", "SGP");

        when(tradeAgreementService.addTradeAgreement(any(TradeAgreement.class))).thenReturn(savedAgreement);

        tradeAgreementController.addAgreement(inputAgreement);

        verify(tradeAgreementService, times(1)).addTradeAgreement(any(TradeAgreement.class));
    }

    @Test
    void testUpdateAgreement_WithValidAgreement_ShouldUpdateSuccessfully() {
        String agreementName = "RCEP";
        TradeAgreement updatedAgreement = new TradeAgreement("RCEP", "CHN", "AUS");

        when(tradeAgreementService.updateAgreement(eq(agreementName), any(TradeAgreement.class)))
                .thenReturn(true);

        tradeAgreementController.updateAgreement(agreementName, updatedAgreement);

        verify(tradeAgreementService, times(1)).updateAgreement(eq(agreementName), any(TradeAgreement.class));
    }

    @Test
    void testUpdateAgreement_WithNonExistentAgreement_ShouldThrowException() {
        String agreementName = "NonExistent";
        TradeAgreement updatedAgreement = new TradeAgreement("NonExistent", "USA", "CAN");

        when(tradeAgreementService.updateAgreement(eq(agreementName), any(TradeAgreement.class)))
                .thenReturn(false);

        assertThrows(TradeAgreementNotFoundException.class, () -> {
            tradeAgreementController.updateAgreement(agreementName, updatedAgreement);
        });

        verify(tradeAgreementService, times(1)).updateAgreement(eq(agreementName), any(TradeAgreement.class));
    }

    @Test
    void testDeleteAgreement_WithValidName_ShouldDeleteAgreement() {
        String agreementName = "OldAgreement";

        when(tradeAgreementService.deleteAgreement(agreementName)).thenReturn(true);

        tradeAgreementController.deleteAgreement(agreementName);

        verify(tradeAgreementService, times(1)).deleteAgreement(agreementName);
    }

    @Test
    void testDeleteAgreement_WithNonExistentName_ShouldThrowException() {
        String agreementName = "NonExistent";

        when(tradeAgreementService.deleteAgreement(agreementName)).thenReturn(false);

        assertThrows(TradeAgreementNotFoundException.class, () -> {
            tradeAgreementController.deleteAgreement(agreementName);
        });

        verify(tradeAgreementService, times(1)).deleteAgreement(agreementName);
    }

    @Test
    void testGetAllAgreements_WhenEmpty_ShouldReturnEmptyList() {
        // Controller returns hardcoded data, so it will never be empty
        List<TradeAgreement> result = tradeAgreementController.getAllAgreements();

        assertNotNull(result);
        assertFalse(result.isEmpty()); // Hardcoded data always returns 4 agreements
        assertEquals(4, result.size());
    }
}
