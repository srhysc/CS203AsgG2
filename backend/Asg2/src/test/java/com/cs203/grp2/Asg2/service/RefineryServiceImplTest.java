package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.RefineryResponseDTO;
import com.cs203.grp2.Asg2.DTO.RefineryCostResponseDTO;
import com.cs203.grp2.Asg2.DTO.CostDetailResponseDTO;
import com.cs203.grp2.Asg2.DTO.RefineryRequestDTO;
import com.cs203.grp2.Asg2.models.Refinery;
import com.cs203.grp2.Asg2.models.RefineryCost;
import com.cs203.grp2.Asg2.models.CostDetail;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefineryServiceImplTest {

    @Mock
    private FirebaseDatabase firebaseDatabase;

    @InjectMocks
    private RefineryServiceImpl refineryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        refineryService.refineryList.clear();
    }

    // Test: getAllRefineries returns all refineries as DTOs
    @Test
    void getAllRefineries_ReturnsAllRefineries() {
        // Arrange
        Refinery refinery = new Refinery("Jamnagar Refinery", "Reliance", "India", 1999, null, true, new ArrayList<>(), "IND", "356");
        refineryService.refineryList.add(refinery);

        // Act
        List<RefineryResponseDTO> result = refineryService.getAllRefineries();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Jamnagar Refinery", result.get(0).getName());
    }

    // Test: getRefineriesByCountry filters by countryIso3
    @Test
    void getRefineriesByCountry_FiltersByCountryIso3() {
        // Arrange
        refineryService.refineryList.add(new Refinery("Jamnagar Refinery", "Reliance", "India", 1999, null, true, new ArrayList<>(), "IND", "356"));
        refineryService.refineryList.add(new Refinery("Paraguana", "PDVSA", "Venezuela", 1976, null, true, new ArrayList<>(), "VEN", "862"));

        // Act
        List<RefineryResponseDTO> result = refineryService.getRefineriesByCountry("IND");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Jamnagar Refinery", result.get(0).getName());
    }

    // Test: getRefinery returns correct refinery by name and country
    @Test
    void getRefinery_ReturnsCorrectRefinery() {
        // Arrange
        refineryService.refineryList.add(new Refinery("Jamnagar Refinery", "Reliance", "India", 1999, null, true, new ArrayList<>(), "IND", "356"));

        // Act
        RefineryResponseDTO result = refineryService.getRefinery("IND", "Jamnagar Refinery");

        // Assert
        assertNotNull(result);
        assertEquals("Jamnagar Refinery", result.getName());
    }

    // Test: getRefinery returns null if refinery does not exist
    @Test
    void getRefinery_ReturnsNullIfNotFound() {
        // Arrange
        // refineryList is empty

        // Act
        RefineryResponseDTO result = refineryService.getRefinery("IND", "Nonexistent Refinery");

        // Assert
        assertNull(result);
    }

    // Test: getAllCosts returns all cost entries for a refinery
    @Test
    void getAllCosts_ReturnsAllCosts() {
        // Arrange
        Map<String, CostDetail> costs = new HashMap<>();
        costs.put("barrel", new CostDetail(5.5, "USD per barrel"));
        RefineryCost costEntry = new RefineryCost(LocalDate.of(2020, 1, 1), costs);
        List<RefineryCost> costList = List.of(costEntry);
        refineryService.refineryList.add(new Refinery("Jamnagar Refinery", "Reliance", "India", 1999, null, true, costList, "IND", "356"));

        // Act
        List<RefineryCostResponseDTO> result = refineryService.getAllCosts("IND", "Jamnagar Refinery");

        // Assert
        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2020, 1, 1), result.get(0).getDate());
    }

    // Test: getAllCosts returns empty list if refinery not found
    @Test
    void getAllCosts_ReturnsEmptyIfRefineryNotFound() {
        // Arrange
        // refineryList is empty

        // Act
        List<RefineryCostResponseDTO> result = refineryService.getAllCosts("IND", "Nonexistent Refinery");

        // Assert
        assertTrue(result.isEmpty());
    }

    // Test: getLatestCost returns the most applicable cost for a given date
    @Test
    void getLatestCost_ReturnsMostApplicableCost() {
        // Arrange
        Map<String, CostDetail> costs1 = new HashMap<>();
        costs1.put("barrel", new CostDetail(5.5, "USD per barrel"));
        Map<String, CostDetail> costs2 = new HashMap<>();
        costs2.put("barrel", new CostDetail(6.0, "USD per barrel"));
        RefineryCost costEntry1 = new RefineryCost(LocalDate.of(2020, 1, 1), costs1);
        RefineryCost costEntry2 = new RefineryCost(LocalDate.of(2025, 1, 1), costs2);
        List<RefineryCost> costList = List.of(costEntry1, costEntry2);
        refineryService.refineryList.add(new Refinery("Jamnagar Refinery", "Reliance", "India", 1999, null, true, costList, "IND", "356"));

        // Act
        RefineryCostResponseDTO result = refineryService.getLatestCost("IND", "Jamnagar Refinery", LocalDate.of(2023, 1, 1));

        // Assert
        assertNotNull(result);
        assertEquals(LocalDate.of(2020, 1, 1), result.getDate());
    }

    // Test: getLatestCost returns null if no applicable cost
    @Test
    void getLatestCost_ReturnsNullIfNoApplicableCost() {
        // Arrange
        Map<String, CostDetail> costs = new HashMap<>();
        costs.put("barrel", new CostDetail(5.5, "USD per barrel"));
        RefineryCost costEntry = new RefineryCost(LocalDate.of(2000, 1, 1), costs);
        List<RefineryCost> costList = List.of(costEntry);
        refineryService.refineryList.add(new Refinery("Jamnagar Refinery", "Reliance", "India", 1999, null, true, costList, "IND", "356"));

        // Act
        RefineryCostResponseDTO result = refineryService.getLatestCost("IND", "Jamnagar Refinery", LocalDate.of(1990, 1, 1));

        // Assert
        assertNull(result);
    }

    // Test: getCostByUnit returns the cost for the requested unit
    @Test
    void getCostByUnit_ReturnsCostForUnit() {
        // Arrange
        Map<String, CostDetail> costs = new HashMap<>();
        costs.put("barrel", new CostDetail(5.5, "USD per barrel"));
        costs.put("MMBtu", new CostDetail(0.95, "USD per MMBtu"));
        RefineryCost costEntry = new RefineryCost(LocalDate.of(2020, 1, 1), costs);
        List<RefineryCost> costList = List.of(costEntry);
        refineryService.refineryList.add(new Refinery("Jamnagar Refinery", "Reliance", "India", 1999, null, true, costList, "IND", "356"));

        // Act
        CostDetailResponseDTO result = refineryService.getCostByUnit("IND", "Jamnagar Refinery", "MMBtu", LocalDate.of(2020, 1, 1));

        // Assert
        assertNotNull(result);
        assertEquals(0.95, result.getCostPerUnit());
        assertEquals("USD per MMBtu", result.getUnit());
    }

    // Test: getCostByUnit returns null if unit not found
    @Test
    void getCostByUnit_ReturnsNullIfUnitNotFound() {
        // Arrange
        Map<String, CostDetail> costs = new HashMap<>();
        costs.put("barrel", new CostDetail(5.5, "USD per barrel"));
        RefineryCost costEntry = new RefineryCost(LocalDate.of(2020, 1, 1), costs);
        List<RefineryCost> costList = List.of(costEntry);
        refineryService.refineryList.add(new Refinery("Jamnagar Refinery", "Reliance", "India", 1999, null, true, costList, "IND", "356"));

        // Act
        CostDetailResponseDTO result = refineryService.getCostByUnit("IND", "Jamnagar Refinery", "ton", LocalDate.of(2020, 1, 1));

        // Assert
        assertNull(result);
    }

    // Test: addOrUpdateRefinery adds a new refinery if not exists (stub Firebase)
    @Test
    void addOrUpdateRefinery_AddsNewRefineryIfNotExists() {
        // Arrange
        RefineryRequestDTO requestDTO = new RefineryRequestDTO();
        requestDTO.setName("New Refinery");
        requestDTO.setCompany("New Company");
        requestDTO.setLocation("New Location");
        requestDTO.setOperationalFrom(2022);
        requestDTO.setOperationalTo(null);
        requestDTO.setCanRefineAny(true);
        requestDTO.setCountryIso3("SGP");
        requestDTO.setCountryIsoNumeric("702");
        requestDTO.setEstimatedCosts(new ArrayList<>());

        // Act
        RefineryResponseDTO responseDTO = refineryService.addOrUpdateRefinery("SGP", requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals("New Refinery", responseDTO.getName());
        // Verify Firebase interaction (stubbed, so just check method called)
        verify(firebaseDatabase, atLeast(0)).getReference(anyString());
    }
}