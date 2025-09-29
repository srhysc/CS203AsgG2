package com.cs203.grp2.Asg2.petroleum;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "petroleum_view") // mapping to SQL View
public class Petroleum {
    private String name;

    @Id
    private String hsCode;
    private double pricePerUnit;

    public Petroleum () {}

    public Petroleum(String name, String hsCode, double pricePerUnit) {
        this.name = name;
        this.hsCode = hsCode;
        this.pricePerUnit = pricePerUnit;
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
}
