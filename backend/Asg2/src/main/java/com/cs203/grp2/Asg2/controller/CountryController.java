package com.cs203.grp2.Asg2.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import com.cs203.grp2.Asg2.models.*;
import com.cs203.grp2.Asg2.exceptions.*;
import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.controller.*;
import com.cs203.grp2.Asg2.config.*;
import com.cs203.grp2.Asg2.service.*;

import java.util.List;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/countries")
public class CountryController {

    private final CountryService svc;

    public CountryController(CountryService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Country> getAllCountries() {
        // try {

        // return svc.getAll();
        // } catch (Exception e) {

        // // TODO: handle exception
        // System.out.println("return failed");
        // }
        // return null;
        try {
            return svc.getAll();
        } catch (Exception e) {
            throw new CountryNotFoundException("Failed to retrieve countries: " + e.getMessage());
        }

    }

    // Use numeric ISO3 code in path
    @GetMapping("/{iso3n}")
    public Country getCountryByCode(@PathVariable @Min(1) @Max(999) int iso3n) {
        String s = "" + iso3n;
        Country country = svc.getCountryByCode(s);
        if (country == null) {
            throw new CountryNotFoundException("Country not found for code: " + iso3n);
        }
        return country;
    }

    // Added this to connect to the frontedn: Get VAT rate for a country by name and
    // date
    @GetMapping("/{countryName}/vat-rate")
    public VATRate getVatRateForCountryAndDate(
            @PathVariable String countryName,
            @RequestParam String date) {
        // System.out.println("Received VAT rate request for: " + countryName + " on " +
        // date);
        Country country = svc.getCountryByName(countryName);
        // System.out.println("Country object: " + country);
        if (country == null) {
            throw new CountryNotFoundException("Country not found: " + countryName);
        }

        if (country.getVatRates() == null || country.getVatRates().isEmpty()) {
            // System.out.println("No VAT rates found for country: " + countryName);
            throw new CountryNotFoundException("No VAT rates found for country: " + countryName);
        }

        // LocalDate queryDate = LocalDate.parse(date);
        // Optional<VATRate> found = country.getVatRates().stream()
        // .filter(rate -> !rate.getDate().isAfter(queryDate))
        // .max(Comparator.comparing(VATRate::getDate));

        // if (found.isPresent()) {
        // System.out.println("Found VAT rate: " + found.get().getVATRate() + " on " +
        // found.get().getDate());
        // return found.get();
        // } else {
        // System.out.println("No VAT rate found for country " + countryName + " on or
        // before " + date);
        // throw new RuntimeException("No VAT rate found for this date.");
        // }
        LocalDate queryDate;
        try {
            queryDate = LocalDate.parse(date);
        } catch (Exception e) {
            throw new GeneralBadRequestException("Invalid date format: " + date);
        }
        Optional<VATRate> found = country.getVatRates().stream()
                .filter(rate -> !rate.getDate().isAfter(queryDate))
                .max(Comparator.comparing(VATRate::getDate));
        if (found.isPresent()) {
            return found.get();
        } else {
            throw new CountryNotFoundException(
                    "No VAT rate found for country " + countryName + " on or before " + date);
        }
    }


    @PostMapping("/{countryName}/vat-ratenew")
    public ResponseEntity<String> addVatRate(
        @PathVariable String countryName,
        @RequestBody VATRate newRate
        ) {
        try {
            countryService.addVatRate(countryName, newRate);
            return ResponseEntity.ok("VAT rate added for " + countryName);
        } catch (CountryNotFoundException e) {
            throw e; // handled by GlobalControllerExceptionHandler
        } catch (Exception e) {
            throw new RuntimeException("Failed to add VAT rate: " + e.getMessage(), e);
        }
    }


}
