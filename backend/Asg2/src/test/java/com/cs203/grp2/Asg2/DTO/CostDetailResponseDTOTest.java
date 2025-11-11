package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CostDetailResponseDTO covering constructors, getters, and setters.
 */
class CostDetailResponseDTOTest {

    @Test
    void testNoArgsConstructor() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO();
        assertNotNull(dto);
    }

    @Test
    void testParameterizedConstructor() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO(75.5, "liter");
        
        assertEquals(75.5, dto.getCost_per_unit());
        assertEquals("liter", dto.getUnit());
    }

    @Test
    void testParameterizedConstructor_ZeroCost() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO(0.0, "unit");
        
        assertEquals(0.0, dto.getCost_per_unit());
        assertEquals("unit", dto.getUnit());
    }

    @Test
    void testParameterizedConstructor_NullUnit() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO(50.0, null);
        
        assertEquals(50.0, dto.getCost_per_unit());
        assertNull(dto.getUnit());
    }

    @Test
    void testGettersAndSetters() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO();
        
        dto.setCost_per_unit(100.25);
        dto.setUnit("barrel");
        
        assertEquals(100.25, dto.getCost_per_unit());
        assertEquals("barrel", dto.getUnit());
    }

    @Test
    void testSetCostPerUnit_NegativeValue() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO();
        dto.setCost_per_unit(-25.5);
        assertEquals(-25.5, dto.getCost_per_unit());
    }

    @Test
    void testSetCostPerUnit_LargeValue() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO();
        dto.setCost_per_unit(1000000.99);
        assertEquals(1000000.99, dto.getCost_per_unit());
    }

    @Test
    void testSetUnit_EmptyString() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO();
        dto.setUnit("");
        assertEquals("", dto.getUnit());
    }

    @Test
    void testSetUnit_VariousUnits() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO();
        
        dto.setUnit("gallon");
        assertEquals("gallon", dto.getUnit());
        
        dto.setUnit("cubic meter");
        assertEquals("cubic meter", dto.getUnit());
    }

    @Test
    void testModifyAfterConstruction() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO(50.0, "kg");
        
        assertEquals(50.0, dto.getCost_per_unit());
        assertEquals("kg", dto.getUnit());
        
        dto.setCost_per_unit(75.0);
        dto.setUnit("lb");
        
        assertEquals(75.0, dto.getCost_per_unit());
        assertEquals("lb", dto.getUnit());
    }

    @Test
    void testConstructor_WithDecimalPrecision() {
        CostDetailResponseDTO dto = new CostDetailResponseDTO(123.456789, "unit");
        assertEquals(123.456789, dto.getCost_per_unit(), 0.000001);
    }
}
