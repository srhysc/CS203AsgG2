package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShippingCostDetailTest {

    private ShippingCostDetail costDetail;

    @BeforeEach
    void setUp() {
        costDetail = new ShippingCostDetail(15.50, "barrel");
    }

    @Test
    void testDefaultConstructor() {
        ShippingCostDetail detail = new ShippingCostDetail();
        assertNotNull(detail);
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(costDetail);
        assertEquals(15.50, costDetail.getCostPerUnit());
        assertEquals("barrel", costDetail.getUnit());
    }

    @Test
    void testGetCostPerUnit() {
        assertEquals(15.50, costDetail.getCostPerUnit());
    }

    @Test
    void testSetCostPerUnit() {
        costDetail.setCostPerUnit(20.75);
        assertEquals(20.75, costDetail.getCostPerUnit());
    }

    @Test
    void testSetCostPerUnitZero() {
        costDetail.setCostPerUnit(0.0);
        assertEquals(0.0, costDetail.getCostPerUnit());
    }

    @Test
    void testSetCostPerUnitNegative() {
        costDetail.setCostPerUnit(-5.0);
        assertEquals(-5.0, costDetail.getCostPerUnit());
    }

    @Test
    void testGetUnit() {
        assertEquals("barrel", costDetail.getUnit());
    }

    @Test
    void testSetUnit() {
        costDetail.setUnit("ton");
        assertEquals("ton", costDetail.getUnit());
    }

    @Test
    void testSetUnitMMBtu() {
        costDetail.setUnit("MMBtu");
        assertEquals("mmbtu", costDetail.getUnit());
    }

    @Test
    void testSetUnitNull() {
        costDetail.setUnit(null);
        assertNull(costDetail.getUnit());
    }

    @Test
    void testSetUnitEmpty() {
        costDetail.setUnit("");
        assertEquals("", costDetail.getUnit());
    }

    @Test
    void testDifferentUnits() {
        ShippingCostDetail barrel = new ShippingCostDetail(15.50, "barrel");
        ShippingCostDetail ton = new ShippingCostDetail(100.00, "ton");
        ShippingCostDetail mmbtu = new ShippingCostDetail(5.25, "MMBtu");
        
        assertEquals("barrel", barrel.getUnit());
        assertEquals("ton", ton.getUnit());
        assertEquals("MMBtu", mmbtu.getUnit());
        
        assertEquals(15.50, barrel.getCostPerUnit());
        assertEquals(100.00, ton.getCostPerUnit());
        assertEquals(5.25, mmbtu.getCostPerUnit());
    }
}
