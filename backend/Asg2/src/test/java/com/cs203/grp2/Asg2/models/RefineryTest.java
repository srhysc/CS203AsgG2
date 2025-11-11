package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RefineryTest {

    private Refinery refinery;
    private List<RefineryCost> costs;

    @BeforeEach
    void setUp() {
        costs = new ArrayList<>();
        refinery = new Refinery("Shell Refinery", "Shell", "Singapore", 2000, 2050, 
                true, costs, "SGP", "702", "Singapore");
    }

    @Test
    void testDefaultConstructor() {
        Refinery r = new Refinery();
        assertNotNull(r);
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(refinery);
        assertEquals("Shell Refinery", refinery.getName());
        assertEquals("Shell", refinery.getCompany());
        assertEquals("Singapore", refinery.getLocation());
        assertEquals(2000, refinery.getOperational_from());
        assertEquals(2050, refinery.getOperational_to());
        assertTrue(refinery.isCan_refine_any());
        assertEquals(costs, refinery.getEstimated_costs());
        assertEquals("SGP", refinery.getCountryIso3());
        assertEquals("702", refinery.getCountryIsoNumeric());
        assertEquals("Singapore", refinery.getCountryName());
    }

    @Test
    void testGetSetName() {
        refinery.setName("ExxonMobil Refinery");
        assertEquals("ExxonMobil Refinery", refinery.getName());
    }

    @Test
    void testGetSetCompany() {
        refinery.setCompany("ExxonMobil");
        assertEquals("ExxonMobil", refinery.getCompany());
    }

    @Test
    void testGetSetLocation() {
        refinery.setLocation("Texas");
        assertEquals("Texas", refinery.getLocation());
    }

    @Test
    void testGetSetOperationalFrom() {
        refinery.setOperational_from(1990);
        assertEquals(1990, refinery.getOperational_from());
    }

    @Test
    void testGetSetOperationalTo() {
        refinery.setOperational_to(2060);
        assertEquals(2060, refinery.getOperational_to());
    }

    @Test
    void testGetSetCanRefineAny() {
        refinery.setCan_refine_any(false);
        assertFalse(refinery.isCan_refine_any());
    }

    @Test
    void testGetSetEstimatedCosts() {
        List<RefineryCost> newCosts = new ArrayList<>();
        refinery.setEstimated_costs(newCosts);
        assertEquals(newCosts, refinery.getEstimated_costs());
    }

    @Test
    void testGetSetCountryIso3() {
        refinery.setCountryIso3("USA");
        assertEquals("USA", refinery.getCountryIso3());
    }

    @Test
    void testGetSetCountryIsoNumeric() {
        refinery.setCountryIsoNumeric("840");
        assertEquals("840", refinery.getCountryIsoNumeric());
    }

    @Test
    void testGetSetCountryName() {
        refinery.setCountryName("United States");
        assertEquals("United States", refinery.getCountryName());
    }

    @Test
    void testWithNullEstimatedCosts() {
        Refinery r = new Refinery("Test", "Company", "Location", 2000, 2050, 
                true, null, "USA", "840", "United States");
        assertNull(r.getEstimated_costs());
    }

    @Test
    void testWithNullOperationalDates() {
        Refinery r = new Refinery("Test", "Company", "Location", null, null, 
                false, costs, "USA", "840", "United States");
        assertNull(r.getOperational_from());
        assertNull(r.getOperational_to());
    }
}
