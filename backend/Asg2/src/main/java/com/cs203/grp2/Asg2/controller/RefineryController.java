package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.RefineryResponseDTO;
import com.cs203.grp2.Asg2.DTO.CostDetailResponseDTO;
import com.cs203.grp2.Asg2.DTO.RefineryCostResponseDTO;
import com.cs203.grp2.Asg2.DTO.RefineryRequestDTO;
import com.cs203.grp2.Asg2.service.RefineryService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for refinery endpoints.
 */
@RestController
@RequestMapping("/refineries")
public class RefineryController {

    private final RefineryService refineryService;

    public RefineryController(RefineryService refineryService) {
        this.refineryService = refineryService;
    }

    /**
     * GET /refineries
     * Returns all refineries with country info for dropdown.
     */
    @GetMapping
    public List<RefineryResponseDTO> getAllRefineries() {
        return refineryService.getAllRefineries();
    }

    /**
     * GET /refineries/{countryIso3}
     * Returns all refineries for a country.
     */
    @GetMapping("/{countryIso3}")
    public List<RefineryResponseDTO> getRefineriesByCountry(@PathVariable String countryIso3) {
        return refineryService.getRefineriesByCountry(countryIso3);
    }

    /**
     * GET /refineries/{countryIso3}/{refineryName}
     * Returns details for a specific refinery.
     */
    @GetMapping("/{countryIso3}/{refineryName}")
    public RefineryResponseDTO getRefinery(
            @PathVariable String countryIso3,
            @PathVariable String refineryName) {
        return refineryService.getRefinery(countryIso3, refineryName);
    }

    /**
     * GET /refineries/{countryIso3}/{refineryName}/costs
     * Returns all historical costs for a refinery.
     */
    @GetMapping("/{countryIso3}/{refineryName}/costs")
    public List<RefineryCostResponseDTO> getAllCosts(
            @PathVariable String countryIso3,
            @PathVariable String refineryName) {
        return refineryService.getAllCosts(countryIso3, refineryName);
    }

    /**
     * GET /refineries/{countryIso3}/{refineryName}/cost?date=YYYY-MM-DD
     * Returns the most applicable cost for a refinery on or before the given date.
     */
    @GetMapping("/{countryIso3}/{refineryName}/cost")
    public RefineryCostResponseDTO getLatestCost(
            @PathVariable String countryIso3,
            @PathVariable String refineryName,
            @RequestParam(required = false) String date) {
        if (date == null || date.isEmpty()) {
            // If no date, return null or handle as needed
            return null;
        }
        LocalDate inputDate = LocalDate.parse(date);
        return refineryService.getLatestCost(countryIso3, refineryName, inputDate);
    }

    /**
     * GET
     * /refineries/{countryIso3}/{refineryName}/cost/unit?date=YYYY-MM-DD&unit=barrel
     * Returns the cost for a refinery in a specific unit on or before the given
     * date.
     */
    @GetMapping("/{countryIso3}/{refineryName}/cost/unit")
    public CostDetailResponseDTO getCostByUnit(
            @PathVariable String countryIso3,
            @PathVariable String refineryName,
            @RequestParam String unit,
            @RequestParam(required = false) String date) {
        LocalDate inputDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        return refineryService.getCostByUnit(countryIso3, refineryName, unit, inputDate);
    }

    /**
     * POST /refineries/{countryIso3}/add
     * Adds a new refinery or updates an existing refinery for the specified
     * country.
     * - If the country and refinery exist, adds a new estimated cost entry to the
     * refinery.
     * - If the country exists but the refinery does not, creates a new refinery
     * under that country.
     * - If the country does not exist, creates the country and adds the refinery.
     * Only accessible to admin users.
     *
     * @param countryIso3        ISO3 code of the country
     * @param refineryRequestDTO Refinery details and estimated costs
     * @return The created or updated RefineryResponseDTO
     */
    @PostMapping("/{countryIso3}/add")
    public RefineryResponseDTO addOrUpdateRefinery(
            @PathVariable String countryIso3,
            @RequestBody RefineryRequestDTO refineryRequestDTO) {
        return refineryService.addOrUpdateRefinery(countryIso3, refineryRequestDTO);
    }
}
