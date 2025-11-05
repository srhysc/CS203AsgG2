package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.models.Petroleum;
import com.cs203.grp2.Asg2.service.PetroleumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetroleumControllerTest {

    @Mock
    private PetroleumService petroleumService;

    @InjectMocks
    private PetroleumController petroleumController;

    @Test
    void testGetAllPetroleum_ShouldReturnListOfPetroleum() throws Exception {
        // Arrange
        Petroleum petroleum1 = new Petroleum("Crude Oil", "2710", null);
        Petroleum petroleum2 = new Petroleum("Natural Gas", "2711", null);

        List<Petroleum> petroleumList = Arrays.asList(petroleum1, petroleum2);
        when(petroleumService.getAllPetroleum()).thenReturn(petroleumList);

        // Act
        List<Petroleum> result = petroleumController.getAllPetroleum();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Crude Oil", result.get(0).getName());
        assertEquals("Natural Gas", result.get(1).getName());
        verify(petroleumService).getAllPetroleum();
    }

    @Test
    void testGetAllPetroleum_WhenExceptionThrown_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(petroleumService.getAllPetroleum()).thenThrow(new RuntimeException("Database error"));

        // Act
        List<Petroleum> result = petroleumController.getAllPetroleum();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(petroleumService).getAllPetroleum();
    }

    @Test
    void testGetPetroleumByHsCode_WithValidCode_ShouldReturnPetroleum() {
        // Arrange
        Petroleum testPetroleum = new Petroleum("Crude Oil", "2710", null);
        when(petroleumService.getPetroleumByHsCode("2710")).thenReturn(testPetroleum);

        // Act
        Petroleum result = petroleumController.getPetroleumByHsCode("2710");

        // Assert
        assertNotNull(result);
        assertEquals("2710", result.getHsCode());
        assertEquals("Crude Oil", result.getName());
        verify(petroleumService).getPetroleumByHsCode("2710");
    }

    @Test
    void testGetPetroleumByHsCode_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(petroleumService.getPetroleumByHsCode("9999")).thenReturn(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            petroleumController.getPetroleumByHsCode("9999");
        });

        assertTrue(exception.getMessage().contains("Petroleum not found for HS code: 9999"));
        verify(petroleumService).getPetroleumByHsCode("9999");
    }

    @Test
    void testGetPetroleumByHsCode_WithFourDigitCode_ShouldWork() {
        // Arrange
        Petroleum testPetroleum = new Petroleum("2710", "Crude Oil", null);
        when(petroleumService.getPetroleumByHsCode("2710")).thenReturn(testPetroleum);

        // Act
        Petroleum result = petroleumController.getPetroleumByHsCode("2710");

        // Assert
        assertNotNull(result);
        verify(petroleumService).getPetroleumByHsCode("2710");
    }

    @Test
    void testGetPetroleumByHsCode_WithSixDigitCode_ShouldWork() {
        // Arrange
        Petroleum petroleum = new Petroleum("Gasoline", "271012", null);
        when(petroleumService.getPetroleumByHsCode("271012")).thenReturn(petroleum);

        // Act
        Petroleum result = petroleumController.getPetroleumByHsCode("271012");

        // Assert
        assertNotNull(result);
        assertEquals("271012", result.getHsCode());
        verify(petroleumService).getPetroleumByHsCode("271012");
    }

    @Test
    void testGetAllPetroleum_WhenServiceReturnsEmptyList_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(petroleumService.getAllPetroleum()).thenReturn(List.of());

        // Act
        List<Petroleum> result = petroleumController.getAllPetroleum();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(petroleumService).getAllPetroleum();
    }
}
