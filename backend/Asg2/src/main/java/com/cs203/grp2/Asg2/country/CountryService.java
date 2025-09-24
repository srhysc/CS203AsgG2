package com.cs203.grp2.Asg2.country;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CountryService {

    private final List<Country> countries = new ArrayList<>();

    // Add a new country
    public Country addCountry(Country country) {
        countries.add(country);
        return country;
    }

    // Get all countries
    public List<Country> getAllCountries() {
        return countries;
    }

    // Find a country by iso6Code
    public Optional<Country> getCountryByISO6code(int iso6Code) {
        return countries.stream()
                .filter(c -> c.getIso6Code() == iso6Code)
                .findFirst();
    }

    // Update a country by iso6Code
    public boolean updateCountry(int iso6Code, Country updatedCountry) {
        Optional<Country> existing = getCountryByISO6code(iso6Code);
        if (existing.isPresent()) {
            Country country = existing.get();
            country.setName(updatedCountry.getName());
            // Usually, iso6Code should not be changed - commenting out
            // country.setIso6Code(updatedCountry.getIso6Code());
            return true;
        }
        return false;
    }

    // Remove a country by iso6Code
    public boolean deleteCountry(int iso6Code) {
        return countries.removeIf(c -> c.getIso6Code() == iso6Code);
    }
}
