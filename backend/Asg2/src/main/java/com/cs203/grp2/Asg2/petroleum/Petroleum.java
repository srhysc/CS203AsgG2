package com.cs203.grp2.Asg2.petroleum;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
public class Petroleum {
    private String name;

    @Id
    private String hsCode; // HS6 code
    private double pricePerUnit;
    private String unit; // e.g. USD/ton, USD/barrel

    public Petroleum () {}

    public Petroleum(String name, String hsCode, double pricePerUnit, String unit) {
        this.name = name;
        this.hsCode = hsCode;
        this.pricePerUnit = pricePerUnit;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getHsCode() {
        return hsCode;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public String getUnit() {
        return unit;
    }
}
