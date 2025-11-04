package com.cs203.grp2.Asg2.models;

import java.util.List;

public class ShippingFee {
    private Country country1;
    private Country country2;
    private List<ShippingFeeEntry> shippingFees;

    public ShippingFee() {}

    public ShippingFee(Country country1, Country country2, List<ShippingFeeEntry> shippingFees) {
        this.country1 = country1;
        this.country2 = country2;
        this.shippingFees = shippingFees;
    }

    // getters and setters
    public Country getCountry1() { return country1; }
    public void setCountry1(Country country1) { this.country1 = country1; }
    public Country getCountry2() { return country2; }
    public void setCountry2(Country country2) { this.country2 = country2; }
    public List<ShippingFeeEntry> getShippingFees() { return shippingFees; }
    public void setShippingFees(List<ShippingFeeEntry> shippingFees) { this.shippingFees = shippingFees; }
}