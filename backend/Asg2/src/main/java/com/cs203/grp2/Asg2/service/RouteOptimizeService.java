package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException;
import com.cs203.grp2.Asg2.exceptions.PetroleumNotFoundException;
import com.cs203.grp2.Asg2.exceptions.RouteOptimizationNotFoundException;
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

    private static final Set<String> OIL_TRADING_HUBS = Set.of(
        "SGP", // Singapore - largest oil trading hub in Asia
        "NLD", // Netherlands (Rotterdam) - Europe's largest port
        "ARE", // UAE (Dubai, Fujairah) - Middle East hub
        "USA", // United States (Houston, Louisiana) - major hub
        "GBR", // United Kingdom (London) - financial/trading hub
        "CHE", // Switzerland (Geneva) - trading hub
        "HKG", // Hong Kong - Asia trading hub
        "CHN", // China (Shanghai) - major importer/hub
        "JPN", // Japan - major importer/transit
        "KOR", // South Korea - major refining hub
        "EGY", // Egypt (Suez Canal) - critical chokepoint
        "TUR", // Turkey (Bosphorus) - transit point
        "PAN", // Panama (Canal) - transit point
        "BHR", // Bahrain - Middle East trading hub
        "MYS"  // Malaysia (Malacca Strait region)
    );

    //FULL FUNCTION
    public RouteOptimizationResponse optimizeRoutes(RouteOptimizationRequest request) {

        // logger.info("=== Starting route optimization ===");
        // logger.info("Request - HS Code: {}, Units: {}", request.getHsCode(),
        // request.getUnits());

        // //establish countries
        // Country exporter = request.getExportingCountry();
        // Country importer = request.getImportingCountry();

        // //ERROR HANDLING
        // if (exporter.getCode().equals(importer.getCode())) {
        // throw new IllegalArgumentException("Importer and exporter cannot be the same
        // country");
        // }

        // Petroleum petroleum =
        // petroleumService.getPetroleumByHsCode(request.getHsCode());
        // if (petroleum == null) {
        // throw new IllegalArgumentException("Invalid HS Code for petroleum");
        // }

        // //set a new list to return top x results
        // List<RouteBreakdown> candidateRoutes = new ArrayList<>();

        // //get latest petroleum price
        // double latestprice = petroleum.getPricePerUnit(request.getCalculationDate());

        // //Calculate direct route
        // RouteBreakdown direct = computeDirectRoute(exporter, importer, petroleum,
        // request.getUnits(), latestprice, request.getCalculationDate());
        // logger.info("Direct route calculated - Total cost: ${}",
        // direct.getTotalLandedCost());
        // candidateRoutes.add(direct);

        // //Calculate transit routes
        // computeFixedEndpointsRoutes(exporter, importer, petroleum,
        // request.getUnits(),candidateRoutes, latestprice,
        // request.getCalculationDate());
        // logger.info("Total candidate routes: {}", candidateRoutes.size());

        // return new RouteOptimizationResponse(candidateRoutes,latestprice);
        logger.info("=== Starting route optimization ===");
        logger.info("Request - HS Code: {}, Units: {}", request.getHsCode(), request.getUnits());

        // establish countries
        Country exporter = request.getExportingCountry();
        Country importer = request.getImportingCountry();

        // ERROR HANDLING
        if (exporter.getCode().equals(importer.getCode())) {
            throw new GeneralBadRequestException("Importer and exporter cannot be the same country");
        }

        Petroleum petroleum = petroleumService.getPetroleumByHsCode(request.getHsCode());
        if (petroleum == null) {
            throw new PetroleumNotFoundException("Invalid HS Code for petroleum: " + request.getHsCode());
        }

        // set a new list to return top x results
        List<RouteBreakdown> candidateRoutes = new ArrayList<>();

        // get latest petroleum price
        double latestprice = petroleum.getPricePerUnit(request.getCalculationDate());

        // Calculate direct route
        RouteBreakdown direct = computeDirectRoute(exporter, importer, petroleum, request.getUnits(), latestprice,
                request.getCalculationDate());
                logger.info("Direct route calculated - Total cost: ${}", direct.getTotalLandedCost());
        candidateRoutes.add(direct);

        // Calculate transit routes using ONLY hub countries
        computeHubBasedRoutes(exporter, importer, petroleum, request.getUnits(),
            candidateRoutes, latestprice, request.getCalculationDate());
        
        logger.info("Total candidate routes: {}", candidateRoutes.size());
                for (RouteBreakdown routeBreakdown : candidateRoutes) {
            System.out.println("😈 " + routeBreakdown.getVatFees() + " " + routeBreakdown.getExportingCountry() + " to " + routeBreakdown.getImportingCountry() + " btwn " + routeBreakdown.getTransitCountry());
        }

        // //Calculate transit routes
        // computeFixedEndpointsRoutes(exporter, importer, petroleum, request.getUnits(),candidateRoutes, latestprice, request.getCalculationDate());
        // logger.info("Total candidate routes: {}", candidateRoutes.size());
        // for (RouteBreakdown routeBreakdown : candidateRoutes) {
        //     System.out.println("😈 " + routeBreakdown.getTariffFees());
        // }
        return new RouteOptimizationResponse(candidateRoutes,latestprice);
        // Calculate transit routes
        computeFixedEndpointsRoutes(exporter, importer, petroleum, request.getUnits(), candidateRoutes, latestprice,
                request.getCalculationDate());
        logger.info("Total candidate routes: {}", candidateRoutes.size());

        if (candidateRoutes.isEmpty()) {
            throw new RouteOptimizationNotFoundException("No route found for route optimization.");
        }

        return new RouteOptimizationResponse(candidateRoutes, latestprice);

    }


    //////////////////////////////////////////////////////////////////////////////

    // LANDED COST MAIN CALCULATING FUNCTIONS

    // DIRECT CALCULATION
    private RouteBreakdown computeDirectRoute(Country exporter, Country importer,
            Petroleum petroleum, int units, double petroleumprice, LocalDate date) {
        double baseCost = petroleumprice * units;
        double tariffRate = 0.0;

        // get tariffs
        try {
            TariffRequestDTO dto = new TariffRequestDTO(exporter.getISO3(), importer.getISO3(),
                    petroleum.getHsCode(), date);
            tariffRate = tariffService.resolveTariff(dto).ratePercent() ;
        } catch (Exception e) {
            tariffRate = 0.0;
        }

        // calculate tariff fees if any
        double tariffFees = baseCost * tariffRate;

        // retrieve VAT rate of importing country that you have to pay
        double vatRate = (importer.getVatRates() != null) ? importer.getVatRates(date) : 0.0;
        // calculate VAT fees based on rate
        double vatFees = (baseCost + tariffFees) * (vatRate / 100);

        // calculate total - basecost + tariff fees + vat fees
        double total = baseCost + tariffFees + vatFees;

        return new RouteBreakdown(exporter.getName(), null, importer.getName(),
                baseCost, tariffFees, vatFees, total, vatRate * 100, petroleum.getName());
    }

    // FUNCTION LOOPING THROUGH COUNTRIES AND CALCULATING DIFFERENT PERMUTATIONS
    private void computeFixedEndpointsRoutes(
            Country exporter, Country importer, Petroleum petroleum, int units, List<RouteBreakdown> candidateRoutes,
            double petroleumprice, LocalDate date) {

        List<RouteBreakdown> middleRouteList = new ArrayList<>();
        // Get all countries in firebase
        List<Country> allCountries = countryService.getAll();

        // loop through all middle countries
        for (Country middle : allCountries) {
            if (middle.getCode().equals(exporter.getCode()) || middle.getCode().equals(importer.getCode()))
                continue;

            // calculate price with middle country involved
            middleRouteList
                    .add(computeRouteWithMiddle(exporter, middle, importer, petroleum, units, petroleumprice, date));
        }

        // Sort by total cost and pick best 5, and add to starting list
        candidateRoutes.addAll(
                middleRouteList.stream()
                        .sorted(Comparator.comparingDouble(RouteBreakdown::getTotalLandedCost))
                        .limit(5)
                        .collect(Collectors.toList()));
    }

    //////////////////////////////////////////////////////////////////////////////

    // FUNCTION JUST FOR CALCUALTION WITH MIDDLE COUNTRY
    private RouteBreakdown computeRouteWithMiddle(Country exporter, Country middle,
            Country importer, Petroleum petroleum, int units, double petroleumprice, LocalDate date) {
        double baseCost = petroleumprice * units;
        double totalTariff = 0.0;

        try {
            // Tariff between exporter and middle
            TariffRequestDTO dto1 = new TariffRequestDTO(exporter.getISO3(), middle.getISO3(),
                    petroleum.getHsCode(), date);
            double rate1 = tariffService.resolveTariff(dto1).ratePercent() ;

            // Tariff between middle and importer
            TariffRequestDTO dto2 = new TariffRequestDTO(middle.getISO3(), importer.getISO3(),
                    petroleum.getHsCode(), date);
            double rate2 = tariffService.resolveTariff(dto2).ratePercent() ;

            totalTariff = baseCost * rate1 + baseCost * rate2; // cost times rate for both countries to get teriff extra
                                                               // cost

        } catch (Exception e) {
            totalTariff = 0.0;
        }

        // get importing countries' vat rate you will have to pay for
        double vatRate = (importer.getVatRates() != null) ? importer.getVatRates(date) : 0.0;
        // calculate vat fees using vat rate on base cost
        double vatFees = (baseCost + totalTariff) * (vatRate / 100);
        // add base cost + tariff cost + vat cost
        double total = baseCost + totalTariff + vatFees;

        return new RouteBreakdown(exporter.getName(),
                middle.getName(),
                importer.getName(),
                baseCost,
                totalTariff,
                vatFees,
                total,
                vatRate / 100,
                petroleum.getName());
    }


    private void computeHubBasedRoutes(
        Country exporter, Country importer, Petroleum petroleum, int units,
        List<RouteBreakdown> candidateRoutes, double petroleumprice, LocalDate date) {

        List<RouteBreakdown> hubRouteList = new ArrayList<>();
        
        // // Get only hub countries
        // List<Country> hubCountries = countryService.getAll().stream()
        //     .filter(country -> OIL_TRADING_HUBS.contains(country.getISO3()))
        //     .filter(country -> !country.getISO3().equals(exporter.getISO3()) 
        //                     && !country.getISO3().equals(importer.getISO3()))
        //     .collect(Collectors.toList());

       // logger.info("Evaluating {} hub countries as transit points", hubCountries.size());
            System.out.println("starting compute hubbsased routes");
        // Calculate routes through each hub
        for (String hub : OIL_TRADING_HUBS) {

            if (hub == exporter.getISO3() || hub == importer.getISO3()){
                continue;
            }
            else{
                Country hubObj = countryService.getCountryByISO3(hub);
                logger.debug("hub obj == " + hubObj.getISO3());
                System.out.println("🌍 counrty  hub " + hubObj.getName());
                hubRouteList.add(computeRouteWithMiddle(exporter, hubObj, importer, 
                    petroleum, units, petroleumprice, date));
            }

        }
        
        logger.info("FINISHED - Total hub routes calculated: {}", hubRouteList.size());

        // Sort by total cost and pick best 5
        candidateRoutes.addAll(
            hubRouteList.stream()
                .sorted(Comparator.comparingDouble(RouteBreakdown::getTotalLandedCost))
                .limit(5)
                .collect(Collectors.toList()));
    }
}
