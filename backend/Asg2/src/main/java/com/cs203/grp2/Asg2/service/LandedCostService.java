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
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class LandedCostService {
    private static final Logger logger = LoggerFactory.getLogger(LandedCostService.class);

    private final CountryService countryService;
    private final PetroleumService petroleumService;
    private final WitsService tariffService;

    public LandedCostService(CountryService countryService,
            PetroleumService petroleumService,
            WitsService tariffService) {
        this.countryService = countryService;
        this.petroleumService = petroleumService;
        this.tariffService = tariffService;
    }

    public LandedCostResponse calculateLandedCost(LandedCostRequest request) {
        // Importer
        Country importer = null;
        if (request.getImporterCode() != null) {
            importer = countryService.getCountryByCode(request.getImporterCode());
        } else if (request.getImporterName() != null) {
            importer = countryService.getCountryByName(request.getImporterName());
        }
        System.out.println("✅Importer recieved : " + importer.getCode().toString());

        // Exporter
        Country exporter = null;
        if (request.getExporterCode() != null) {
            exporter = countryService.getCountryByCode(request.getExporterCode());
        } else if (request.getExporterName() != null) {
            exporter = countryService.getCountryByName(request.getExporterName());
        }
        System.out.println("✅Exporter recieved : " + exporter.getCode().toString());

        // Petroleum
        Petroleum petroleum = petroleumService.getPetroleumByHsCode(request.getHsCode());

        // if (importer == null || exporter == null || petroleum == null) {
        // throw new IllegalArgumentException("Invalid importer/exporter HS code or
        // country code/name");
        // }

        RouteOptimizationResponse bestRoutes = optimizeRoutes(importer,exporter);

        // debug
        List<String> missing = new ArrayList<>();

        if (importer == null)
            missing.add("importer");
        if (exporter == null)
            missing.add("exporter");
        if (petroleum == null)
            missing.add("petroleum");

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("❌ Missing or invalid: " + String.join(", ", missing));
        }
        
        if (importer.getCode() == null || exporter.getCode() == null) {
            throw new IllegalArgumentException("❌ Invalid importer/exporter; country is null.");
        }
        if (importer.getCode().equals(exporter.getCode())) {
            throw new IllegalArgumentException("Importer and exporter cannot be the same country");
        }

        // Calculations
        double pricePerUnit = petroleum.getPricePerUnit();
        double baseCost = pricePerUnit * request.getUnits();

        double tariffRate; // decimal

        try {
            LocalDate today = LocalDate.now();
            double tariffRateReq = tariffService.
            resolveTariff(new TariffRequestDTO(
                importer.getCode(), 
                exporter.getCode(), 
                request.getHsCode(), 
                /*date for now */today)).ratePercent();
           
            // TariffEntry te = tariffService.getLatest(
            //         importer.getCode(), // reporter (importer)
            //         exporter.getCode(), // partner (exporter)
            //         request.getHsCode(),
            //         "aveestimated");
            // our TariffEntry stores rate in basis points (int)
            tariffRate = tariffRateReq / 100.0;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            logger.warn("No tariff available. Using fallback of 0%.", e);
            tariffRate = 0.0;
        }
        // try {
        //     tariffRate = tariffService.getLatest(
        //             Integer.parseInt(importer.getIso3n()),
        //             Integer.parseInt(exporter.getIso3n()),
        //             request.getHsCode(),
        //             "aveestimated").getSimpleAverage() / 100.0;
        // } catch (Exception e) {
        //     logger.warn("No tariff available. Using fallback of 0%.");
        //     tariffRate = 0.0;
        // }

        double tariffFees = baseCost * tariffRate;
        // Check if importervatRate exists, 0.0 otherwise
        Long importerVatRate = importer.getVatRates();

        double vatRate = (importerVatRate != null) ? (double) importerVatRate.doubleValue() : 0.0;
        double vatFees = (baseCost + tariffFees) * (vatRate / 100);
        double totalCost = baseCost + tariffFees + vatFees;

        // Build response
        return new LandedCostResponse(
                importer.getName(),
                exporter.getName(),
                petroleum.getName(),
                petroleum.getHsCode(),
                pricePerUnit,
                baseCost,
                tariffRate,
                tariffFees,
                vatRate,
                vatFees,
                totalCost,
                "USD");
    }


}