package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.service.ShippingFeesService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

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
        
        // Case 2: No date, but with unit -> return all historical costs for that unit only
        if (date == null || date.isEmpty()) {
            return shippingFeesService.getAllCostsByUnit(country1Iso3, country2Iso3, unit);
        }
        
        // Case 3: With date, with unit -> return single most applicable cost for that unit
        LocalDate inputDate = LocalDate.parse(date);
        if (unit != null && !unit.isEmpty()) {
            return shippingFeesService.getCostByUnit(country1Iso3, country2Iso3, unit, inputDate);
        }
        
        // Case 4: With date, no unit -> return single most applicable entry (all units)
        return shippingFeesService.getLatestCost(country1Iso3, country2Iso3, inputDate);
    }

    @PostMapping
    public ShippingFeeResponseDTO addOrUpdateShippingFee(@RequestBody ShippingFeeRequestDTO requestDTO) {
        return shippingFeesService.addOrUpdateShippingFee(requestDTO);
    }
}