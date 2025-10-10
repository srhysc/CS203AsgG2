package com.cs203.grp2.Asg2.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ShippingFees {

    @Min(value = 0, message = "Fee must be zero or positive")
    private double fee;

    @NotBlank(message = "Importing country must not be blank")
    private String importingCountry;

    @NotBlank(message = "Exporting country must not be blank")
    private String exportingCountry;

    public ShippingFees() {}

    public ShippingFees(double fee, String importingCountry, String exportingCountry) {
        this.fee = fee;
        this.importingCountry = importingCountry;
        this.exportingCountry = exportingCountry;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getImportingCountry() {
        return importingCountry;
    }

    public void setImportingCountry(String importingCountry) {
        this.importingCountry = importingCountry;
    }

    public String getExportingCountry() {
        return exportingCountry;
    }

    public void setExportingCountry(String exportingCountry) {
        this.exportingCountry = exportingCountry;
    }

    @Override
    public String toString() {
        return "ShippingFees{" +
                "fee=" + fee +
                ", importingCountry='" + importingCountry + '\'' +
                ", exportingCountry='" + exportingCountry + '\'' +
                '}';
    }
}
