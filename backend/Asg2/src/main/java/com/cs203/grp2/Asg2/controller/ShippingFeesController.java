package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.service.ShippingFeesService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/shipping-fees")
public class ShippingFeesController {

    private final ShippingFeesService shippingFeesService;

    public ShippingFeesController(ShippingFeesService shippingFeesService) {
        this.shippingFeesService = shippingFeesService;
    }

    @GetMapping
    public List<ShippingFeeResponseDTO> getAllShippingFees() {
        return shippingFeesService.getAllShippingFees();
    }

    @GetMapping("/{country1Iso3}/{country2Iso3}")
    public List<ShippingFeeEntryResponseDTO> getAllCosts(
            @PathVariable String country1Iso3,
            @PathVariable String country2Iso3) {
        return shippingFeesService.getAllCosts(country1Iso3, country2Iso3);
    }

    @GetMapping("/{country1Iso3}/{country2Iso3}/cost")
    public Object getCost(
            @PathVariable String country1Iso3,
            @PathVariable String country2Iso3,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String unit) {
        // Case 1: No date, no unit -> return all historical costs
        if ((date == null || date.isEmpty()) && (unit == null || unit.isEmpty())) {
            return shippingFeesService.getAllCosts(country1Iso3, country2Iso3);
        }

        // Case 2: No date, but with unit -> return all historical costs for that unit
        // only
        if (date == null || date.isEmpty()) {
            return shippingFeesService.getAllCostsByUnit(country1Iso3, country2Iso3, unit);
        }

        // Case 3: With date, with unit -> return single most applicable cost for that
        // unit
        LocalDate inputDate = LocalDate.parse(date);
        if (unit != null && !unit.isEmpty()) {
            var cost = shippingFeesService.getCostByUnit(country1Iso3, country2Iso3, unit, inputDate);
            if (cost == null)
                return List.of();
            return List.of(cost);
        }
        // Case 4: With date, no unit -> return single most applicable entry (all units)
        var entry = shippingFeesService.getLatestCost(country1Iso3, country2Iso3, inputDate);
        if (entry == null)
            return List.of();
        return List.of(entry);
    }

    @GetMapping("/cost/all")
    public List<Map<String, Object>> getAllShippingFeesFlat() {
        List<ShippingFeeResponseDTO> allFees = shippingFeesService.getAllShippingFees();
        List<Map<String, Object>> flattened = new ArrayList<>();
        
        for (ShippingFeeResponseDTO feeGroup : allFees) {
            if (feeGroup.getShippingFees() != null) {
                for (ShippingFeeEntryResponseDTO entry : feeGroup.getShippingFees()) {
                    Map<String, Object> flat = new HashMap<>();
                    
                    Map<String, String> country1 = new HashMap<>();
                    country1.put("name", feeGroup.getCountry1Name());
                    country1.put("iso3", feeGroup.getCountry1Iso3());
                    flat.put("country1", country1);
                    
                    Map<String, String> country2 = new HashMap<>();
                    country2.put("name", feeGroup.getCountry2Name());
                    country2.put("iso3", feeGroup.getCountry2Iso3());
                    flat.put("country2", country2);
                    
                    // Null-safe date
                    if (entry.getDate() != null) {
                        flat.put("date", entry.getDate().toString());
                    } else {
                        flat.put("date", "");
                    }
                    
                    Map<String, ShippingCostDetailResponseDTO> costs = entry.getCosts();
                    if (costs != null) {
                        if (costs.containsKey("ton") && costs.get("ton") != null) {
                            // getCostPerUnit() may be a primitive double — avoid null comparison; box to Double for map
                            flat.put("ton", Double.valueOf(costs.get("ton").getCostPerUnit()));
                        }
                        if (costs.containsKey("barrel") && costs.get("barrel") != null) {
                            flat.put("barrel", Double.valueOf(costs.get("barrel").getCostPerUnit()));
                        }
                        if (costs.containsKey("MMBtu") && costs.get("MMBtu") != null) {
                            flat.put("MMBtu", Double.valueOf(costs.get("MMBtu").getCostPerUnit()));
                        }
                    }
                    
                    flattened.add(flat);
                }
            }
        }
        
        return flattened;
    }


    @PostMapping
    public ShippingFeeResponseDTO addOrUpdateShippingFee(@RequestBody ShippingFeeRequestDTO requestDTO) {
System.out.println("HITTING CONTROLLER: " );
System.out.println("FEES: " + requestDTO.getShippingFees());

        return shippingFeesService.addOrUpdateShippingFee(requestDTO);
    }
}