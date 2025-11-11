package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConvertToTest {

    private ConvertTo convertTo;

    @BeforeEach
    void setUp() {
        convertTo = new ConvertTo("271000", "Gasoline", 45);
    }

    @Test
    void testDefaultConstructor() {
        ConvertTo ct = new ConvertTo();
        assertNotNull(ct);
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(convertTo);
        assertEquals("271000", convertTo.getHscode());
        assertEquals("Gasoline", convertTo.getName());
        assertEquals(45, convertTo.getYield_percent());
    }

    @Test
    void testGetHscode() {
        assertEquals("271000", convertTo.getHscode());
    }

    @Test
    void testSetHscode() {
        convertTo.setHscode("271200");
        assertEquals("271200", convertTo.getHscode());
    }

    @Test
    void testGetName() {
        assertEquals("Gasoline", convertTo.getName());
    }

    @Test
    void testSetName() {
        convertTo.setName("Diesel");
        assertEquals("Diesel", convertTo.getName());
    }

    @Test
    void testGetYieldPercent() {
        assertEquals(45, convertTo.getYield_percent());
    }

    @Test
    void testSetYieldPercent() {
        convertTo.setYield_percent(30);
        assertEquals(30, convertTo.getYield_percent());
    }

    @Test
    void testWithZeroYieldPercent() {
        ConvertTo ct = new ConvertTo("271000", "Product", 0);
        assertEquals(0, ct.getYield_percent());
    }

    @Test
    void testWithHighYieldPercent() {
        ConvertTo ct = new ConvertTo("271000", "Product", 100);
        assertEquals(100, ct.getYield_percent());
    }

    @Test
    void testWithNullHscode() {
        ConvertTo ct = new ConvertTo(null, "Product", 50);
        assertNull(ct.getHscode());
    }

    @Test
    void testWithNullName() {
        ConvertTo ct = new ConvertTo("271000", null, 50);
        assertNull(ct.getName());
    }

    @Test
    void testWithEmptyHscode() {
        ConvertTo ct = new ConvertTo("", "Product", 50);
        assertEquals("", ct.getHscode());
    }
}
