package com.cs203.grp2.Asg2.country;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CountryService {

    /*private final CountryRepository repo;
     * public CountryService(CountryRepository repo) {
     *   this.repo = repo;
     * }
     * 
     * public List<Country> getAllCountries() {
     *   return repo.findAll();
     * }
     * 
     * public Country getCountryByISO3n(int iso3n) {
     *   return repo.findByIso3n(iso3n).orElseThrow(() -> new CountryNotFoundException("Country not found: " + iso3n));
    */

    private final List<Country> countries = new ArrayList<>();

    public CountryService() {
        // Mock data using numeric ISO codes
        countries.add(new Country(702, "Singapore", 0.08)); // SGP → 702
        countries.add(new Country(156, "China", 0.13)); // CHN → 156
    }

    public List<Country> getAllCountries() {
        return countries;
    }

    public Country getCountryByISO3n(int iso3n) {
        return countries.stream()
                .filter(c -> c.getIso3n() == iso3n)
                .findFirst()
                .orElse(null);
    }
}

