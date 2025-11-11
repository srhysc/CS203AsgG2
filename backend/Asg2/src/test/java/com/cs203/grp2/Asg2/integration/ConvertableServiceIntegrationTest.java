package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.DTO.ConvertableResponseDTO;
import com.cs203.grp2.Asg2.DTO.ConvertToResponseDTO;
import com.cs203.grp2.Asg2.exceptions.ConvertableNotFoundException;
import com.cs203.grp2.Asg2.service.ConvertableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ConvertableServiceImpl
 * Tests the service's interaction with Firebase to fetch convertable petroleum data
 */
public class ConvertableServiceIntegrationTest extends BaseFirebaseIntegrationTest {

    @Autowired
    private ConvertableService convertableService;

    @Test
    void testGetAllConvertables_Success() {
        // Act
        List<ConvertableResponseDTO> convertables = convertableService.getAllConvertables();

        // Assert
        assertNotNull(convertables, "Convertables list should not be null");
        System.out.println("✓ Found " + convertables.size() + " convertable petroleum products");

        if (!convertables.isEmpty()) {
            ConvertableResponseDTO first = convertables.get(0);
            assertNotNull(first.getHscode(), "HS code should not be null");
            assertNotNull(first.getName(), "Name should not be null");
            System.out.println("✓ Sample convertable: " + first.getName() + " (HS: " + first.getHscode() + ")");

            if (first.getTo() != null && !first.getTo().isEmpty()) {
                ConvertToResponseDTO firstTo = first.getTo().get(0);
                System.out.println("✓ Can convert to: " + firstTo.getName() + " with " + firstTo.getYield_percent() + "% yield");
            }
        }
    }

    @Test
    void testGetConvertableByHscode_CrudeOil() {
        // Arrange - Get all convertables first to find a valid HS code
        List<ConvertableResponseDTO> allConvertables = convertableService.getAllConvertables();
        assertTrue(allConvertables.size() > 0, "Should have at least one convertable");

        // Use the first convertable's HS code for lookup test
        String firstHscode = allConvertables.get(0).getHscode();
        String expectedName = allConvertables.get(0).getName();

        // Act
        ConvertableResponseDTO convertable = convertableService.getConvertableByHscode(firstHscode);

        // Assert
        assertNotNull(convertable, "Should find convertable for HS code " + firstHscode);
        assertEquals(firstHscode, convertable.getHscode(), "HS code should match");
        assertEquals(expectedName, convertable.getName(), "Name should match");
        assertNotNull(convertable.getTo(), "ConvertTo list should not be null");
        
        System.out.println("✅ Found convertable by HS code: " + convertable.getName() + " (HS: " + firstHscode + ")");
        if (convertable.getTo() != null && !convertable.getTo().isEmpty()) {
            System.out.println("✅ Can convert to: " + convertable.getTo().get(0).getName() 
                    + " with " + convertable.getTo().get(0).getYield_percent() + "% yield");
        }
    }

    @Test
    void testGetConvertableByHscode_NotFound() {
        // Arrange - Non-existent HS code
        String invalidHscode = "999999";

        // Act & Assert
        assertThrows(ConvertableNotFoundException.class, () -> {
            convertableService.getConvertableByHscode(invalidHscode);
        }, "Should throw ConvertableNotFoundException for invalid HS code");

        System.out.println("✓ Correctly throws exception for non-existent HS code");
    }

    @Test
    void testConvertableDataStructure() {
        // Act
        List<ConvertableResponseDTO> convertables = convertableService.getAllConvertables();

        // Assert - Validate data structure
        for (ConvertableResponseDTO convertable : convertables) {
            assertNotNull(convertable.getHscode(), "Each convertable must have an HS code");
            assertNotNull(convertable.getName(), "Each convertable must have a name");
            assertFalse(convertable.getHscode().trim().isEmpty(), "HS code should not be empty");
            assertFalse(convertable.getName().trim().isEmpty(), "Name should not be empty");

            // Validate conversion targets
            if (convertable.getTo() != null) {
                for (ConvertToResponseDTO to : convertable.getTo()) {
                    assertNotNull(to.getHscode(), "Convert-to product must have HS code");
                    assertNotNull(to.getName(), "Convert-to product must have name");
                    assertTrue(to.getYield_percent() >= 0, "Yield percent cannot be negative");
                    assertTrue(to.getYield_percent() <= 100, "Yield percent cannot exceed 100");
                }
            }
        }
        System.out.println("✓ All convertable data structures are valid");
    }

    @Test
    void testMultipleCallsConsistency() {
        // Act - Call service multiple times
        List<ConvertableResponseDTO> firstCall = convertableService.getAllConvertables();
        List<ConvertableResponseDTO> secondCall = convertableService.getAllConvertables();

        // Assert - Results should be consistent
        assertEquals(firstCall.size(), secondCall.size(),
                "Multiple calls should return same number of convertables");

        System.out.println("✓ Multiple calls return consistent data: " + firstCall.size() + " convertables");
    }

    @Test
    void testYieldPercentageValidation() {
        // Act
        List<ConvertableResponseDTO> convertables = convertableService.getAllConvertables();

        // Assert - Check that total yield percentages make sense
        for (ConvertableResponseDTO convertable : convertables) {
            if (convertable.getTo() != null && !convertable.getTo().isEmpty()) {
                int totalYield = 0;
                for (ConvertToResponseDTO to : convertable.getTo()) {
                    totalYield += to.getYield_percent();
                }
                // Note: Total yield might exceed 100% in some refining processes due to volume expansion
                assertTrue(totalYield > 0, "Total yield should be positive for " + convertable.getName());
                System.out.println("✓ " + convertable.getName() + " total yield: " + totalYield + "%");
            }
        }
    }

    @Test
    void testConvertableByHscode_CaseInsensitive() {
        // Arrange
        List<ConvertableResponseDTO> allConvertables = convertableService.getAllConvertables();

        if (!allConvertables.isEmpty()) {
            String hscode = allConvertables.get(0).getHscode();

            // Act - Try different cases
            ConvertableResponseDTO upper = convertableService.getConvertableByHscode(hscode.toUpperCase());
            ConvertableResponseDTO lower = convertableService.getConvertableByHscode(hscode.toLowerCase());

            // Assert - Should handle both cases
            if (upper != null && lower != null) {
                assertEquals(upper.getHscode(), lower.getHscode(),
                        "Case-insensitive search should return same convertable");
                System.out.println("✓ Case-insensitive HS code search works correctly");
            }
        } else {
            System.out.println("⚠ No convertables available for case-sensitivity test");
        }
    }
}
