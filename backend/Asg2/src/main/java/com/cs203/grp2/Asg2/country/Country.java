package com.cs203.grp2.Asg2.country;

public class Country {

    private int iso6Code;
    private String name;
    private double vatRate;  // new VAT rate field

    public Country() {
    }

    public Country(int iso6Code, String name, double vatRate) {
        this.iso6Code = iso6Code;
        this.name = name;
        this.vatRate = vatRate;
    }

    public int getIso6Code() {
        return iso6Code;
    }

    public void setIso6Code(int iso6Code) {
        this.iso6Code = iso6Code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVatRate() {
        return vatRate;
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    @Override
    public String toString() {
        return "Country{" +
                "iso6Code=" + iso6Code +
                ", name='" + name + '\'' +
                ", vatRate=" + vatRate +
                '}';
    }
}
