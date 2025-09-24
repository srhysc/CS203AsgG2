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
        Country importer = countryService.getCountryByISO3n(request.getImporterIso3n());
        Country exporter = countryService.getCountryByISO3n(request.getExporterIso3n());
        Petroleum petroleum = petroleumService.getPetroleumByHsCode(request.getHsCode());

        if (importer == null || exporter == null || petroleum == null) {
            throw new IllegalArgumentException("Invalid importer/exporter HS code or country code");
        }

        // Base cost
        double baseCost = petroleum.getPricePerUnit() * request.getUnits();

        // Get tariff from WITS (mocked if unavailable)
        double tariffRate;
        try {
            tariffRate = tariffService.getLatest(
                    request.getImporterIso3n(),   // reporter numeric ISO
                    request.getExporterIso3n(),   // partner numeric ISO
                    request.getHsCode(),
                    "aveestimated"
            ).getSimpleAverage() / 100.0; // convert percent to decimal
        } catch (Exception e) {
            logger.warn("WITS tariff unavailable, using fallback 10%");
            tariffRate = 0.1; // fallback
        }

        double tariff = baseCost * tariffRate;
        double vat = (baseCost + tariff) * importer.getVatRate();

        double totalCost = baseCost + tariff + vat;

        return new LandedCostResponse(totalCost, "USD");
    }
}
