package com.cs203.grp2.Asg2.country;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CountryService {
    private final Map<Integer, Country> countries = new HashMap<>();

    public CountryService() {
        // Pre-load some countries with ISO6code as key
        countries.put(250, new Country("France", 250));
        countries.put(276, new Country("Germany", 276));
    }

    public Country getCountryByCode(int iso6code) {
        return countries.get(iso6code);
    }

    public void addCountry(Country country) {
        countries.put(country.getISO6code(), country);
    }

    // You can add update/delete methods here as needed
}
