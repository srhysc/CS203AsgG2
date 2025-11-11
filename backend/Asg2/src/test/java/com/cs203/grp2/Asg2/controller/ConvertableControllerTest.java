package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.ConvertableResponseDTO;
import com.cs203.grp2.Asg2.DTO.ConvertToResponseDTO;
import com.cs203.grp2.Asg2.exceptions.ConvertableNotFoundException;
import com.cs203.grp2.Asg2.service.ConvertableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConvertableControllerTest {

    @Mock
    private ConvertableService convertableService;

    @InjectMocks
    private ConvertableController convertableController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllConvertables_Success() {
        // Arrange
        List<ConvertToResponseDTO> toList1 = new ArrayList<>();
        toList1.add(new ConvertToResponseDTO("271000", "Gasoline", 45));
        
        List<ConvertToResponseDTO> toList2 = new ArrayList<>();
        toList2.add(new ConvertToResponseDTO("271200", "Diesel", 30));

        List<ConvertableResponseDTO> convertables = new ArrayList<>();
        convertables.add(new ConvertableResponseDTO("270900", "Crude Oil", toList1));
        convertables.add(new ConvertableResponseDTO("270800", "Light Crude", toList2));

        when(convertableService.getAllConvertables()).thenReturn(convertables);

        // Act
        List<ConvertableResponseDTO> result = convertableController.getAllConvertables();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("270900", result.get(0).getHscode());
        assertEquals("Crude Oil", result.get(0).getName());
        verify(convertableService).getAllConvertables();
    }

    @Test
    void testGetAllConvertables_EmptyList() {
        // Arrange
        when(convertableService.getAllConvertables()).thenReturn(new ArrayList<>());

        // Act
        List<ConvertableResponseDTO> result = convertableController.getAllConvertables();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(convertableService).getAllConvertables();
    }

    @Test
    void testGetConvertableByHscode_Success() {
        // Arrange
        List<ConvertToResponseDTO> toList = new ArrayList<>();
        toList.add(new ConvertToResponseDTO("271000", "Gasoline", 45));
        toList.add(new ConvertToResponseDTO("271200", "Diesel", 30));

        ConvertableResponseDTO convertable = new ConvertableResponseDTO("270900", "Crude Oil", toList);
        when(convertableService.getConvertableByHscode("270900")).thenReturn(convertable);

        // Act
        ConvertableResponseDTO result = convertableController.getConvertableByHscode("270900");

        // Assert
        assertNotNull(result);
        assertEquals("270900", result.getHscode());
        assertEquals("Crude Oil", result.getName());
        assertEquals(2, result.getTo().size());
        verify(convertableService).getConvertableByHscode("270900");
    }

    @Test
    void testGetConvertableByHscode_NotFound() {
        // Arrange
        when(convertableService.getConvertableByHscode(anyString())).thenReturn(null);

        // Act & Assert
        ConvertableNotFoundException exception = assertThrows(
            ConvertableNotFoundException.class,
            () -> convertableController.getConvertableByHscode("999999")
        );

        assertTrue(exception.getMessage().contains("999999"));
        verify(convertableService).getConvertableByHscode("999999");
    }

    @Test
    void testGetConvertableByHscode_ValidHscode() {
        // Arrange
        List<ConvertToResponseDTO> toList = new ArrayList<>();
        toList.add(new ConvertToResponseDTO("271000", "Gasoline", 45));

        ConvertableResponseDTO convertable = new ConvertableResponseDTO("270900", "Crude Oil", toList);
        when(convertableService.getConvertableByHscode("270900")).thenReturn(convertable);

        // Act
        ConvertableResponseDTO result = convertableController.getConvertableByHscode("270900");

        // Assert
        assertEquals("270900", result.getHscode());
        assertEquals(1, result.getTo().size());
    }

    @Test
    void testGetConvertableByHscode_EmptyConvertToList() {
        // Arrange
        ConvertableResponseDTO convertable = new ConvertableResponseDTO("270900", "Crude Oil", new ArrayList<>());
        when(convertableService.getConvertableByHscode("270900")).thenReturn(convertable);

        // Act
        ConvertableResponseDTO result = convertableController.getConvertableByHscode("270900");

        // Assert
        assertNotNull(result.getTo());
        assertEquals(0, result.getTo().size());
    }
}
