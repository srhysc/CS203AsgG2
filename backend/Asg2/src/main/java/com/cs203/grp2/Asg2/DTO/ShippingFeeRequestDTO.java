package com.cs203.grp2.Asg2.DTO;

import java.util.List;

public class ShippingFeeRequestDTO {
    private String country1Iso3;
    private String country2Iso3;
    private String country1Name;
    private String country2Name;
    private String country1IsoNumeric;
    private String country2IsoNumeric;
    private List<ShippingFeeEntryRequestDTO> shippingFees;

    public String getCountry1Iso3() {
        return country1Iso3;
    }
    public void setCountry1Iso3(String country1Iso3) {
        this.country1Iso3 = country1Iso3;
    }
    public String getCountry2Iso3() {
        return country2Iso3;
    }
    public void setCountry2Iso3(String country2Iso3) {
        this.country2Iso3 = country2Iso3;
    }
    public String getCountry1Name() {
        return country1Name;
    }
    public void setCountry1Name(String country1Name) {
        this.country1Name = country1Name;
    }
    public String getCountry2Name() {
        return country2Name;
    }
    public void setCountry2Name(String country2Name) {
        this.country2Name = country2Name;
    }
    public String getCountry1IsoNumeric() {
        return country1IsoNumeric;
    }
    public void setCountry1IsoNumeric(String country1IsoNumeric) {
        this.country1IsoNumeric = country1IsoNumeric;
    }
    public String getCountry2IsoNumeric() {
        return country2IsoNumeric;
    }
    public void setCountry2IsoNumeric(String country2IsoNumeric) {
        this.country2IsoNumeric = country2IsoNumeric;
    }
    public List<ShippingFeeEntryRequestDTO> getShippingFees() {
        return shippingFees;
    }
    public void setShippingFees(List<ShippingFeeEntryRequestDTO> shippingFees) {
        this.shippingFees = shippingFees;
    }
}