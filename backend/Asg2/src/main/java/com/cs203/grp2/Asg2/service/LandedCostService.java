package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.models.*;
import com.cs203.grp2.Asg2.exceptions.*;
import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.controller.*;
import com.cs203.grp2.Asg2.config.*;
import com.cs203.grp2.Asg2.service.*;

import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class LandedCostService {
    private static final Logger logger = LoggerFactory.getLogger(LandedCostService.class);

    private final CountryService countryService;
    private final PetroleumService petroleumService;
    private final WitsService tariffService;
    private final RouteOptimizeService routeOptimizationService; 


    public LandedCostService(CountryService countryService,
            PetroleumService petroleumService,
            WitsService tariffService,
            RouteOptimizeService routeOptimizationService) {
        this.countryService = countryService;
        this.petroleumService = petroleumService;
        this.tariffService = tariffService;
        this.routeOptimizationService = routeOptimizationService;
    }

    public LandedCostResponse calculateLandedCost(LandedCostRequest request) {
        // Get exporter/importer
        Country importer = resolveCountry(request.getImporterCode(), request.getImporterName(), "Importer");
        Country exporter = resolveCountry(request.getExporterCode(), request.getExporterName(), "Exporter");

        if (importer.getCode().equals(exporter.getCode())) {
             throw new GeneralBadRequestException("Importer and exporter cannot be the same country");
        }

        // get Petroleum
        Petroleum petroleum = petroleumService.getPetroleumByHsCode(request.getHsCode());
        if (petroleum == null) {
            throw new PetroleumNotFoundException("Invalid HS code for petroleum: " + request.getHsCode());
        }

        // Build RouteOptimizationRequest
        RouteOptimizationRequest optRequest = new RouteOptimizationRequest();
        optRequest.setExporter(exporter);
        optRequest.setImporter(importer);
        optRequest.setHsCode(request.getHsCode());
        optRequest.setUnits(request.getUnits());
        optRequest.setCalculationDate(request.getCalculationDate());

        // Calculate top routes
        RouteOptimizationResponse routeResponse = routeOptimizationService.optimizeRoutes(optRequest);

        if (routeResponse == null || routeResponse.getTopRoutes() == null || routeResponse.getTopRoutes().isEmpty()) {
            throw new LandedCostNotFoundException("No route found for landed cost calculation.");
        }

        List<RouteBreakdown> candidateRoutes = routeResponse.getTopRoutes();

        // Take the cheapest route (first in list)
        RouteBreakdown directRoute = candidateRoutes.get(0);

        //Populate alternative route map
        Map<String, RouteBreakdown> alternativeRoutes = candidateRoutes.stream()
        .skip(1)
        .collect(Collectors.toMap(
            RouteBreakdown::getTransitCountry, // key - transit country
            r -> r                            // value -  full RouteBreakdown object
        ));

        // tariff rate (in percentage) = (tariff fees/base cost) * 100, multiply and divide by another 100 for math.round
        double tariffRatePercent = Math.round((directRoute.getTariffFees() / directRoute.getBaseCost()) * 100 * 100.0) / 100.0;
        //round landedcost and base cost
        double totalLandedCostRounded = Math.round(directRoute.getTotalLandedCost() * 100.0) / 100.0;
        double baseCostRounded = Math.round(directRoute.getBaseCost() * 100.0) / 100.0;
        double vatFeeRounded = Math.round(directRoute.getVatFees() * 100.0) / 100.0;


        // Build LandedCostResponse from the direct route, pass in alternative routes map
        return new LandedCostResponse(
                importer.getName(),
                exporter.getName(),
                petroleum.getName(),
                petroleum.getHsCode(),
                routeResponse.getPetroleumPrice(),
                baseCostRounded,
                tariffRatePercent, 
                directRoute.getTariffFees(),
                directRoute.getVatRate(),
                vatFeeRounded,
                totalLandedCostRounded,
                "USD",
                alternativeRoutes
        );
    }


    // Country helper function - get the Country from firebase using code/name
    private Country resolveCountry(String iso3n, String name, String role) {
        if (iso3n != null)
            return countryService.getCountryByCode(String.valueOf(iso3n));
        if (name != null)
            return countryService.getCountryByName(name);
        throw new GeneralBadRequestException(role + " not specified");
    }

}