package com.cs203.grp2.Asg2.models;

import java.util.List;

public class ShippingFee {
    private String country1Name;
    private String country1Iso3;
    private String country1IsoNumeric;
    private String country2Name;
    private String country2Iso3;
    private String country2IsoNumeric;
    private List<ShippingFeeEntry> shippingFees;

    public ShippingFee() {}

    public ShippingFee(String country1Name, String country1Iso3, String country1IsoNumeric,
                       String country2Name, String country2Iso3, String country2IsoNumeric,
                       List<ShippingFeeEntry> shippingFees) {
        this.country1Name = country1Name;
        this.country1Iso3 = country1Iso3;
        this.country1IsoNumeric = country1IsoNumeric;
        this.country2Name = country2Name;
        this.country2Iso3 = country2Iso3;
        this.country2IsoNumeric = country2IsoNumeric;
        this.shippingFees = shippingFees;
    }

    public String getCountry1Name() { return country1Name; }
    public String getCountry1Iso3() { return country1Iso3; }
    public String getCountry1IsoNumeric() { return country1IsoNumeric; }
    public String getCountry2Name() { return country2Name; }
    public String getCountry2Iso3() { return country2Iso3; }
    public String getCountry2IsoNumeric() { return country2IsoNumeric; }
    public List<ShippingFeeEntry> getShippingFees() { return shippingFees; }
    public void setShippingFees(List<ShippingFeeEntry> shippingFees) { this.shippingFees = shippingFees; }
}