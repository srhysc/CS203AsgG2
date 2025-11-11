package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShippingFeeEntryTest {

    private ShippingFeeEntry entry;
    private LocalDate testDate;
    private Map<String, ShippingCostDetail> testCosts;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2024, 1, 1);
        testCosts = new HashMap<>();
        testCosts.put("barrel", new ShippingCostDetail(15.50, "barrel"));
        testCosts.put("ton", new ShippingCostDetail(100.00, "ton"));
        testCosts.put("MMBtu", new ShippingCostDetail(5.25, "MMBtu"));
        
        entry = new ShippingFeeEntry(testDate, testCosts);
    }

    @Test
    void testDefaultConstructor() {
        ShippingFeeEntry entry = new ShippingFeeEntry();
        assertNotNull(entry);
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(entry);
        assertEquals(testDate, entry.getDate());
        assertEquals(3, entry.getCosts().size());
    }

    @Test
    void testGetDate() {
        assertEquals(LocalDate.of(2024, 1, 1), entry.getDate());
    }

    @Test
    void testSetDate() {
        LocalDate newDate = LocalDate.of(2025, 6, 15);
        entry.setDate(newDate);
        assertEquals(newDate, entry.getDate());
    }

    @Test
    void testGetCosts() {
        Map<String, ShippingCostDetail> costs = entry.getCosts();
        assertNotNull(costs);
        assertEquals(3, costs.size());
        assertTrue(costs.containsKey("barrel"));
        assertTrue(costs.containsKey("ton"));
        assertTrue(costs.containsKey("MMBtu"));
    }

    @Test
    void testSetCosts() {
        Map<String, ShippingCostDetail> newCosts = new HashMap<>();
        newCosts.put("barrel", new ShippingCostDetail(20.00, "barrel"));
        
        entry.setCosts(newCosts);
        
        assertEquals(1, entry.getCosts().size());
        assertEquals(20.00, entry.getCosts().get("barrel").getCostPerUnit());
    }

    @Test
    void testGetCostByUnit() {
        assertEquals(15.50, entry.getCosts().get("barrel").getCostPerUnit());
        assertEquals(100.00, entry.getCosts().get("ton").getCostPerUnit());
        assertEquals(5.25, entry.getCosts().get("MMBtu").getCostPerUnit());
    }

    @Test
    void testSetCostsWithEmptyMap() {
        entry.setCosts(new HashMap<>());
        assertTrue(entry.getCosts().isEmpty());
    }

    @Test
    void testSetCostsWithNull() {
        entry.setCosts(null);
        assertNull(entry.getCosts());
    }

    @Test
    void testDateNull() {
        entry.setDate(null);
        assertNull(entry.getDate());
    }
}
