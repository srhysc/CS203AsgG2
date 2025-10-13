package com.cs203.grp2.Asg2.country;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.List;

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
        try {
            
            return svc.init().get();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("return failed");
        }
        return null;
        
    }

    // Use numeric ISO3 code in path
    @GetMapping("/{iso3n}")
    public Country getCountryByISO3n(@PathVariable @Min(1) @Max(999) int iso3n) {
        return svc.getCountryByISO3n(iso3n);
    }
}
