package com.cs203.grp2.Asg2.landedcost;

import com.cs203.grp2.Asg2.country.Country;
import com.cs203.grp2.Asg2.country.CountryService;
import com.cs203.grp2.Asg2.petroleum.Petroleum;
import com.cs203.grp2.Asg2.petroleum.PetroleumService;
import com.cs203.grp2.Asg2.wits.WitsTariffService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LandedCostService {
    private static final Logger logger = LoggerFactory.getLogger(LandedCostService.class);

    private final CountryService countryService;
    private final PetroleumService petroleumService;
    private final WitsTariffService tariffService;

    public LandedCostService(CountryService countryService,
                             PetroleumService petroleumService,
                             WitsTariffService tariffService) {
        this.countryService = countryService;
        this.petroleumService = petroleumService;
        this.tariffService = tariffService;
    }

    public LandedCostResponse calculateLandedCost(LandedCostRequest request) {
        // Importer
        Country importer = null;
        if (request.getImporterIso3n() != null) {
            importer = countryService.getCountryByISO3n(request.getImporterIso3n());
        } else if (request.getImporterName() != null) {
            importer = countryService.getCountryByName(request.getImporterName());
        }

        // Exporter
        Country exporter = null;
        if (request.getExporterIso3n() != null) {
            exporter = countryService.getCountryByISO3n(request.getExporterIso3n());
        } else if (request.getExporterName() != null) {
            exporter = countryService.getCountryByName(request.getExporterName());
        }

        // Petroleum
        Petroleum petroleum = petroleumService.getPetroleumByHsCode(request.getHsCode());

        if (importer == null || exporter == null || petroleum == null) {
            throw new IllegalArgumentException("Invalid importer/exporter HS code or country code/name");
        }

        if (importer.getIso3n() == exporter.getIso3n()) {
            throw new IllegalArgumentException("Importer and exporter cannot be the same country");
        }

        // Calculations
        double pricePerUnit = petroleum.getPricePerUnit();
        double baseCost = pricePerUnit * request.getUnits();

        double tariffRate;
        try {
            tariffRate = tariffService.getLatest(
                    importer.getIso3n(),
                    exporter.getIso3n(),
                    request.getHsCode(),
                    "aveestimated"
            ).getSimpleAverage() / 100.0;
        } catch (Exception e) {
            logger.warn("No tariff available. Using fallback of 0%.");
            tariffRate = 0.0;
        }

        double tariffFees = baseCost * tariffRate;
        double vatRate = importer.getVatRate();
        double vatFees = (baseCost + tariffFees) * vatRate;

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
                "USD"
        );
    }
}
