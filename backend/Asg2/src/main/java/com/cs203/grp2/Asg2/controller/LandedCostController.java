package com.cs203.grp2.Asg2.controller;

import org.springframework.web.bind.annotation.*;

import com.cs203.grp2.Asg2.DTO.LandedCostRequest;
import com.cs203.grp2.Asg2.DTO.LandedCostResponse;
import com.cs203.grp2.Asg2.service.LandedCostService;

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
            @RequestParam String importer,
            @RequestParam String exporter,
            @RequestParam String hsCode,
            @RequestParam int units) {

        LandedCostRequest request = new LandedCostRequest();

        // Detect if importer is numeric -> ISO3n
        if (importer.matches("\\d+")) {
            request.setImporterIso3n(Integer.parseInt(importer));
        } else {
            request.setImporterName(importer);
        }

        // Detect if exporter is numeric -> ISO3n
        if (exporter.matches("\\d+")) {
            request.setExporterIso3n(Integer.parseInt(exporter));
        } else {
            request.setExporterName(exporter);
        }

        request.setHsCode(hsCode);
        request.setUnits(units);

        return service.calculateLandedCost(request);
    }
}
