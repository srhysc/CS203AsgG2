package com.cs203.grp2.Asg2.country;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository repo;

    public CountryService(CountryRepository repo) {
        this.repo = repo;
    }

    public List<Country> getAllCountries() {
        return repo.findAll();
    }

    public Country getCountryByISO3n(int iso3n) {
        return repo.findById(iso3n)
                   .orElseThrow(() -> new CountryNotFoundException("No country with iso3n=" + iso3n));
    }

    public Country getCountryByName(String name) {
    return countries.stream()
            .filter(c -> c.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
}
}
