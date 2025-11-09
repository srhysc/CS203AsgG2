package com.cs203.grp2.Asg2.DTO;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CountryDTO {
    private String country;
    private double rate;  
    private LocalDate lastUpdated;
    
    public CountryDTO() {}

    public CountryDTO(String country, double rate, LocalDate lastUpdated) {
        this.country = country;
        this.rate = rate;
        this.lastUpdated = lastUpdated;
    }

    public String getCountry() {
        return country;
    }

    @JsonProperty("rate")  
    public double getRate() {
        return rate;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }
}