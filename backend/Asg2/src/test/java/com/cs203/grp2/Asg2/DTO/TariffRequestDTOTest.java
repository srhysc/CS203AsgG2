package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TariffRequestDTOTest {

    @Test
    void testRecordCreation_WithStringParameters() {
        // Arrange & Act
        LocalDate testDate = LocalDate.of(2025, 10, 14);
        TariffRequestDTO dto = new TariffRequestDTO("BGR", "CHN", "271012", testDate);

        // Assert
        assertEquals("BGR", dto.importerIso3());
        assertEquals("CHN", dto.exporterIso3());
        assertEquals("271012", dto.hs6());
        assertEquals(testDate, dto.date());
    }

    @Test
    void testRecordCreation_WithIntegerParameters() {
        // Arrange & Act
        LocalDate testDate = LocalDate.of(2025, 1, 1);
        TariffRequestDTO dto = new TariffRequestDTO(100, 156, "270900", testDate);

        // Assert
        assertEquals("100", dto.importerIso3());
        assertEquals("156", dto.exporterIso3());
        assertEquals("270900", dto.hs6());
        assertEquals(testDate, dto.date());
    }

    @Test
    void testRecordEquality_SameValues_ShouldBeEqual() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 5, 20);
        TariffRequestDTO dto1 = new TariffRequestDTO("USA", "CAN", "123456", testDate);
        TariffRequestDTO dto2 = new TariffRequestDTO("USA", "CAN", "123456", testDate);

        // Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testRecordEquality_DifferentValues_ShouldNotBeEqual() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 5, 20);
        TariffRequestDTO dto1 = new TariffRequestDTO("USA", "CAN", "123456", testDate);
        TariffRequestDTO dto2 = new TariffRequestDTO("USA", "MEX", "123456", testDate);

        // Assert
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testToString_ShouldContainAllFields() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 6, 15);
        TariffRequestDTO dto = new TariffRequestDTO("FRA", "DEU", "654321", testDate);

        // Act
        String result = dto.toString();

        // Assert
        assertTrue(result.contains("FRA"));
        assertTrue(result.contains("DEU"));
        assertTrue(result.contains("654321"));
        assertTrue(result.contains("2025-06-15"));
    }

    @Test
    void testIntegerConstructor_WithZeroValues() {
        // Arrange & Act
        LocalDate testDate = LocalDate.of(2025, 3, 10);
        TariffRequestDTO dto = new TariffRequestDTO(0, 0, "111111", testDate);

        // Assert
        assertEquals("0", dto.importerIso3());
        assertEquals("0", dto.exporterIso3());
    }

    @Test
    void testRecordImmutability() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 7, 1);
        TariffRequestDTO dto = new TariffRequestDTO("SGP", "JPN", "999999", testDate);

        // Assert - Record fields are final by default
        assertNotNull(dto.importerIso3());
        assertNotNull(dto.exporterIso3());
        assertNotNull(dto.hs6());
        assertNotNull(dto.date());
    }
}
