package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CostDetailRequestDTO covering constructors, getters, and setters.
 */
class CostDetailRequestDTOTest {

    @Test
    void testNoArgsConstructor() {
        CostDetailRequestDTO dto = new CostDetailRequestDTO();
        assertNotNull(dto);
    }

    @Test
    void testGettersAndSetters() {
        CostDetailRequestDTO dto = new CostDetailRequestDTO();
        
        dto.setCost_per_unit(50.5);
        dto.setUnit("barrel");
        
        assertEquals(50.5, dto.getCost_per_unit());
        assertEquals("barrel", dto.getUnit());
    }

    @Test
    void testSetCostPerUnit_Zero() {
        CostDetailRequestDTO dto = new CostDetailRequestDTO();
        dto.setCost_per_unit(0.0);
        assertEquals(0.0, dto.getCost_per_unit());
    }

    @Test
    void testSetCostPerUnit_Negative() {
        CostDetailRequestDTO dto = new CostDetailRequestDTO();
        dto.setCost_per_unit(-10.5);
        assertEquals(-10.5, dto.getCost_per_unit());
    }

    @Test
    void testSetCostPerUnit_LargeValue() {
        CostDetailRequestDTO dto = new CostDetailRequestDTO();
        dto.setCost_per_unit(999999.99);
        assertEquals(999999.99, dto.getCost_per_unit());
    }

    @Test
    void testSetUnit_Null() {
        CostDetailRequestDTO dto = new CostDetailRequestDTO();
        dto.setUnit(null);
        assertNull(dto.getUnit());
    }

    @Test
    void testSetUnit_EmptyString() {
        CostDetailRequestDTO dto = new CostDetailRequestDTO();
        dto.setUnit("");
        assertEquals("", dto.getUnit());
    }

    @Test
    void testSetUnit_VariousValues() {
        CostDetailRequestDTO dto = new CostDetailRequestDTO();
        
        dto.setUnit("liter");
        assertEquals("liter", dto.getUnit());
        
        dto.setUnit("gallon");
        assertEquals("gallon", dto.getUnit());
        
        dto.setUnit("ton");
        assertEquals("ton", dto.getUnit());
    }

    @Test
    void testMultipleSettersChaining() {
        CostDetailRequestDTO dto = new CostDetailRequestDTO();
        dto.setCost_per_unit(100.0);
        dto.setUnit("kg");
        
        assertEquals(100.0, dto.getCost_per_unit());
        assertEquals("kg", dto.getUnit());
        
        // Modify again
        dto.setCost_per_unit(200.0);
        dto.setUnit("lb");
        
        assertEquals(200.0, dto.getCost_per_unit());
        assertEquals("lb", dto.getUnit());
    }
}
