package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CostDetailTest {

    private CostDetail costDetail;

    @BeforeEach
    void setUp() {
        costDetail = new CostDetail(50.0, "barrel");
    }

    @Test
    void testDefaultConstructor() {
        CostDetail cd = new CostDetail();
        assertNotNull(cd);
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(costDetail);
        assertEquals(50.0, costDetail.getCost_per_unit());
        assertEquals("barrel", costDetail.getUnit());
    }

    @Test
    void testGetCostPerUnit() {
        assertEquals(50.0, costDetail.getCost_per_unit());
    }

    @Test
    void testSetCostPerUnit() {
        costDetail.setCost_per_unit(75.5);
        assertEquals(75.5, costDetail.getCost_per_unit());
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
    void testWithZeroCost() {
        CostDetail cd = new CostDetail(0.0, "barrel");
        assertEquals(0.0, cd.getCost_per_unit());
    }

    @Test
    void testWithNegativeCost() {
        CostDetail cd = new CostDetail(-10.0, "barrel");
        assertEquals(-10.0, cd.getCost_per_unit());
    }

    @Test
    void testWithLargeCost() {
        CostDetail cd = new CostDetail(999999.99, "ton");
        assertEquals(999999.99, cd.getCost_per_unit());
    }

    @Test
    void testWithDifferentUnits_Barrel() {
        costDetail.setUnit("barrel");
        assertEquals("barrel", costDetail.getUnit());
    }

    @Test
    void testWithDifferentUnits_Ton() {
        costDetail.setUnit("ton");
        assertEquals("ton", costDetail.getUnit());
    }

    @Test
    void testWithDifferentUnits_MMBtu() {
        costDetail.setUnit("MMBtu");
        assertEquals("MMBtu", costDetail.getUnit());
    }

    @Test
    void testWithNullUnit() {
        CostDetail cd = new CostDetail(50.0, null);
        assertNull(cd.getUnit());
    }

    @Test
    void testWithEmptyUnit() {
        CostDetail cd = new CostDetail(50.0, "");
        assertEquals("", cd.getUnit());
    }
}
