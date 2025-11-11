package com.cs203.grp2.Asg2.models;

public class MfnRate {
    private String countryIso3;
    private double mfnAve;
    private int year;

    public MfnRate() {}

    public MfnRate(String countryIso3, double mfnAve, int year) {
        this.countryIso3 = countryIso3;
        this.mfnAve = mfnAve;
        this.year = year;
    }

    public String getCountryIso3() { return countryIso3; }
    public void setCountryIso3(String countryIso3) { this.countryIso3 = countryIso3; }

    public double getMfnAve() { return mfnAve; }
    public void setMfnAve(double mfnAve) { this.mfnAve = mfnAve; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}