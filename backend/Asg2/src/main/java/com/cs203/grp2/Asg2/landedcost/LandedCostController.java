package com.cs203.grp2.Asg2.landedcost;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/landedcost")
public class LandedCostController {

    private final LandedCostService landedCostService;

    public LandedCostController(LandedCostService landedCostService) {
        this.landedCostService = landedCostService;
    }

    @GetMapping
    public LandedCostResponse getLandedCost(
            @RequestParam @Min(1) int importerIso6,
            @RequestParam @Min(1) int exporterIso6,
            @RequestParam @Pattern(regexp = "\\d{6}") String hsCode,
            @RequestParam @Min(1) int units
    ) {
        LandedCost landedCost = landedCostService.calculateLandedCost(hsCode, exporterIso6, importerIso6, units);
        return new LandedCostResponse(
                landedCost.getProductHsCode(),
                landedCost.getImporterIso6Code(),
                landedCost.getExporterIso6Code(),
                landedCost.getUnits(),
                landedCost.getTotalLandedCost()
        );
    }
}
