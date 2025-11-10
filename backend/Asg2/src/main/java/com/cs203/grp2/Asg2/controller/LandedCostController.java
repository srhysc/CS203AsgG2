package com.cs203.grp2.Asg2.controller;

import org.springframework.web.bind.annotation.*;

import com.cs203.grp2.Asg2.DTO.LandedCostRequest;
import com.cs203.grp2.Asg2.DTO.LandedCostResponse;
import com.cs203.grp2.Asg2.service.LandedCostService;
import com.cs203.grp2.Asg2.models.*;
import com.cs203.grp2.Asg2.exceptions.*;
import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.controller.*;
import com.cs203.grp2.Asg2.config.*;
import com.cs203.grp2.Asg2.service.*;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

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
        // return service.calculateLandedCost(request);
        if (request == null || request.getUnits() <= 0 || request.getHsCode() == null) {
            throw new GeneralBadRequestException("Invalid landed cost request.");
        }
        LandedCostResponse response = service.calculateLandedCost(request);
        if (response == null) {
            throw new LandedCostNotFoundException("Landed cost calculation failed or not found.");
        }
        return response;
    }

    // GET endpoint for quick browser testing
    @GetMapping
    public LandedCostResponse calculateLandedCostViaGet(
            @RequestParam String importer,
            @RequestParam String exporter,
            @RequestParam String hsCode,
            @RequestParam int units,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

            // Exception for invalid parameters
        if (importer == null || exporter == null || hsCode == null || units <= 0 || date == null) {
            throw new GeneralBadRequestException("Missing or invalid parameters for landed cost calculation.");
        }

        LandedCostRequest request = new LandedCostRequest();

        // Detect if importer is numeric -> ISO3n
        if (importer.matches("\\d+")) {
            request.setImporterCode(importer);
        } else {
            request.setImporterName(importer);
        }

        // Detect if exporter is numeric -> ISO3n
        if (exporter.matches("\\d+")) {
            request.setExporterCode(exporter);
        } else {
            request.setExporterName(exporter);
        }

        request.setHsCode(hsCode);
        request.setUnits(units);
        request.setCalculationDate(date);

        LandedCostResponse response = service.calculateLandedCost(request);
        if (response == null) {
            throw new LandedCostNotFoundException("Landed cost calculation failed or not found.");
        }
        return response;
        // return service.calculateLandedCost(request);
    }
}
