package com.cs203.grp2.Asg2.country;

import jakarta.persistence.*;

@Entity
@Table(name = "country_view") // mapping to SQL View
public class Country {

    @Id
    private Long iso3n;   

    private String name;

    @Column(name = "vatRate") 
    private Double vatRate;   

    //required by JPA
    protected Country() {}

    // Parameterized constructor
    public Country(Long iso3n, String name, Double vatRate) {
        this.iso3n = iso3n;
        this.name = name;
        this.vatRate = vatRate;
    }

    public Long getIso3n() {
        return iso3n;
    }

    public void setIso3n(Long iso3n) {
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
