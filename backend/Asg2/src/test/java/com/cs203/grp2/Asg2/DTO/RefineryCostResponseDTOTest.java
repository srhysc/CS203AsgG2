package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RefineryCostResponseDTO covering constructors, getters, and setters.
 */
class RefineryCostResponseDTOTest {

    @Test
    void testNoArgsConstructor() {
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO();
        assertNotNull(dto);
    }

    @Test
    void testParameterizedConstructor() {
        Map<String, CostDetailResponseDTO> costs = new HashMap<>();
        CostDetailResponseDTO cost = new CostDetailResponseDTO(100.0, "barrel");
        costs.put("Crude Oil", cost);
        
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO("2025-01-15", costs);
        
        assertEquals("2025-01-15", dto.getDate());
        assertEquals(costs, dto.getCosts());
        assertEquals(1, dto.getCosts().size());
    }

    @Test
    void testParameterizedConstructor_NullValues() {
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO(null, null);
        
        assertNull(dto.getDate());
        assertNull(dto.getCosts());
    }

    @Test
    void testParameterizedConstructor_EmptyMap() {
        Map<String, CostDetailResponseDTO> emptyCosts = new HashMap<>();
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO("2025-01-01", emptyCosts);
        
        assertEquals("2025-01-01", dto.getDate());
        assertNotNull(dto.getCosts());
        assertTrue(dto.getCosts().isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO();
        
        dto.setDate("2025-03-20");
        
        Map<String, CostDetailResponseDTO> costs = new HashMap<>();
        CostDetailResponseDTO diesel = new CostDetailResponseDTO(95.5, "gallon");
        costs.put("Diesel", diesel);
        
        dto.setCosts(costs);
        
        assertEquals("2025-03-20", dto.getDate());
        assertEquals(1, dto.getCosts().size());
        assertTrue(dto.getCosts().containsKey("Diesel"));
    }

    @Test
    void testSetDate_VariousFormats() {
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO();
        
        dto.setDate("2025-12-31");
        assertEquals("2025-12-31", dto.getDate());
        
        dto.setDate("31-12-2025");
        assertEquals("31-12-2025", dto.getDate());
    }

    @Test
    void testSetDate_Null() {
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO();
        dto.setDate(null);
        assertNull(dto.getDate());
    }

    @Test
    void testSetCosts_MultipleCosts() {
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO();
        
        Map<String, CostDetailResponseDTO> costs = new HashMap<>();
        costs.put("Crude Oil", new CostDetailResponseDTO(90.0, "barrel"));
        costs.put("Gasoline", new CostDetailResponseDTO(110.0, "liter"));
        costs.put("Diesel", new CostDetailResponseDTO(95.0, "liter"));
        
        dto.setCosts(costs);
        
        assertEquals(3, dto.getCosts().size());
        assertTrue(dto.getCosts().containsKey("Crude Oil"));
        assertTrue(dto.getCosts().containsKey("Gasoline"));
        assertTrue(dto.getCosts().containsKey("Diesel"));
    }

    @Test
    void testSetCosts_Null() {
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO();
        dto.setCosts(null);
        assertNull(dto.getCosts());
    }

    @Test
    void testModifyAfterConstruction() {
        Map<String, CostDetailResponseDTO> initialCosts = new HashMap<>();
        initialCosts.put("Crude Oil", new CostDetailResponseDTO(100.0, "barrel"));
        
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO("2025-01-01", initialCosts);
        
        assertEquals("2025-01-01", dto.getDate());
        assertEquals(1, dto.getCosts().size());
        
        dto.setDate("2025-06-01");
        Map<String, CostDetailResponseDTO> newCosts = new HashMap<>();
        newCosts.put("Diesel", new CostDetailResponseDTO(85.0, "gallon"));
        dto.setCosts(newCosts);
        
        assertEquals("2025-06-01", dto.getDate());
        assertEquals(1, dto.getCosts().size());
        assertTrue(dto.getCosts().containsKey("Diesel"));
        assertFalse(dto.getCosts().containsKey("Crude Oil"));
    }

    @Test
    void testGetCosts_ReturnsCorrectReference() {
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO();
        Map<String, CostDetailResponseDTO> costs = new HashMap<>();
        dto.setCosts(costs);
        
        assertSame(costs, dto.getCosts());
    }

    @Test
    void testConstructor_WithComplexCosts() {
        Map<String, CostDetailResponseDTO> costs = new HashMap<>();
        costs.put("Heavy Crude", new CostDetailResponseDTO(120.5, "barrel"));
        costs.put("Light Crude", new CostDetailResponseDTO(135.75, "barrel"));
        costs.put("Natural Gas", new CostDetailResponseDTO(3.25, "cubic meter"));
        
        RefineryCostResponseDTO dto = new RefineryCostResponseDTO("2025-07-15", costs);
        
        assertEquals("2025-07-15", dto.getDate());
        assertEquals(3, dto.getCosts().size());
        assertEquals(120.5, dto.getCosts().get("Heavy Crude").getCost_per_unit());
        assertEquals(135.75, dto.getCosts().get("Light Crude").getCost_per_unit());
    }
}
