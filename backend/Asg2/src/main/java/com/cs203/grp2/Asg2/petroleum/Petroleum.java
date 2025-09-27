package com.cs203.grp2.Asg2.petroleum;

public class Petroleum {
    private String name;
    private String hsCode;
    private double pricePerUnit;

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
