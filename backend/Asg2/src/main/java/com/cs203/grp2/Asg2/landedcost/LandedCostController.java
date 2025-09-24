package com.cs203.grp2.Asg2.landedcost;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/landedcost")
public class LandedCostController {

    private final LandedCostService service;

    public LandedCostController(LandedCostService service) {
        this.service = service;
    }

    // POST endpoint for front-end JSON
    @PostMapping
    public LandedCostResponse calculateLandedCost(@RequestBody LandedCostRequest request) {
        return service.calculateLandedCost(request);
    }

    // GET endpoint for quick browser testing
    @GetMapping
    public LandedCostResponse calculateLandedCostViaGet(
            @RequestParam int importerIso3n,
            @RequestParam int exporterIso3n,
            @RequestParam String hsCode,
            @RequestParam int units) {

        LandedCostRequest request = new LandedCostRequest();
        request.setImporterIso3n(importerIso3n);
        request.setExporterIso3n(exporterIso3n);
        request.setHsCode(hsCode);
        request.setUnits(units);

        return service.calculateLandedCost(request);
    }
}


