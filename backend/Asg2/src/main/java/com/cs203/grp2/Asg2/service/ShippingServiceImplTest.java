package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.models.*;
import com.google.firebase.database.FirebaseDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShippingFeesServiceImplTest {

    @Mock
    private FirebaseDatabase firebaseDatabase;

    @InjectMocks
    private ShippingFeesServiceImpl shippingFeesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        shippingFeesService.shippingFeeList.clear();
    }

    @Test
    void getAllShippingFees_ReturnsAllShippingFees() {
        Country c1 = new Country();
        c1.setName("Saudi Arabia");
        c1.setISO3("SAU");
        c1.setCode("682");
        Country c2 = new Country();
        c2.setName("India");
        c2.setISO3("IND");
        c2.setCode("356");
        Map<String, ShippingCostDetail> costs = new HashMap<>();
        costs.put("barrel", new ShippingCostDetail(2.10, "USD per barrel"));
        ShippingFeeEntry entry = new ShippingFeeEntry(LocalDate.of(2008, 4, 19), costs);
        ShippingFee fee = new ShippingFee(c1, c2, List.of(entry));
        shippingFeesService.shippingFeeList.add(fee);

        List<ShippingFeeResponseDTO> result = shippingFeesService.getAllShippingFees();

        assertEquals(1, result.size());
        assertEquals("Saudi Arabia", result.get(0).getCountry1Name());
        assertEquals("India", result.get(0).getCountry2Name());
    }

    @Test
    void getAllCosts_ReturnsAllCostsForCountryPair() {
        Country c1 = new Country();
        c1.setName("USA");
        c1.setISO3("USA");
        c1.setCode("840");
        Country c2 = new Country();
        c2.setName("Japan");
        c2.setISO3("JPN");
        c2.setCode("392");
        Map<String, ShippingCostDetail> costs = new HashMap<>();
        costs.put("barrel", new ShippingCostDetail(2.80, "USD per barrel"));
        ShippingFeeEntry entry = new ShippingFeeEntry(LocalDate.of(2006, 7, 12), costs);
        ShippingFee fee = new ShippingFee(c1, c2, List.of(entry));
        shippingFeesService.shippingFeeList.add(fee);

        List<ShippingFeeEntryResponseDTO> result = shippingFeesService.getAllCosts("USA", "JPN");

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2006, 7, 12), result.get(0).getDate());
    }

    @Test
    void getLatestCost_ReturnsMostApplicableCost() {
        Country c1 = new Country();
        c1.setName("USA");
        c1.setISO3("USA");
        c1.setCode("840");
        Country c2 = new Country();
        c2.setName("Japan");
        c2.setISO3("JPN");
        c2.setCode("392");
        Map<String, ShippingCostDetail> costs1 = new HashMap<>();
        costs1.put("barrel", new ShippingCostDetail(2.80, "USD per barrel"));
        Map<String, ShippingCostDetail> costs2 = new HashMap<>();
        costs2.put("barrel", new ShippingCostDetail(3.05, "USD per barrel"));
        ShippingFeeEntry entry1 = new ShippingFeeEntry(LocalDate.of(2006, 7, 12), costs1);
        ShippingFeeEntry entry2 = new ShippingFeeEntry(LocalDate.of(2013, 11, 25), costs2);
        ShippingFee fee = new ShippingFee(c1, c2, List.of(entry1, entry2));
        shippingFeesService.shippingFeeList.add(fee);

        ShippingFeeEntryResponseDTO result = shippingFeesService.getLatestCost("USA", "JPN", LocalDate.of(2015, 1, 1));

        assertNotNull(result);
        assertEquals(LocalDate.of(2013, 11, 25), result.getDate());
    }

    @Test
    void getCostByUnit_ReturnsCostForUnit() {
        Country c1 = new Country();
        c1.setName("USA");
        c1.setISO3("USA");
        c1.setCode("840");
        Country c2 = new Country();
        c2.setName("Japan");
        c2.setISO3("JPN");
        c2.setCode("392");
        Map<String, ShippingCostDetail> costs = new HashMap<>();
        costs.put("barrel", new ShippingCostDetail(2.80, "USD per barrel"));
        costs.put("MMBtu", new ShippingCostDetail(0.48, "USD per MMBtu"));
        ShippingFeeEntry entry = new ShippingFeeEntry(LocalDate.of(2006, 7, 12), costs);
        ShippingFee fee = new ShippingFee(c1, c2, List.of(entry));
        shippingFeesService.shippingFeeList.add(fee);

        ShippingCostDetailResponseDTO result = shippingFeesService.getCostByUnit("USA", "JPN", "MMBtu", LocalDate.of(2006, 7, 12));

        assertNotNull(result);
        assertEquals(0.48, result.getCostPerUnit());
        assertEquals("USD per MMBtu", result.getUnit());
    }

    @Test
    void addOrUpdateShippingFee_AddsNewCountryPair() {
        ShippingFeeRequestDTO requestDTO = new ShippingFeeRequestDTO();
        requestDTO.setCountry1Iso3("SGP");
        requestDTO.setCountry2Iso3("IDN");
        requestDTO.setCountry1Name("Singapore");
        requestDTO.setCountry2Name("Indonesia");
        requestDTO.setCountry1IsoNumeric("702");
        requestDTO.setCountry2IsoNumeric("360");
        ShippingFeeEntryRequestDTO entryReq = new ShippingFeeEntryRequestDTO();
        entryReq.setDate("2022-01-01");
        Map<String, ShippingCostDetailRequestDTO> costs = new HashMap<>();
        ShippingCostDetailRequestDTO costReq = new ShippingCostDetailRequestDTO();
        costReq.setCostPerUnit(2.50);
        costReq.setUnit("USD per barrel");
        costs.put("barrel", costReq);
        entryReq.setCosts(costs);
        requestDTO.setShippingFees(List.of(entryReq));

        ShippingFeeResponseDTO responseDTO = shippingFeesService.addOrUpdateShippingFee(requestDTO);

        assertNotNull(responseDTO);
        assertEquals("Singapore", responseDTO.getCountry1Name());
        verify(firebaseDatabase, atLeast(0)).getReference(anyString());
    }
}