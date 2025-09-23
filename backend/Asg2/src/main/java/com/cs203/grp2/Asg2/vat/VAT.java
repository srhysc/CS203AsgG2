package com.cs203.grp2.Asg2.vat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class VAT {

    @NotBlank(message = "Country must not be blank")
    private String country;

    @Min(value = 0, message = "VAT rate must be zero or positive")
    private double vatRate;

    public VAT() {}

    public VAT(String country, double vatRate) {
        this.country = country;
        this.vatRate = vatRate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getVatRate() {
        return vatRate;
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    @Override
    public String toString() {
        return "VAT{" +
                "country='" + country + '\'' +
                ", vatRate=" + vatRate +
                '}';
    }
}
