package com.cs203.grp2.Asg2.models;

import jakarta.validation.constraints.NotBlank;

public class TradeAgreement {

    @NotBlank(message = "Agreement name must not be blank")
    private String agreementName;

    @NotBlank(message = "Country A must not be blank")
    private String countryA;

    @NotBlank(message = "Country B must not be blank")
    private String countryB;

    public TradeAgreement() {
    }

    public TradeAgreement(String agreementName, String countryA, String countryB) {
        this.agreementName = agreementName;
        this.countryA = countryA;
        this.countryB = countryB;
    }

    public String getAgreementName() {
        return agreementName;
    }

    public void setAgreementName(String agreementName) {
        this.agreementName = agreementName;
    }

    public String getCountryA() {
        return countryA;
    }

    public void setCountryA(String countryA) {
        this.countryA = countryA;
    }

    public String getCountryB() {
        return countryB;
    }

    public void setCountryB(String countryB) {
        this.countryB = countryB;
    }

    @Override
    public String toString() {
        return "TradeAgreement{" +
                "agreementName='" + agreementName + '\'' +
                ", countryA='" + countryA + '\'' +
                ", countryB='" + countryB + '\'' +
                '}';
    }
}
