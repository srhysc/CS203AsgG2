package com.cs203.grp2.Asg2.landedcost;

import com.cs203.grp2.Asg2.country.Country;
import com.cs203.grp2.Asg2.country.CountryService;
import com.cs203.grp2.Asg2.petroleum.Petroleum;
import com.cs203.grp2.Asg2.petroleum.PetroleumService;
import com.cs203.grp2.Asg2.wits.TariffLatest;
import com.cs203.grp2.Asg2.wits.WitsTariffService;
import org.springframework.stereotype.Service;

@Service
public class LandedCostService {

    private final PetroleumService petroleumService;
    private final WitsTariffService witsTariffService;
    private final CountryService countryService;

    public LandedCostService(PetroleumService petroleumService,
                             WitsTariffService witsTariffService,
                             CountryService countryService) {
        this.petroleumService = petroleumService;
        this.witsTariffService = witsTariffService;
        this.countryService = countryService;
    }

    /**
     * Calculate landed cost for a product imported into a destination country.
     *
     * @param hsCode HS6 product code
     * @param exporterIso6Code ISO6 numeric code of exporting country
     * @param importerIso6Code ISO6 numeric code of importing country
     * @param units Number of units being imported
     * @return LandedCost object with detailed cost info
     */
    public LandedCost calculateLandedCost(String hsCode, int exporterIso6Code, int importerIso6Code, int units) {

        // 1. Get product base price per unit
        Petroleum product = petroleumService.getPetroleumByHsCode(hsCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product HS code: " + hsCode));
        double productUnitCost = product.getPricePerUnit();

        // 2. Get tariff rate from WITS
        TariffLatest tariff = witsTariffService.getLatest(exporterIso6Code, importerIso6Code, hsCode, "aveestimated");
        double tariffRate = tariff.getSimpleAverage();

        // 3. Get VAT rate for importer country
        Country importerCountry = countryService.getCountryByISO6code(importerIso6Code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown importer country code: " + importerIso6Code));
        double vatRate = importerCountry.getVatRate();

        // 4. Calculate costs
        double productCostTotal = productUnitCost * units;
        double tariffAmount = productCostTotal * tariffRate / 100.0;
        double vatAmount = (productCostTotal + tariffAmount) * vatRate / 100.0;
        double totalLandedCost = productCostTotal + tariffAmount + vatAmount;

        // 5. Build and return result
        return new LandedCost(hsCode, importerIso6Code, exporterIso6Code, units,
                productUnitCost, tariffRate, vatRate, tariffAmount, vatAmount, totalLandedCost);
    }
}
