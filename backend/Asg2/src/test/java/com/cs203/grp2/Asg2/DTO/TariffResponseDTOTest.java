package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TariffResponseDTOTest {

    @Test
    void testRecordCreation_WithValidValues() {
        // Arrange & Act
        TariffResponseDTO dto = new TariffResponseDTO(10.5, "MFN", "WITS Database");

        // Assert
        assertEquals(10.5, dto.ratePercent());
        assertEquals("MFN", dto.basis());
        assertEquals("WITS Database", dto.sourceNote());
    }

    @Test
    void testRecordCreation_WithZeroRate() {
        // Arrange & Act
        TariffResponseDTO dto = new TariffResponseDTO(0.0, "none", "No tariff");

        // Assert
        assertEquals(0.0, dto.ratePercent());
        assertEquals("none", dto.basis());
        assertEquals("No tariff", dto.sourceNote());
    }

    @Test
    void testRecordCreation_WithPreferentialBasis() {
        // Arrange & Act
        TariffResponseDTO dto = new TariffResponseDTO(5.0, "preferential", "Trade Agreement");

        // Assert
        assertEquals(5.0, dto.ratePercent());
        assertEquals("preferential", dto.basis());
        assertEquals("Trade Agreement", dto.sourceNote());
    }

    @Test
    void testNoneFactory_ShouldReturnDefaultValues() {
        // Act
        TariffResponseDTO dto = TariffResponseDTO.none();

        // Assert
        assertEquals(0.0, dto.ratePercent());
        assertEquals("none", dto.basis());
        assertEquals("No rate found in DB or WITS", dto.sourceNote());
    }

    @Test
    void testRecordEquality_SameValues_ShouldBeEqual() {
        // Arrange
        TariffResponseDTO dto1 = new TariffResponseDTO(15.75, "wits", "Default");
        TariffResponseDTO dto2 = new TariffResponseDTO(15.75, "wits", "Default");

        // Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testRecordEquality_DifferentRates_ShouldNotBeEqual() {
        // Arrange
        TariffResponseDTO dto1 = new TariffResponseDTO(10.0, "MFN", "Source");
        TariffResponseDTO dto2 = new TariffResponseDTO(15.0, "MFN", "Source");

        // Assert
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testRecordEquality_DifferentBasis_ShouldNotBeEqual() {
        // Arrange
        TariffResponseDTO dto1 = new TariffResponseDTO(10.0, "MFN", "Source");
        TariffResponseDTO dto2 = new TariffResponseDTO(10.0, "preferential", "Source");

        // Assert
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testToString_ShouldContainAllFields() {
        // Arrange
        TariffResponseDTO dto = new TariffResponseDTO(8.5, "GSP", "Special Rate");

        // Act
        String result = dto.toString();

        // Assert
        assertTrue(result.contains("8.5"));
        assertTrue(result.contains("GSP"));
        assertTrue(result.contains("Special Rate"));
    }

    @Test
    void testRecordCreation_WithHighRate() {
        // Arrange & Act
        TariffResponseDTO dto = new TariffResponseDTO(99.99, "MFN", "Maximum Rate");

        // Assert
        assertEquals(99.99, dto.ratePercent());
        assertEquals("MFN", dto.basis());
        assertEquals("Maximum Rate", dto.sourceNote());
    }

    @Test
    void testMultipleNoneInstances_ShouldBeEqual() {
        // Act
        TariffResponseDTO dto1 = TariffResponseDTO.none();
        TariffResponseDTO dto2 = TariffResponseDTO.none();

        // Assert
        assertEquals(dto1, dto2);
    }

    @Test
    void testRecordImmutability() {
        // Arrange
        TariffResponseDTO dto = new TariffResponseDTO(12.5, "MFN", "Test");

        // Assert - Record fields are final by default
        assertNotNull(dto.basis());
        assertNotNull(dto.sourceNote());
        assertEquals(12.5, dto.ratePercent());
    }
}
