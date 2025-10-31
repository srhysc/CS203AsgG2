package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.service.RouteOptimizationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route-optimization")
public class RouteOptimizationController {

    private final RouteOptimizationService service;

    public RouteOptimizationController(RouteOptimizationService service) {
        this.service = service;
    }

    // POST endpoint (for frontend or JSON clients)
    @PostMapping
    public RouteOptimizationResponse calculateRoutes(@RequestBody RouteOptimizationRequest request) {
        return service.calculateOptimalRoutes(request);
    }

    // GET endpoint (for Swagger/browser testing)
    @GetMapping
    public RouteOptimizationResponse calculateRoutesViaGet(
            @RequestParam String importer,
            @RequestParam String exporter,
            @RequestParam String hsCode,
            @RequestParam int units,
            @RequestParam(defaultValue = "2") int maxTransits
    ) {
        RouteOptimizationRequest request = new RouteOptimizationRequest();

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
        request.setMaxTransits(maxTransits);

        return service.calculateOptimalRoutes(request);
    }
}
