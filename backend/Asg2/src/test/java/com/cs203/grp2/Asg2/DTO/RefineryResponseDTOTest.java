package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RefineryResponseDTO covering constructors, getters, and setters.
 */
class RefineryResponseDTOTest {

    @Test
    void testNoArgsConstructor() {
        RefineryResponseDTO dto = new RefineryResponseDTO();
        assertNotNull(dto);
    }

    @Test
    void testParameterizedConstructor_AllFields() {
        List<RefineryCostResponseDTO> costs = new ArrayList<>();
        RefineryCostResponseDTO cost = new RefineryCostResponseDTO("2025-01-01", new HashMap<>());
        costs.add(cost);
        
        RefineryResponseDTO dto = new RefineryResponseDTO(
            "Test Refinery",
            "Test Company",
            "Test Location",
            2000,
            2030,
            true,
            costs,
            "USA",
            "840",
            "United States"
        );
        
        assertEquals("Test Refinery", dto.getName());
        assertEquals("Test Company", dto.getCompany());
        assertEquals("Test Location", dto.getLocation());
        assertEquals(2000, dto.getOperational_from());
        assertEquals(2030, dto.getOperational_to());
        assertTrue(dto.isCan_refine_any());
        assertEquals(1, dto.getEstimated_costs().size());
        assertEquals("USA", dto.getCountryIso3());
        assertEquals("840", dto.getCountryIsoNumeric());
        assertEquals("United States", dto.getCountryName());
    }

    @Test
    void testParameterizedConstructor_NullValues() {
        RefineryResponseDTO dto = new RefineryResponseDTO(
            null, null, null, null, null, false, null, null, null, null
        );
        
        assertNull(dto.getName());
        assertNull(dto.getCompany());
        assertNull(dto.getLocation());
        assertNull(dto.getOperational_from());
        assertNull(dto.getOperational_to());
        assertFalse(dto.isCan_refine_any());
        assertNull(dto.getEstimated_costs());
        assertNull(dto.getCountryIso3());
        assertNull(dto.getCountryIsoNumeric());
        assertNull(dto.getCountryName());
    }

    @Test
    void testParameterizedConstructor_WithComplexCosts() {
        List<RefineryCostResponseDTO> costs = new ArrayList<>();
        Map<String, CostDetailResponseDTO> costDetails = new HashMap<>();
        costDetails.put("Crude Oil", new CostDetailResponseDTO(100.0, "barrel"));
        costs.add(new RefineryCostResponseDTO("2025-01-01", costDetails));
        
        RefineryResponseDTO dto = new RefineryResponseDTO(
            "Advanced Refinery",
            "Shell",
            "Singapore",
            1995,
            2040,
            true,
            costs,
            "SGP",
            "702",
            "Singapore"
        );
        
        assertEquals("Advanced Refinery", dto.getName());
        assertEquals(1, dto.getEstimated_costs().size());
        assertTrue(dto.isCan_refine_any());
    }

    @Test
    void testGettersAndSetters() {
        RefineryResponseDTO dto = new RefineryResponseDTO();
        
        dto.setName("New Refinery");
        dto.setCompany("ExxonMobil");
        dto.setLocation("Houston");
        dto.setOperational_from(2010);
        dto.setOperational_to(2050);
        dto.setCan_refine_any(true);
        dto.setCountryIso3("USA");
        dto.setCountryIsoNumeric("840");
        dto.setCountryName("United States");
        
        List<RefineryCostResponseDTO> costs = new ArrayList<>();
        dto.setEstimated_costs(costs);
        
        assertEquals("New Refinery", dto.getName());
        assertEquals("ExxonMobil", dto.getCompany());
        assertEquals("Houston", dto.getLocation());
        assertEquals(2010, dto.getOperational_from());
        assertEquals(2050, dto.getOperational_to());
        assertTrue(dto.isCan_refine_any());
        assertEquals("USA", dto.getCountryIso3());
        assertEquals("840", dto.getCountryIsoNumeric());
        assertEquals("United States", dto.getCountryName());
        assertNotNull(dto.getEstimated_costs());
    }

    @Test
    void testSetName_Various() {
        RefineryResponseDTO dto = new RefineryResponseDTO();
        dto.setName("Refinery 1");
        assertEquals("Refinery 1", dto.getName());
        
        dto.setName(null);
        assertNull(dto.getName());
        
        dto.setName("");
        assertEquals("", dto.getName());
    }

    @Test
    void testSetCompany_EmptyString() {
        RefineryResponseDTO dto = new RefineryResponseDTO();
        dto.setCompany("");
        assertEquals("", dto.getCompany());
    }

    @Test
    void testSetLocation_Null() {
        RefineryResponseDTO dto = new RefineryResponseDTO();
        dto.setLocation(null);
        assertNull(dto.getLocation());
    }

    @Test
    void testSetOperationalDates() {
        RefineryResponseDTO dto = new RefineryResponseDTO();
        
        dto.setOperational_from(1980);
        dto.setOperational_to(2025);
        assertEquals(1980, dto.getOperational_from());
        assertEquals(2025, dto.getOperational_to());
        
        dto.setOperational_from(null);
        dto.setOperational_to(null);
        assertNull(dto.getOperational_from());
        assertNull(dto.getOperational_to());
    }

    @Test
    void testSetCanRefineAny_Toggle() {
        RefineryResponseDTO dto = new RefineryResponseDTO();
        
        dto.setCan_refine_any(true);
        assertTrue(dto.isCan_refine_any());
        
        dto.setCan_refine_any(false);
        assertFalse(dto.isCan_refine_any());
    }

    @Test
    void testSetEstimatedCosts_EmptyList() {
        RefineryResponseDTO dto = new RefineryResponseDTO();
        dto.setEstimated_costs(new ArrayList<>());
        assertTrue(dto.getEstimated_costs().isEmpty());
    }

    @Test
    void testSetEstimatedCosts_MultipleCosts() {
        RefineryResponseDTO dto = new RefineryResponseDTO();
        
        List<RefineryCostResponseDTO> costs = new ArrayList<>();
        costs.add(new RefineryCostResponseDTO("2025-01-01", new HashMap<>()));
        costs.add(new RefineryCostResponseDTO("2025-06-01", new HashMap<>()));
        costs.add(new RefineryCostResponseDTO("2025-12-01", new HashMap<>()));
        
        dto.setEstimated_costs(costs);
        assertEquals(3, dto.getEstimated_costs().size());
    }

    @Test
    void testSetCountryFields() {
        RefineryResponseDTO dto = new RefineryResponseDTO();
        
        dto.setCountryIso3("CHN");
        dto.setCountryIsoNumeric("156");
        dto.setCountryName("China");
        
        assertEquals("CHN", dto.getCountryIso3());
        assertEquals("156", dto.getCountryIsoNumeric());
        assertEquals("China", dto.getCountryName());
    }

    @Test
    void testModifyAfterConstruction() {
        List<RefineryCostResponseDTO> initialCosts = new ArrayList<>();
        initialCosts.add(new RefineryCostResponseDTO("2025-01-01", new HashMap<>()));
        
        RefineryResponseDTO dto = new RefineryResponseDTO(
            "Initial", "Company1", "Location1", 2000, 2020, true, 
            initialCosts, "USA", "840", "United States"
        );
        
        assertEquals("Initial", dto.getName());
        assertEquals(1, dto.getEstimated_costs().size());
        
        dto.setName("Modified");
        dto.setCompany("Company2");
        List<RefineryCostResponseDTO> newCosts = new ArrayList<>();
        dto.setEstimated_costs(newCosts);
        
        assertEquals("Modified", dto.getName());
        assertEquals("Company2", dto.getCompany());
        assertTrue(dto.getEstimated_costs().isEmpty());
    }

    @Test
    void testCompleteRefineryResponse() {
        List<RefineryCostResponseDTO> costs = new ArrayList<>();
        Map<String, CostDetailResponseDTO> costDetails1 = new HashMap<>();
        costDetails1.put("Crude Oil", new CostDetailResponseDTO(95.5, "barrel"));
        costDetails1.put("Natural Gas", new CostDetailResponseDTO(3.2, "cubic meter"));
        costs.add(new RefineryCostResponseDTO("2025-01-15", costDetails1));
        
        Map<String, CostDetailResponseDTO> costDetails2 = new HashMap<>();
        costDetails2.put("Diesel", new CostDetailResponseDTO(110.0, "gallon"));
        costs.add(new RefineryCostResponseDTO("2025-06-15", costDetails2));
        
        RefineryResponseDTO dto = new RefineryResponseDTO(
            "Global Energy Refinery",
            "BP",
            "Rotterdam",
            1960,
            2060,
            true,
            costs,
            "NLD",
            "528",
            "Netherlands"
        );
        
        assertEquals("Global Energy Refinery", dto.getName());
        assertEquals("BP", dto.getCompany());
        assertEquals("Rotterdam", dto.getLocation());
        assertEquals(1960, dto.getOperational_from());
        assertEquals(2060, dto.getOperational_to());
        assertTrue(dto.isCan_refine_any());
        assertEquals(2, dto.getEstimated_costs().size());
        assertEquals("NLD", dto.getCountryIso3());
        assertEquals("528", dto.getCountryIsoNumeric());
        assertEquals("Netherlands", dto.getCountryName());
    }
}
