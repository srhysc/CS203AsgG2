package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.*;
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
        //use resolveCountry method to get country from url string
        Country importerCountry = resolveCountry(importer);
        Country exporterCountry = resolveCountry(exporter);

        RouteOptimizationRequest request = new RouteOptimizationRequest();

        request.setExporter(exporterCountry);
        request.setImporter(importerCountry);
        request.setHsCode(hsCode);
        request.setUnits(units);
        request.setCalculationDate(date);


        return service.optimizeRoutes(request);
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
