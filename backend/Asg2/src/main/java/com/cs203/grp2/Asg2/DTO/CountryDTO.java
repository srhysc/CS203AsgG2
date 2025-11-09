package com.cs203.grp2.Asg2.DTO;

import java.time.LocalDate;

public class CountryDTO {
    private String country;
    private double vatRate;
    private LocalDate lastUpdated;
    public CountryDTO() {} // Default constructor for deserialization

    public CountryDTO(String country, double vatRate, LocalDate lastUpdated) {
        this.country = country;
        this.vatRate = vatRate;
        this.lastUpdated = lastUpdated;
    }

    public String getCountry() {
        return country;
    }

    public double getVatRate() {
        return vatRate;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }
}


