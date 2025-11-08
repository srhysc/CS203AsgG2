package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.exceptions.CountryNotFoundException;
import com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException;
import com.cs203.grp2.Asg2.exceptions.RouteOptimizationNotFoundException;
import com.cs203.grp2.Asg2.models.Country;  
import com.cs203.grp2.Asg2.service.RouteOptimizeService;
import com.cs203.grp2.Asg2.service.CountryService; 

import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@RestController
@RequestMapping("/route-optimization")
public class RouteOptimizationController {

    private final RouteOptimizeService service;
    private final CountryService countryService;  //to resolve countries with method


    public RouteOptimizationController(RouteOptimizeService service, CountryService countryService) {
        this.service = service;
        this.countryService = countryService;  
    }

    // POST endpoint (for frontend or JSON clients)
    @PostMapping
    public RouteOptimizationResponse calculateRoutes(@RequestBody RouteOptimizationRequest request) {
        return service.optimizeRoutes(request);
    }

    // GET endpoint (for Swagger/browser testing)
    @GetMapping
    public RouteOptimizationResponse calculateRoutesViaGet(
            @RequestParam String importer,
            @RequestParam String exporter,
            @RequestParam String hsCode,
            @RequestParam int units,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {

        if (importer == null || exporter == null || hsCode == null || units <= 0 || date == null) {
            throw new GeneralBadRequestException("Missing or invalid parameters for route optimization.");
        }
        //use resolveCountry method to get country from url string
        Country importerCountry = resolveCountry(importer);
        Country exporterCountry = resolveCountry(exporter);

        if (importerCountry == null) {
            throw new CountryNotFoundException("Importer country not found: " + importer);
        }
        if (exporterCountry == null) {
            throw new CountryNotFoundException("Exporter country not found: " + exporter);
        }

        RouteOptimizationRequest request = new RouteOptimizationRequest();

        request.setExporter(exporterCountry);
        request.setImporter(importerCountry);
        request.setHsCode(hsCode);
        request.setUnits(units);
        request.setCalculationDate(date);


        RouteOptimizationResponse response = service.optimizeRoutes(request);
        if (response == null) {
            throw new RouteOptimizationNotFoundException("Route optimization failed or not found.");
        }
        return response;
    }

    //get country by code or name
    private Country resolveCountry(String identifier) {
    //if all digits
    if (identifier.matches("\\d+")) {
        return countryService.getCountryByCode(identifier);
    } else {
        return countryService.getCountryByName(identifier);
    }
}
}
