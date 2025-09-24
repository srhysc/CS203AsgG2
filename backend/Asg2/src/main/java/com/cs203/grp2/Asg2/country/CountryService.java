package com.cs203.grp2.Asg2.country;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CountryService {

    private final List<Country> countries = new ArrayList<>();

    public CountryService() {
        // Add fake/sample data here
        countries.add(new Country(840, "United States", 7.5));
        countries.add(new Country(124, "Canada", 5.0));
        countries.add(new Country(276, "Germany", 19.0));
        countries.add(new Country(356, "India", 18.0));
    }
    
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
        country.setVatRate(updatedCountry.getVatRate());  // update VAT rate here
        return true;
    }
    return false;
}

    // Remove a country by iso6Code
    public boolean deleteCountry(int iso6Code) {
        return countries.removeIf(c -> c.getIso6Code() == iso6Code);
    }
}
