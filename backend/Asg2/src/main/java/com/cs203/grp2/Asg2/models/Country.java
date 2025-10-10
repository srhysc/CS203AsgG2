package com.cs203.grp2.Asg2.models;

import jakarta.persistence.*;

@Entity
@Table(name = "country_view") // mapping to SQL View
public class Country {

    @Id
    private Integer iso3n;   // use Integer consistently

    private String name;

    @Column(name = "vatRate")
    private Double vatRate;

    // required by JPA
    protected Country() {}

    // Parameterized constructor
    public Country(Integer iso3n, String name, Double vatRate) {
        this.iso3n = iso3n;
        this.name = name;
        this.vatRate = vatRate;
    }

    public Integer getIso3n() {
        return iso3n;
    }

    public void setIso3n(Integer iso3n) {
        this.iso3n = iso3n;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getVatRate() {
        return vatRate;
    }

    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
    }
}