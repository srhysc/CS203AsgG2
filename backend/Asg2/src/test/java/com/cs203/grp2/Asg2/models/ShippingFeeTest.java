package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShippingFeeTest {

    private ShippingFee shippingFee;
    private List<ShippingFeeEntry> sampleEntries;

    @BeforeEach
    void setUp() {
        sampleEntries = new ArrayList<>();
        
        // Create cost details for first entry
        Map<String, ShippingCostDetail> costs1 = new HashMap<>();
        costs1.put("barrel", new ShippingCostDetail(15.50, "barrel"));
        costs1.put("ton", new ShippingCostDetail(100.00, "ton"));
        sampleEntries.add(new ShippingFeeEntry(LocalDate.of(2024, 1, 1), costs1));
        
        // Create cost details for second entry
        Map<String, ShippingCostDetail> costs2 = new HashMap<>();
        costs2.put("barrel", new ShippingCostDetail(12.30, "barrel"));
        costs2.put("ton", new ShippingCostDetail(85.00, "ton"));
        sampleEntries.add(new ShippingFeeEntry(LocalDate.of(2024, 6, 1), costs2));
        
        shippingFee = new ShippingFee(
            "Singapore", "SGP", "702",
            "Malaysia", "MYS", "458",
            sampleEntries
        );
    }

    @Test
    void testDefaultConstructor() {
        ShippingFee fee = new ShippingFee();
        assertNotNull(fee);
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(shippingFee);
        assertEquals("Singapore", shippingFee.getCountry1Name());
        assertEquals("SGP", shippingFee.getCountry1Iso3());
        assertEquals("702", shippingFee.getCountry1IsoNumeric());
        assertEquals("Malaysia", shippingFee.getCountry2Name());
        assertEquals("MYS", shippingFee.getCountry2Iso3());
        assertEquals("458", shippingFee.getCountry2IsoNumeric());
        assertEquals(2, shippingFee.getShippingFees().size());
    }

    @Test
    void testGetCountry1Name() {
        assertEquals("Singapore", shippingFee.getCountry1Name());
    }

    @Test
    void testGetCountry1Iso3() {
        assertEquals("SGP", shippingFee.getCountry1Iso3());
    }

    @Test
    void testGetCountry1IsoNumeric() {
        assertEquals("702", shippingFee.getCountry1IsoNumeric());
    }

    @Test
    void testGetCountry2Name() {
        assertEquals("Malaysia", shippingFee.getCountry2Name());
    }

    @Test
    void testGetCountry2Iso3() {
        assertEquals("MYS", shippingFee.getCountry2Iso3());
    }

    @Test
    void testGetCountry2IsoNumeric() {
        assertEquals("458", shippingFee.getCountry2IsoNumeric());
    }

    @Test
    void testGetShippingFees() {
        List<ShippingFeeEntry> fees = shippingFee.getShippingFees();
        assertNotNull(fees);
        assertEquals(2, fees.size());
        assertEquals(LocalDate.of(2024, 1, 1), fees.get(0).getDate());
        assertNotNull(fees.get(0).getCosts());
        assertEquals(15.50, fees.get(0).getCosts().get("barrel").getCostPerUnit());
    }

    @Test
    void testSetShippingFees() {
        List<ShippingFeeEntry> newEntries = new ArrayList<>();
        Map<String, ShippingCostDetail> newCosts = new HashMap<>();
        newCosts.put("barrel", new ShippingCostDetail(10.00, "barrel"));
        newEntries.add(new ShippingFeeEntry(LocalDate.of(2025, 1, 1), newCosts));
        
        shippingFee.setShippingFees(newEntries);
        
        assertEquals(1, shippingFee.getShippingFees().size());
        assertEquals(LocalDate.of(2025, 1, 1), shippingFee.getShippingFees().get(0).getDate());
        assertEquals(10.00, shippingFee.getShippingFees().get(0).getCosts().get("barrel").getCostPerUnit());
    }

    @Test
    void testSetShippingFeesWithEmptyList() {
        shippingFee.setShippingFees(new ArrayList<>());
        assertTrue(shippingFee.getShippingFees().isEmpty());
    }

    @Test
    void testSetShippingFeesWithNull() {
        shippingFee.setShippingFees(null);
        assertNull(shippingFee.getShippingFees());
    }
}
