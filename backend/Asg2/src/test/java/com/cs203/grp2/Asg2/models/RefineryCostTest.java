package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RefineryCostTest {

    private RefineryCost refineryCost;
    private Map<String, CostDetail> costs;

    @BeforeEach
    void setUp() {
        costs = new HashMap<>();
        costs.put("barrel", new CostDetail(50.0, "barrel"));
        costs.put("ton", new CostDetail(400.0, "ton"));
        
        refineryCost = new RefineryCost("2024-01-01", costs);
    }

    @Test
    void testDefaultConstructor() {
        RefineryCost rc = new RefineryCost();
        assertNotNull(rc);
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(refineryCost);
        assertEquals("2024-01-01", refineryCost.getDate());
        assertEquals(costs, refineryCost.getCosts());
    }

    @Test
    void testGetDate() {
        assertEquals("2024-01-01", refineryCost.getDate());
    }

    @Test
    void testSetDate() {
        refineryCost.setDate("2024-06-01");
        assertEquals("2024-06-01", refineryCost.getDate());
    }

    @Test
    void testGetCosts() {
        Map<String, CostDetail> retrievedCosts = refineryCost.getCosts();
        assertEquals(2, retrievedCosts.size());
        assertTrue(retrievedCosts.containsKey("barrel"));
        assertTrue(retrievedCosts.containsKey("ton"));
    }

    @Test
    void testSetCosts() {
        Map<String, CostDetail> newCosts = new HashMap<>();
        newCosts.put("MMBtu", new CostDetail(10.0, "MMBtu"));
        
        refineryCost.setCosts(newCosts);
        assertEquals(newCosts, refineryCost.getCosts());
        assertEquals(1, refineryCost.getCosts().size());
    }

    @Test
    void testWithEmptyCostsMap() {
        RefineryCost rc = new RefineryCost("2024-01-01", new HashMap<>());
        assertNotNull(rc.getCosts());
        assertEquals(0, rc.getCosts().size());
    }

    @Test
    void testWithNullDate() {
        RefineryCost rc = new RefineryCost(null, costs);
        assertNull(rc.getDate());
    }

    @Test
    void testWithNullCosts() {
        RefineryCost rc = new RefineryCost("2024-01-01", null);
        assertNull(rc.getCosts());
    }

    @Test
    void testModifyCostsMap() {
        Map<String, CostDetail> retrievedCosts = refineryCost.getCosts();
        retrievedCosts.put("MMBtu", new CostDetail(10.0, "MMBtu"));
        
        assertEquals(3, refineryCost.getCosts().size());
        assertTrue(refineryCost.getCosts().containsKey("MMBtu"));
    }
}
