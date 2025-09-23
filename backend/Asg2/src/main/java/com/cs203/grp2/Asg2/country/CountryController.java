package com.cs203.grp2.Asg2.country;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/countries")
@Validated
public class CountryController {

    private final CountryService svc;

    public CountryController(CountryService svc) {
        this.svc = svc;
    }

    // GET /countries/{iso6code}
    @GetMapping("/{iso6code}")
    public Country getCountryByCode(@PathVariable @Min(1) int iso6code) {
        Country country = svc.getCountryByCode(iso6code);
        if (country == null) {
            throw new CountryNotFoundException("Country not found for code: " + iso6code);
        }
        return country;
    }

    // POST /countries
    @PostMapping
    public void addCountry(@RequestBody @Valid Country country) {
        svc.addCountry(country);
    }
}
