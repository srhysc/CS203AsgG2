package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.models.Country;
import com.cs203.grp2.Asg2.models.Petroleum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteOptimizationService {

    private final CountryService countryService;
    private final PetroleumService petroleumService;
    private final WitsService tariffService;

    public RouteOptimizationService(CountryService countryService,
                                    PetroleumService petroleumService,
                                    WitsService tariffService) {
        this.countryService = countryService;
        this.petroleumService = petroleumService;
        this.tariffService = tariffService;
    }

    public RouteOptimizationResponse calculateOptimalRoutes(RouteOptimizationRequest request) {

        // Resolve exporter
        Country exporter;
        if (request.getExporterIso3n() != null) {
            exporter = countryService.getCountryByCode(""+request.getExporterIso3n());
        } else if (request.getExporterName() != null) {
            exporter = countryService.getCountryByName(request.getExporterName());
        } else {
            throw new IllegalArgumentException("Exporter not specified");
        }

        // Resolve importer
        Country importer;
        if (request.getImporterIso3n() != null) {
            importer = countryService.getCountryByCode(""+request.getImporterIso3n());
        } else if (request.getImporterName() != null) {
            importer = countryService.getCountryByName(request.getImporterName());
        } else {
            throw new IllegalArgumentException("Importer not specified");
        }

        // Petroleum
        Petroleum petroleum = petroleumService.getPetroleumByHsCode(request.getHsCode());
        if (petroleum == null) {
            throw new IllegalArgumentException("Invalid HS Code for petroleum");
        }

        if (exporter.getCode().equals(importer.getCode())) {
            throw new IllegalArgumentException("Importer and exporter cannot be the same country");
        }

        List<Country> allCountries = countryService.getAll();
        List<RouteBreakdown> allRoutes = new ArrayList<>();

        // Direct route (0 transits)
        allRoutes.add(computeRoute(exporter, new ArrayList<>(), importer, petroleum, request.getUnits()));

        // Routes with 1 or 2 transits
        for (Country transit1 : allCountries) {
            if (transit1.getCode().equals(exporter.getCode()) || transit1.getCode().equals(importer.getCode()))
                continue;

            if (request.getMaxTransits() >= 1) {
                allRoutes.add(computeRoute(exporter, List.of(transit1), importer, petroleum, request.getUnits()));
            }

            if (request.getMaxTransits() == 2) {
                for (Country transit2 : allCountries) {
                    if (transit2.getCode().equals(exporter.getCode()) ||
                        transit2.getCode().equals(importer.getCode()) ||
                        transit2.getCode().equals(transit1.getCode())) continue;

                    allRoutes.add(computeRoute(exporter, List.of(transit1, transit2), importer, petroleum, request.getUnits()));
                }
            }
        }

        // Sort by total landed cost and take top 10
        List<RouteBreakdown> top10 = allRoutes.stream()
                .sorted(Comparator.comparingDouble(RouteBreakdown::getTotalLandedCost))
                .limit(10)
                .collect(Collectors.toList());

        return new RouteOptimizationResponse(top10);
    }

    private RouteBreakdown computeRoute(Country exporter, List<Country> transits, Country importer,
                                        Petroleum petroleum, int units) {

        double baseCost = petroleum.getPricePerUnit() * units;
        double tariffFees = 0.0;

        // Full route: exporter -> transits -> importer
        List<Country> routeCountries = new ArrayList<>();
        routeCountries.add(exporter);
        routeCountries.addAll(transits);
        routeCountries.add(importer);

        for (int i = 0; i < routeCountries.size() - 1; i++) {
            Country from = routeCountries.get(i);
            Country to = routeCountries.get(i + 1);

            double rate;
            try {
                TariffRequestDTO dto = new TariffRequestDTO(from.getCode(), to.getCode(), petroleum.getHsCode(), null);
                rate = tariffService.resolveTariff(dto).ratePercent();

                // rate = tariffService.getLatest(from.getCode(), to.getCode(),
                //         petroleum.getHsCode(), "aveestimated").getSimpleAverage() / 100.0;
            } catch (Exception e) {
                rate = 0.0; // fallback
            }
            tariffFees += baseCost * rate;
        }

        // VAT only for importing country
        double vatRate = (importer.getVatRates() != null) ? importer.getVatRates() / 100.0 : 0.0;
        double vatFees = (baseCost + tariffFees) * vatRate;
        double totalLandedCost = baseCost + tariffFees + vatFees;

        List<String> transitNames = transits.stream().map(Country::getName).collect(Collectors.toList());

        return new RouteBreakdown(
                exporter.getName(),
                transitNames,
                importer.getName(),
                baseCost,
                tariffFees,
                vatFees,
                totalLandedCost,
                vatRate * 100 // display as percentage
        );
    }
}
