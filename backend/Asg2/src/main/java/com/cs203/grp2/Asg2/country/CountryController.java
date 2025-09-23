package com.cs203.grp2.Asg2.country;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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
    public Country getCountryByCode(@PathVariable("iso6code") @Min(1) int iso6Code) {
        return svc.getCountryByISO6code(iso6Code)
                .orElseThrow(() -> new CountryNotFoundException("Country not found for code: " + iso6Code));
    }

    // GET /countries (all countries)
    @GetMapping
    public Collection<Country> getAllCountries() {
        return svc.getAllCountries();
    }

    // POST /countries
    @PostMapping
    public void addCountry(@RequestBody @Valid Country country) {
        svc.addCountry(country);
    }
}
