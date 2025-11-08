package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException;
import com.cs203.grp2.Asg2.exceptions.RefineryNotFoundException;
import com.cs203.grp2.Asg2.service.RefineryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/refineries")
public class RefineryController {

    @Autowired
    private RefineryService refineryService;

    @GetMapping
    public List<RefineryResponseDTO> getAllRefineries() {
        List<RefineryResponseDTO> list = refineryService.getAllRefineries();
        if (list == null || list.isEmpty()) {
            throw new RefineryNotFoundException("No refineries found.");
        }
        return list;
    }

    @GetMapping("/{countryIso3}")
    public List<RefineryResponseDTO> getRefineriesByCountry(@PathVariable String countryIso3) {
        List<RefineryResponseDTO> list = refineryService.getRefineriesByCountry(countryIso3);
        if (list == null || list.isEmpty()) {
            throw new RefineryNotFoundException("No refineries found for country: " + countryIso3);
        }
        return list;
    }

    @GetMapping("/{countryIso3}/{refineryName}")
    public RefineryResponseDTO getRefinery(@PathVariable String countryIso3, @PathVariable String refineryName) {
        RefineryResponseDTO refinery = refineryService.getRefinery(countryIso3, refineryName);
        if (refinery == null) {
            throw new RefineryNotFoundException("Refinery not found: " + refineryName + " in " + countryIso3);
        }
        return refinery;
    }

    @GetMapping("/{countryIso3}/{refineryName}/costs")
    public List<RefineryCostResponseDTO> getAllCosts(@PathVariable String countryIso3,
            @PathVariable String refineryName) {
        List<RefineryCostResponseDTO> costs = refineryService.getAllCosts(countryIso3, refineryName);
        if (costs == null || costs.isEmpty()) {
            throw new RefineryNotFoundException("No costs found for refinery: " + refineryName + " in " + countryIso3);
        }
        return costs;
    }

    @GetMapping("/{countryIso3}/{refineryName}/costs/latest")
    public List<RefineryCostResponseDTO> getLatestCost(
            @PathVariable String countryIso3,
            @PathVariable String refineryName,
            @RequestParam(required = false) String date) {
        List<RefineryCostResponseDTO> costs = refineryService.getLatestCost(countryIso3, refineryName, date);
        if (costs == null || costs.isEmpty()) {
            throw new RefineryNotFoundException("No latest cost found for refinery: " + refineryName + " in " + countryIso3);
        }
        return costs;
    }

    @GetMapping("/{countryIso3}/{refineryName}/costs/unit")
    public CostDetailResponseDTO getCostByUnit(@PathVariable String countryIso3, @PathVariable String refineryName,
            @RequestParam String unit, @RequestParam(required = false) String date) {
       if (unit == null || unit.isEmpty()) {
            throw new GeneralBadRequestException("Unit parameter is required.");
        }
        CostDetailResponseDTO cost = refineryService.getCostByUnit(countryIso3, refineryName, unit, date);
        if (cost == null) {
            throw new RefineryNotFoundException("No cost found for unit: " + unit + " in refinery: " + refineryName + " (" + countryIso3 + ")");
        }
        return cost;
    }

    @PostMapping("/{countryIso3}")
    public RefineryResponseDTO addOrUpdateRefinery(
            @PathVariable String countryIso3,
            @RequestBody RefineryRequestDTO refineryRequestDTO) {
        if (refineryRequestDTO == null) {
            throw new GeneralBadRequestException("Refinery request body is required.");
        }
        RefineryResponseDTO refinery = refineryService.addOrUpdateRefinery(countryIso3, refineryRequestDTO);
        if (refinery == null) {
            throw new RefineryNotFoundException("Failed to add or update refinery for country: " + countryIso3);
        }
        return refinery;
    }
}