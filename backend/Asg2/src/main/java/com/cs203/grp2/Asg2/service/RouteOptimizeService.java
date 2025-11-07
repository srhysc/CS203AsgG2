package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.models.Country;
import com.cs203.grp2.Asg2.models.Petroleum;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RouteOptimizeService {

    private static final Logger logger = LoggerFactory.getLogger(RouteOptimizeService.class);


    private final CountryService countryService;
    private final PetroleumService petroleumService;
    private final WitsService tariffService;

    public RouteOptimizeService(CountryService countryService,
                                    PetroleumService petroleumService,
                                    WitsService tariffService) {
        this.countryService = countryService;
        this.petroleumService = petroleumService;
        this.tariffService = tariffService;
    }


    //FULL FUNCTION
    public RouteOptimizationResponse optimizeRoutes(RouteOptimizationRequest request) {

logger.info("=== Starting route optimization ===");
logger.info("Request - HS Code: {}, Units: {}", request.getHsCode(), request.getUnits());

        //establish countries
        Country exporter = request.getExportingCountry();
        Country importer = request.getImportingCountry();

        //ERROR HANDLING
        if (exporter.getCode().equals(importer.getCode())) {
            throw new IllegalArgumentException("Importer and exporter cannot be the same country");
        }

        Petroleum petroleum = petroleumService.getPetroleumByHsCode(request.getHsCode());
        if (petroleum == null) {
            throw new IllegalArgumentException("Invalid HS Code for petroleum");
        }

        //set a new list to return top x results
        List<RouteBreakdown> candidateRoutes = new ArrayList<>();

        //get latest petroleum price
        double latestprice = petroleum.getPricePerUnit(request.getCalculationDate());

        //Calculate direct route
        RouteBreakdown direct = computeDirectRoute(exporter, importer, petroleum, request.getUnits(), latestprice, request.getCalculationDate());
logger.info("Direct route calculated - Total cost: ${}", direct.getTotalLandedCost());
        candidateRoutes.add(direct);

        //Calculate transit routes
         computeFixedEndpointsRoutes(exporter, importer, petroleum, request.getUnits(),candidateRoutes, latestprice, request.getCalculationDate());
logger.info("Total candidate routes: {}", candidateRoutes.size());

        return new RouteOptimizationResponse(candidateRoutes,latestprice);
    }


//////////////////////////////////////////////////////////////////////////////


    // LANDED COST MAIN CALCULATING FUNCTIONS

    //DIRECT CALCULATION
    private RouteBreakdown computeDirectRoute(Country exporter, Country importer,
                                              Petroleum petroleum, int units, double petroleumprice, LocalDate date) {
        double baseCost =  petroleumprice * units;
        double tariffRate = 0.0;

        //get tariffs
        try {
            TariffRequestDTO dto = new TariffRequestDTO(exporter.getCode(), importer.getCode(),
                    petroleum.getHsCode(), date);
            tariffRate = tariffService.resolveTariff(dto).ratePercent() / 100.0;
        } catch (Exception e) {
            tariffRate = 0.0;
        }

        //calculate tariff fees if any
        double tariffFees = baseCost * tariffRate;

        //retrieve VAT rate of importing country that you have to pay
        double vatRate = (importer.getVatRates() != null) ? importer.getVatRates(date) : 0.0;
        //calculate VAT fees based on rate
        double vatFees = (baseCost + tariffFees) * (vatRate/100);

        //calculate total - basecost + tariff fees + vat fees
        double total = baseCost + tariffFees + vatFees;

        return new RouteBreakdown(exporter.getName(), null, importer.getName(),
                baseCost, tariffFees, vatFees, total, vatRate * 100, petroleum.getName());
    }


    //FUNCTION LOOPING THROUGH COUNTRIES AND CALCULATING DIFFERENT PERMUTATIONS
    private void computeFixedEndpointsRoutes(
        Country exporter, Country importer, Petroleum petroleum, int units,List<RouteBreakdown> candidateRoutes, double petroleumprice, LocalDate date) {

        List<RouteBreakdown> middleRouteList = new ArrayList<>();
        //Get all countries in firebase
        List<Country> allCountries = countryService.getAll();

        // loop through all middle countries
        for (Country middle : allCountries) {
            if (middle.getCode().equals(exporter.getCode()) || middle.getCode().equals(importer.getCode()))
                continue;

            //calculate price with middle country involved
            middleRouteList.add(computeRouteWithMiddle(exporter, middle, importer, petroleum, units, petroleumprice, date));
        }

        // Sort by total cost and pick best 5, and add to starting list
        candidateRoutes.addAll(
        middleRouteList.stream()
                .sorted(Comparator.comparingDouble(RouteBreakdown::getTotalLandedCost))
                .limit(5)
                .collect(Collectors.toList()));
    }


//////////////////////////////////////////////////////////////////////////////


    //FUNCTION JUST FOR CALCUALTION WITH MIDDLE COUNTRY
    private RouteBreakdown computeRouteWithMiddle(Country exporter, Country middle,
                                                  Country importer, Petroleum petroleum, int units, double petroleumprice, LocalDate date) {
        double baseCost = petroleumprice * units;
        double totalTariff = 0.0;

        try {
            // Tariff between exporter and middle
            TariffRequestDTO dto1 = new TariffRequestDTO(exporter.getCode(), middle.getCode(),
                    petroleum.getHsCode(), date);
            double rate1 = tariffService.resolveTariff(dto1).ratePercent() / 100.0;

            // Tariff between middle and importer
            TariffRequestDTO dto2 = new TariffRequestDTO(middle.getCode(), importer.getCode(),
                    petroleum.getHsCode(), date);
            double rate2 = tariffService.resolveTariff(dto2).ratePercent() / 100.0;

            totalTariff = baseCost * rate1 + baseCost * rate2; //cost times rate for both countries to get teriff extra cost
 
        } catch (Exception e) {
            totalTariff = 0.0;
        }

        //get importing countries' vat rate you will have to pay for
        double vatRate = (importer.getVatRates() != null) ? importer.getVatRates(date) : 0.0;
        //calculate vat fees using vat rate on base cost
        double vatFees = (baseCost + totalTariff) * (vatRate/100);
        //add base cost + tariff cost + vat cost
        double total = baseCost + totalTariff + vatFees;

        return new RouteBreakdown(exporter.getName(),
                middle.getName(),
                importer.getName(),
                baseCost,
                totalTariff,
                vatFees,
                total,
                vatRate * 100,
                petroleum.getName());
    }

}
