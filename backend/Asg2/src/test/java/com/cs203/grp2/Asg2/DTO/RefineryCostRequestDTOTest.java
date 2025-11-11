package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RefineryCostRequestDTO covering constructors, getters, and setters.
 */
class RefineryCostRequestDTOTest {

    @Test
    void testNoArgsConstructor() {
        RefineryCostRequestDTO dto = new RefineryCostRequestDTO();
        assertNotNull(dto);
    }

    @Test
    void testGettersAndSetters() {
        RefineryCostRequestDTO dto = new RefineryCostRequestDTO();
        
        dto.setDate("2025-01-01");
        
        Map<String, CostDetailRequestDTO> costs = new HashMap<>();
        CostDetailRequestDTO cost1 = new CostDetailRequestDTO();
        cost1.setCost_per_unit(100.0);
        cost1.setUnit("barrel");
        costs.put("Crude Oil", cost1);
        
        dto.setCosts(costs);
        
        assertEquals("2025-01-01", dto.getDate());
        assertEquals(costs, dto.getCosts());
        assertEquals(1, dto.getCosts().size());
        assertTrue(dto.getCosts().containsKey("Crude Oil"));
    }

    @Test
    void testSetDate_Null() {
        RefineryCostRequestDTO dto = new RefineryCostRequestDTO();
        dto.setDate(null);
        assertNull(dto.getDate());
    }

    @Test
    void testSetDate_EmptyString() {
        RefineryCostRequestDTO dto = new RefineryCostRequestDTO();
        dto.setDate("");
        assertEquals("", dto.getDate());
    }

    @Test
    void testSetDate_VariousFormats() {
        RefineryCostRequestDTO dto = new RefineryCostRequestDTO();
        
        dto.setDate("2025-12-31");
        assertEquals("2025-12-31", dto.getDate());
        
        dto.setDate("01/01/2025");
        assertEquals("01/01/2025", dto.getDate());
    }

    @Test
    void testSetCosts_EmptyMap() {
        RefineryCostRequestDTO dto = new RefineryCostRequestDTO();
        Map<String, CostDetailRequestDTO> emptyMap = new HashMap<>();
        dto.setCosts(emptyMap);
        
        assertNotNull(dto.getCosts());
        assertTrue(dto.getCosts().isEmpty());
    }

    @Test
    void testSetCosts_MultipleCosts() {
        RefineryCostRequestDTO dto = new RefineryCostRequestDTO();
        
        Map<String, CostDetailRequestDTO> costs = new HashMap<>();
        
        CostDetailRequestDTO crude = new CostDetailRequestDTO();
        crude.setCost_per_unit(90.0);
        crude.setUnit("barrel");
        costs.put("Crude Oil", crude);
        
        CostDetailRequestDTO diesel = new CostDetailRequestDTO();
        diesel.setCost_per_unit(85.0);
        diesel.setUnit("gallon");
        costs.put("Diesel", diesel);
        
        dto.setCosts(costs);
        
        assertEquals(2, dto.getCosts().size());
        assertTrue(dto.getCosts().containsKey("Crude Oil"));
        assertTrue(dto.getCosts().containsKey("Diesel"));
    }

    @Test
    void testSetCosts_Null() {
        RefineryCostRequestDTO dto = new RefineryCostRequestDTO();
        dto.setCosts(null);
        assertNull(dto.getCosts());
    }

    @Test
    void testModifyValues() {
        RefineryCostRequestDTO dto = new RefineryCostRequestDTO();
        dto.setDate("2025-01-01");
        
        Map<String, CostDetailRequestDTO> costs1 = new HashMap<>();
        dto.setCosts(costs1);
        
        assertEquals("2025-01-01", dto.getDate());
        assertTrue(dto.getCosts().isEmpty());
        
        dto.setDate("2025-06-01");
        Map<String, CostDetailRequestDTO> costs2 = new HashMap<>();
        CostDetailRequestDTO cost = new CostDetailRequestDTO();
        cost.setCost_per_unit(50.0);
        costs2.put("Gasoline", cost);
        dto.setCosts(costs2);
        
        assertEquals("2025-06-01", dto.getDate());
        assertEquals(1, dto.getCosts().size());
    }

    @Test
    void testGetCosts_ReturnsCorrectReference() {
        RefineryCostRequestDTO dto = new RefineryCostRequestDTO();
        Map<String, CostDetailRequestDTO> costs = new HashMap<>();
        dto.setCosts(costs);
        
        assertSame(costs, dto.getCosts());
    }
}
