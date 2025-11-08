package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.exceptions.ShippingFeesNotFoundException;
import com.cs203.grp2.Asg2.models.ShippingFees;
import com.cs203.grp2.Asg2.service.ShippingFeesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingFeesControllerTest {

    @Mock
    private ShippingFeesService shippingFeesService;

    @InjectMocks
    private ShippingFeesController shippingFeesController;

    @Test
    void testGetShippingFee_WithValidCountries_ShouldReturnFee() {
        // Arrange
        ShippingFees testFee = new ShippingFees(500.0, "France", "Germany");
        when(shippingFeesService.getFee("France", "Germany")).thenReturn(testFee);

        // Act
        ShippingFees result = shippingFeesController.getShippingFee("France", "Germany");

        // Assert
        assertNotNull(result);
        assertEquals("France", result.getImportingCountry());
        assertEquals("Germany", result.getExportingCountry());
        assertEquals(500.0, result.getFee());
        verify(shippingFeesService).getFee("France", "Germany");
    }

    @Test
    void testGetShippingFee_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(shippingFeesService.getFee("Unknown", "Country")).thenReturn(null);

        // Act & Assert
        ShippingFeesNotFoundException exception = assertThrows(
            ShippingFeesNotFoundException.class,
            () -> shippingFeesController.getShippingFee("Unknown", "Country")
        );

        assertTrue(exception.getMessage().contains("Shipping fee not found"));
        assertTrue(exception.getMessage().contains("Unknown"));
        assertTrue(exception.getMessage().contains("Country"));
        verify(shippingFeesService).getFee("Unknown", "Country");
    }

    @Test
    void testAddShippingFee_WithValidFee_ShouldCallService() {
        // Arrange
        ShippingFees newFee = new ShippingFees(300.0, "USA", "Canada");
        doNothing().when(shippingFeesService).addShippingFee(any(ShippingFees.class));

        // Act
        shippingFeesController.addShippingFee(newFee);

        // Assert
        verify(shippingFeesService).addShippingFee(newFee);
    }

    @Test
    void testGetShippingFee_WithDifferentCountries_ShouldReturnCorrectFee() {
        // Arrange
        ShippingFees testFee = new ShippingFees(150.0, "Singapore", "Malaysia");
        when(shippingFeesService.getFee("Singapore", "Malaysia")).thenReturn(testFee);

        // Act
        ShippingFees result = shippingFeesController.getShippingFee("Singapore", "Malaysia");

        // Assert
        assertNotNull(result);
        assertEquals(150.0, result.getFee());
        verify(shippingFeesService).getFee("Singapore", "Malaysia");
    }

    @Test
    void testAddShippingFee_MultipleTimes_ShouldCallServiceForEach() {
        // Arrange
        ShippingFees fee1 = new ShippingFees(400.0, "USA", "UK");
        ShippingFees fee2 = new ShippingFees(250.0, "China", "Japan");
        doNothing().when(shippingFeesService).addShippingFee(any(ShippingFees.class));

        // Act
        shippingFeesController.addShippingFee(fee1);
        shippingFeesController.addShippingFee(fee2);

        // Assert
        verify(shippingFeesService, times(2)).addShippingFee(any(ShippingFees.class));
    }
}
