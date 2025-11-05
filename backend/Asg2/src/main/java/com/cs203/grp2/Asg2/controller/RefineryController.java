package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.*;
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
        return refineryService.getAllRefineries();
    }

    @GetMapping("/{countryIso3}")
    public List<RefineryResponseDTO> getRefineriesByCountry(@PathVariable String countryIso3) {
        return refineryService.getRefineriesByCountry(countryIso3);
    }

    @GetMapping("/{countryIso3}/{refineryName}")
    public RefineryResponseDTO getRefinery(@PathVariable String countryIso3, @PathVariable String refineryName) {
        return refineryService.getRefinery(countryIso3, refineryName);
    }

    @GetMapping("/{countryIso3}/{refineryName}/costs")
    public List<RefineryCostResponseDTO> getAllCosts(@PathVariable String countryIso3,
            @PathVariable String refineryName) {
        return refineryService.getAllCosts(countryIso3, refineryName);
    }

    @GetMapping("/{countryIso3}/{refineryName}/costs/latest")
    public List<RefineryCostResponseDTO> getLatestCost(
            @PathVariable String countryIso3,
            @PathVariable String refineryName,
            @RequestParam(required = false) String date) {
        return refineryService.getLatestCost(countryIso3, refineryName, date);
    }

    @GetMapping("/{countryIso3}/{refineryName}/costs/unit")
    public CostDetailResponseDTO getCostByUnit(@PathVariable String countryIso3, @PathVariable String refineryName,
            @RequestParam String unit, @RequestParam(required = false) String date) {
        return refineryService.getCostByUnit(countryIso3, refineryName, unit, date);
    }

    @PostMapping("/{countryIso3}")
    public RefineryResponseDTO addOrUpdateRefinery(
            @PathVariable String countryIso3,
            @RequestBody RefineryRequestDTO refineryRequestDTO) {
        return refineryService.addOrUpdateRefinery(countryIso3, refineryRequestDTO);
    }
}