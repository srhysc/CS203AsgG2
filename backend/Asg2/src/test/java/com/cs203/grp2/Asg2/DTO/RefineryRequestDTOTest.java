package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RefineryRequestDTO covering constructors, getters, and setters.
 */
class RefineryRequestDTOTest {

    @Test
    void testNoArgsConstructor() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllGettersAndSetters() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        
        dto.setName("Houston Refinery");
        dto.setCompany("ExxonMobil");
        dto.setLocation("Texas");
        dto.setOperational_from(2000);
        dto.setOperational_to(2030);
        dto.setCan_refine_any(true);
        dto.setCountryIso3("USA");
        dto.setCountryIsoNumeric("840");
        dto.setCountryName("United States");
        
        List<RefineryCostRequestDTO> costs = new ArrayList<>();
        RefineryCostRequestDTO cost = new RefineryCostRequestDTO();
        cost.setDate("2025-01-01");
        costs.add(cost);
        dto.setEstimated_costs(costs);
        
        assertEquals("Houston Refinery", dto.getName());
        assertEquals("ExxonMobil", dto.getCompany());
        assertEquals("Texas", dto.getLocation());
        assertEquals(2000, dto.getOperational_from());
        assertEquals(2030, dto.getOperational_to());
        assertTrue(dto.isCan_refine_any());
        assertEquals("USA", dto.getCountryIso3());
        assertEquals("840", dto.getCountryIsoNumeric());
        assertEquals("United States", dto.getCountryName());
        assertEquals(1, dto.getEstimated_costs().size());
    }

    @Test
    void testSetName_Null() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setName(null);
        assertNull(dto.getName());
    }

    @Test
    void testSetCompany_EmptyString() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setCompany("");
        assertEquals("", dto.getCompany());
    }

    @Test
    void testSetLocation_Various() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setLocation("Singapore");
        assertEquals("Singapore", dto.getLocation());
    }

    @Test
    void testSetOperationalDates_NullValues() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setOperational_from(null);
        dto.setOperational_to(null);
        assertNull(dto.getOperational_from());
        assertNull(dto.getOperational_to());
    }

    @Test
    void testSetOperationalDates_ValidRange() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setOperational_from(1990);
        dto.setOperational_to(2050);
        assertEquals(1990, dto.getOperational_from());
        assertEquals(2050, dto.getOperational_to());
    }

    @Test
    void testSetCanRefineAny_True() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setCan_refine_any(true);
        assertTrue(dto.isCan_refine_any());
    }

    @Test
    void testSetCanRefineAny_False() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setCan_refine_any(false);
        assertFalse(dto.isCan_refine_any());
    }

    @Test
    void testSetEstimatedCosts_EmptyList() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setEstimated_costs(new ArrayList<>());
        assertNotNull(dto.getEstimated_costs());
        assertTrue(dto.getEstimated_costs().isEmpty());
    }

    @Test
    void testSetEstimatedCosts_MultipleCosts() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        
        List<RefineryCostRequestDTO> costs = new ArrayList<>();
        RefineryCostRequestDTO cost1 = new RefineryCostRequestDTO();
        cost1.setDate("2025-01-01");
        RefineryCostRequestDTO cost2 = new RefineryCostRequestDTO();
        cost2.setDate("2025-06-01");
        costs.add(cost1);
        costs.add(cost2);
        
        dto.setEstimated_costs(costs);
        assertEquals(2, dto.getEstimated_costs().size());
    }

    @Test
    void testSetEstimatedCosts_Null() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setEstimated_costs(null);
        assertNull(dto.getEstimated_costs());
    }

    @Test
    void testSetCountryIso3_Null() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setCountryIso3(null);
        assertNull(dto.getCountryIso3());
    }

    @Test
    void testSetCountryIsoNumeric_Various() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setCountryIsoNumeric("702");
        assertEquals("702", dto.getCountryIsoNumeric());
    }

    @Test
    void testSetCountryName_EmptyString() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        dto.setCountryName("");
        assertEquals("", dto.getCountryName());
    }

    @Test
    void testModifyAllFields() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        
        dto.setName("Refinery A");
        dto.setCompany("Company A");
        assertEquals("Refinery A", dto.getName());
        assertEquals("Company A", dto.getCompany());
        
        dto.setName("Refinery B");
        dto.setCompany("Company B");
        assertEquals("Refinery B", dto.getName());
        assertEquals("Company B", dto.getCompany());
    }

    @Test
    void testCompleteRefineryData() {
        RefineryRequestDTO dto = new RefineryRequestDTO();
        
        dto.setName("Singapore Refinery Complex");
        dto.setCompany("Shell");
        dto.setLocation("Jurong Island");
        dto.setOperational_from(1961);
        dto.setOperational_to(2060);
        dto.setCan_refine_any(true);
        dto.setCountryIso3("SGP");
        dto.setCountryIsoNumeric("702");
        dto.setCountryName("Singapore");
        
        List<RefineryCostRequestDTO> costs = new ArrayList<>();
        RefineryCostRequestDTO cost = new RefineryCostRequestDTO();
        cost.setDate("2025-03-15");
        costs.add(cost);
        dto.setEstimated_costs(costs);
        
        assertNotNull(dto);
        assertEquals("Singapore Refinery Complex", dto.getName());
        assertEquals("Shell", dto.getCompany());
        assertEquals("Jurong Island", dto.getLocation());
        assertEquals(1961, dto.getOperational_from());
        assertEquals(2060, dto.getOperational_to());
        assertTrue(dto.isCan_refine_any());
        assertEquals("SGP", dto.getCountryIso3());
        assertEquals("702", dto.getCountryIsoNumeric());
        assertEquals("Singapore", dto.getCountryName());
        assertEquals(1, dto.getEstimated_costs().size());
    }
}
