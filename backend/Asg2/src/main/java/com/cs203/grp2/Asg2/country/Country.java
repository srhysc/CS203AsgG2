package com.cs203.grp2.Asg2.country;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Country {
    @Id
    private int iso3n;   // numeric ISO code
    private String name;
    private double vatRate;

    public Country(int iso3n, String name, double vatRate) {
        this.iso3n = iso3n;
        this.name = name;
        this.vatRate = vatRate;
    }

    public int getIso3n() {
        return iso3n;
    }

    public void setIso3n(int iso3n) {
        this.iso3n = iso3n;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVatRate() {
        return vatRate;
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }
}
