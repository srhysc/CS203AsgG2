package com.cs203.grp2.Asg2.petroleum;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Petroleum {

    @Id
    private String hsCode;   // assuming HS code is a string (e.g., "270900")

    private String name;
    private double pricePerUnit;

    public Petroleum() {}

    public Petroleum(String hsCode, String name, double pricePerUnit) {
        this.hsCode = hsCode;
        this.name = name;
        this.pricePerUnit = pricePerUnit;
    }

    // Getters and Setters

    public String getHsCode() {
        return hsCode;
    }

    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
