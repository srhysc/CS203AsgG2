package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CountryTest {

    @Test
    void testDefaultConstructor() {
        // Act
        Country country = new Country();

        // Assert
        assertNotNull(country);
        assertNull(country.getName());
        assertNull(country.getCode());
        assertNull(country.getISO3());
        assertNotNull(country.getVatRates()); // VATRates is initialized to empty ArrayList
        assertTrue(country.getVatRates().isEmpty());
        assertNull(country.getHs27_taxes());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Country country = new Country();
        List<VATRate> vatRates = Arrays.asList(new VATRate(LocalDate.now(), 7.0));

        // Act
        country.setName("Singapore");
        country.setCode("702");
        country.setISO3("SGP");
        country.setVatRates(vatRates);

        // Assert
        assertEquals("Singapore", country.getName());
        assertEquals("702", country.getCode());
        assertEquals("SGP", country.getISO3());
        assertEquals(vatRates, country.getVatRates());
    }

    @Test
    void testNormalize_WithNullValues() {
        // Arrange
        Country country = new Country();

        // Act
        country.normalize();

        // Assert
        assertNotNull(country.getVatRates());
        assertNotNull(country.getHs27_taxes());
    }

    @Test
    void testNormalize_WithExistingValues() {
        // Arrange
        Country country = new Country();
        List<VATRate> vatRates = Arrays.asList(new VATRate(LocalDate.now(), 10.0));
        country.setVatRates(vatRates);
        Country.Hs27Taxes taxes = new Country.Hs27Taxes();
        country.setHs27_taxes(taxes);

        // Act
        country.normalize();

        // Assert
        assertEquals(vatRates, country.getVatRates());
        assertNotNull(country.getHs27_taxes());
    }

    @Test
    void testHs27TaxesDefaultConstructor() {
        // Act
        Country.Hs27Taxes taxes = new Country.Hs27Taxes();

        // Assert
        assertNotNull(taxes);
        assertNull(taxes.getVat_gst_percent());
        assertNull(taxes.getExcise_specific_per_liter());
        assertNull(taxes.getExcise_currency());
        assertNull(taxes.getCustoms_fees());
        assertNull(taxes.getCarbon_tax_per_tCO2e());
        assertNull(taxes.getCarbon_tax_currency());
    }

    @Test
    void testHs27TaxesSettersAndGetters() {
        // Arrange
        Country.Hs27Taxes taxes = new Country.Hs27Taxes();
        Country.CustomsFees fees = new Country.CustomsFees();

        // Act
        taxes.setVat_gst_percent(7.0);
        taxes.setExcise_specific_per_liter(0.5);
        taxes.setExcise_currency("SGD");
        taxes.setCustoms_fees(fees);
        taxes.setCarbon_tax_per_tCO2e(25.0);
        taxes.setCarbon_tax_currency("USD");

        // Assert
        assertEquals(7.0, taxes.getVat_gst_percent());
        assertEquals(0.5, taxes.getExcise_specific_per_liter());
        assertEquals("SGD", taxes.getExcise_currency());
        assertEquals(fees, taxes.getCustoms_fees());
        assertEquals(25.0, taxes.getCarbon_tax_per_tCO2e());
        assertEquals("USD", taxes.getCarbon_tax_currency());
    }

    @Test
    void testHs27TaxesNormalize_WithNullValues() {
        // Arrange
        Country.Hs27Taxes taxes = new Country.Hs27Taxes();

        // Act
        taxes.normalize();

        // Assert
        assertEquals(0.0, taxes.getVat_gst_percent());
        assertEquals(0.0, taxes.getExcise_specific_per_liter());
        assertNotNull(taxes.getCustoms_fees());
        assertEquals(0.0, taxes.getCarbon_tax_per_tCO2e());
    }

    @Test
    void testHs27TaxesNormalize_WithExistingValues() {
        // Arrange
        Country.Hs27Taxes taxes = new Country.Hs27Taxes();
        taxes.setVat_gst_percent(10.0);
        taxes.setExcise_specific_per_liter(0.8);
        taxes.setCarbon_tax_per_tCO2e(30.0);
        Country.CustomsFees fees = new Country.CustomsFees();
        taxes.setCustoms_fees(fees);

        // Act
        taxes.normalize();

        // Assert
        assertEquals(10.0, taxes.getVat_gst_percent());
        assertEquals(0.8, taxes.getExcise_specific_per_liter());
        assertEquals(30.0, taxes.getCarbon_tax_per_tCO2e());
        assertNotNull(taxes.getCustoms_fees());
    }

    @Test
    void testHs27TaxesWithNotes() {
        // Arrange
        Country.Hs27Taxes taxes = new Country.Hs27Taxes();
        List<String> notes = Arrays.asList("Note 1", "Note 2");

        // Act
        taxes.setNotes(notes);

        // Assert
        assertEquals(notes, taxes.getNotes());
        assertEquals(2, taxes.getNotes().size());
    }

    @Test
    void testHs27TaxesWithSources() {
        // Arrange
        Country.Hs27Taxes taxes = new Country.Hs27Taxes();
        List<String> sources = Arrays.asList("Source 1", "Source 2", "Source 3");

        // Act
        taxes.setSources(sources);

        // Assert
        assertEquals(sources, taxes.getSources());
        assertEquals(3, taxes.getSources().size());
    }

    @Test
    void testCustomsFeesDefaultConstructor() {
        // Act
        Country.CustomsFees fees = new Country.CustomsFees();

        // Assert
        assertNotNull(fees);
        assertNull(fees.getMpf_percent());
        assertNull(fees.getHmf_percent());
        assertNull(fees.getMpf_min());
        assertNull(fees.getMpf_max());
        assertNull(fees.getCurrency());
    }

    @Test
    void testCustomsFeesSettersAndGetters() {
        // Arrange
        Country.CustomsFees fees = new Country.CustomsFees();

        // Act
        fees.setMpf_percent(0.3464);
        fees.setHmf_percent(0.125);
        fees.setMpf_min(27.23);
        fees.setMpf_max(528.33);
        fees.setCurrency("USD");

        // Assert
        assertEquals(0.3464, fees.getMpf_percent());
        assertEquals(0.125, fees.getHmf_percent());
        assertEquals(27.23, fees.getMpf_min());
        assertEquals(528.33, fees.getMpf_max());
        assertEquals("USD", fees.getCurrency());
    }

    @Test
    void testCustomsFeesNormalize_WithNullValues() {
        // Arrange
        Country.CustomsFees fees = new Country.CustomsFees();

        // Act
        fees.normalize();

        // Assert
        assertEquals(0.0, fees.getMpf_percent());
        assertEquals(0.0, fees.getHmf_percent());
        assertEquals(0.0, fees.getMpf_min());
        assertEquals(0.0, fees.getMpf_max());
    }

    @Test
    void testCustomsFeesNormalize_WithExistingValues() {
        // Arrange
        Country.CustomsFees fees = new Country.CustomsFees();
        fees.setMpf_percent(0.5);
        fees.setHmf_percent(0.2);
        fees.setMpf_min(50.0);
        fees.setMpf_max(1000.0);

        // Act
        fees.normalize();

        // Assert
        assertEquals(0.5, fees.getMpf_percent());
        assertEquals(0.2, fees.getHmf_percent());
        assertEquals(50.0, fees.getMpf_min());
        assertEquals(1000.0, fees.getMpf_max());
    }

    @Test
    void testCompleteCountryWithAllNestedObjects() {
        // Arrange
        Country country = new Country();
        Country.Hs27Taxes taxes = new Country.Hs27Taxes();
        Country.CustomsFees fees = new Country.CustomsFees();

        // Act
        country.setName("United States");
        country.setCode("840");
        country.setISO3("USA");
        country.setVatRates(Arrays.asList(new VATRate(LocalDate.now(), 0.0)));

        taxes.setVat_gst_percent(0.0);
        taxes.setExcise_specific_per_liter(0.184);
        taxes.setExcise_currency("USD");
        taxes.setCarbon_tax_per_tCO2e(0.0);
        taxes.setCarbon_tax_currency("USD");

        fees.setMpf_percent(0.3464);
        fees.setHmf_percent(0.125);
        fees.setMpf_min(27.23);
        fees.setMpf_max(528.33);
        fees.setCurrency("USD");

        taxes.setCustoms_fees(fees);
        country.setHs27_taxes(taxes);

        // Assert
        assertEquals("United States", country.getName());
        assertEquals("USA", country.getISO3());
        assertNotNull(country.getHs27_taxes());
        assertNotNull(country.getHs27_taxes().getCustoms_fees());
        assertEquals(0.3464, country.getHs27_taxes().getCustoms_fees().getMpf_percent());
    }

    @Test
    void testZeroVatRate() {
        // Arrange
        Country country = new Country();
        List<VATRate> vatRates = Arrays.asList(new VATRate(LocalDate.now(), 0.0));

        // Act
        country.setVatRates(vatRates);

        // Assert
        assertEquals(vatRates, country.getVatRates());
    }

    @Test
    void testHighVatRate() {
        // Arrange
        Country country = new Country();
        List<VATRate> vatRates = Arrays.asList(new VATRate(LocalDate.now(), 25.0));

        // Act
        country.setVatRates(vatRates);

        // Assert
        assertEquals(vatRates, country.getVatRates());
    }
}
