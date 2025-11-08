package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WitsTariffTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange & Act
        LocalDate date = LocalDate.of(2020, 1, 1);
        WitsTariff tariff = new WitsTariff(
            "USA",
            "CHN",
            "270900",
            date,
            5.5,
            "Ad valorem",
            "WTO Tariff Database"
        );

        // Assert
        assertEquals("USA", tariff.importerIso3());
        assertEquals("CHN", tariff.exporterIso3());
        assertEquals("270900", tariff.hs6());
        assertEquals(date, tariff.date());
        assertEquals(5.5, tariff.ratePercent());
        assertEquals("Ad valorem", tariff.basis());
        assertEquals("WTO Tariff Database", tariff.sourceNote());
    }

    @Test
    void testImmutability() {
        // Arrange
        LocalDate date = LocalDate.of(2020, 1, 1);
        WitsTariff tariff = new WitsTariff(
            "USA",
            "CHN",
            "270900",
            date,
            5.5,
            "Ad valorem",
            "WTO Tariff Database"
        );

        // Assert - No setters should exist
        // This test verifies compilation - the class has no setters
        assertNotNull(tariff);
        assertEquals("USA", tariff.importerIso3());
    }

    @Test
    void testZeroTariffRate() {
        // Arrange & Act
        LocalDate date = LocalDate.of(2022, 1, 1);
        WitsTariff tariff = new WitsTariff(
            "SGP",
            "MYS",
            "270900",
            date,
            0.0,
            "Ad valorem",
            "ASEAN Free Trade"
        );

        // Assert
        assertEquals(0.0, tariff.ratePercent());
    }

    @Test
    void testHighTariffRate() {
        // Arrange & Act
        LocalDate date = LocalDate.of(2018, 1, 1);
        WitsTariff tariff = new WitsTariff(
            "USA",
            "CHN",
            "270900",
            date,
            25.0,
            "Ad valorem",
            "Trade War Tariffs"
        );

        // Assert
        assertEquals(25.0, tariff.ratePercent());
    }

    @Test
    void testNullFields() {
        // Arrange & Act
        WitsTariff tariff = new WitsTariff(null, null, null, null, 0.0, null, null);

        // Assert
        assertNull(tariff.importerIso3());
        assertNull(tariff.exporterIso3());
        assertNull(tariff.hs6());
        assertNull(tariff.date());
        assertEquals(0.0, tariff.ratePercent());
        assertNull(tariff.basis());
        assertNull(tariff.sourceNote());
    }

    @Test
    void testEmptyStringFields() {
        // Arrange & Act
        LocalDate date = LocalDate.of(2020, 1, 1);
        WitsTariff tariff = new WitsTariff("", "", "", date, 5.5, "", "");

        // Assert
        assertEquals("", tariff.importerIso3());
        assertEquals("", tariff.exporterIso3());
        assertEquals("", tariff.hs6());
        assertEquals(date, tariff.date());
        assertEquals("", tariff.basis());
        assertEquals("", tariff.sourceNote());
    }

    @Test
    void testDifferentDateFormats() {
        // Arrange & Act
        LocalDate date1 = LocalDate.of(2020, 1, 1);
        LocalDate date2 = LocalDate.of(2020, 6, 15);
        WitsTariff tariff1 = new WitsTariff("USA", "CHN", "270900", date1, 5.5, "Ad valorem", "Source");
        WitsTariff tariff2 = new WitsTariff("USA", "CHN", "270900", date2, 5.5, "Ad valorem", "Source");

        // Assert
        assertEquals(date1, tariff1.date());
        assertEquals(date2, tariff2.date());
    }

    @Test
    void testDifferentBasisTypes() {
        // Arrange & Act
        LocalDate date = LocalDate.of(2020, 1, 1);
        WitsTariff adValorem = new WitsTariff("USA", "CHN", "270900", date, 5.5, "Ad valorem", "Source");
        WitsTariff specific = new WitsTariff("USA", "CHN", "270900", date, 100.0, "Specific", "Source");
        WitsTariff compound = new WitsTariff("USA", "CHN", "270900", date, 5.5, "Compound", "Source");

        // Assert
        assertEquals("Ad valorem", adValorem.basis());
        assertEquals("Specific", specific.basis());
        assertEquals("Compound", compound.basis());
    }

    @Test
    void testSameImporterAndExporter() {
        // Arrange & Act
        LocalDate date = LocalDate.of(2020, 1, 1);
        WitsTariff tariff = new WitsTariff("USA", "USA", "270900", date, 0.0, "Ad valorem", "Domestic");

        // Assert
        assertEquals("USA", tariff.importerIso3());
        assertEquals("USA", tariff.exporterIso3());
    }

    @Test
    void testLongSourceNote() {
        // Arrange
        LocalDate date = LocalDate.of(2020, 1, 1);
        String longNote = "This is a very long source note that contains detailed information " +
                         "about the tariff data source, methodology, and any special considerations.";
        
        // Act
        WitsTariff tariff = new WitsTariff("USA", "CHN", "270900", date, 5.5, "Ad valorem", longNote);

        // Assert
        assertEquals(longNote, tariff.sourceNote());
    }

    @Test
    void testNegativeTariffRate() {
        // Arrange & Act
        LocalDate date = LocalDate.of(2020, 1, 1);
        WitsTariff tariff = new WitsTariff("USA", "CHN", "270900", date, -5.5, "Subsidy", "Export subsidy");

        // Assert
        assertEquals(-5.5, tariff.ratePercent());
    }

    @Test
    void testMultipleInstancesIndependence() {
        // Arrange & Act
        LocalDate date1 = LocalDate.of(2020, 1, 1);
        LocalDate date2 = LocalDate.of(2021, 1, 1);
        WitsTariff tariff1 = new WitsTariff("USA", "CHN", "270900", date1, 5.5, "Ad valorem", "Source1");
        WitsTariff tariff2 = new WitsTariff("GBR", "FRA", "123456", date2, 3.2, "Specific", "Source2");

        // Assert
        assertNotEquals(tariff1.importerIso3(), tariff2.importerIso3());
        assertNotEquals(tariff1.exporterIso3(), tariff2.exporterIso3());
        assertNotEquals(tariff1.hs6(), tariff2.hs6());
        assertNotEquals(tariff1.date(), tariff2.date());
        assertNotEquals(tariff1.ratePercent(), tariff2.ratePercent());
        assertNotEquals(tariff1.basis(), tariff2.basis());
        assertNotEquals(tariff1.sourceNote(), tariff2.sourceNote());
    }

    @Test
    void testDecimalPrecision() {
        // Arrange & Act
        LocalDate date = LocalDate.of(2020, 1, 1);
        WitsTariff tariff = new WitsTariff("USA", "CHN", "270900", date, 5.555555, "Ad valorem", "Source");

        // Assert
        assertEquals(5.555555, tariff.ratePercent(), 0.000001);
    }
}
