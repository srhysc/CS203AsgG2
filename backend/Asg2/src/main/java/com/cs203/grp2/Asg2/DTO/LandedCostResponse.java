package com.cs203.grp2.Asg2.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

public class LandedCostResponse {
    private String importingCountry;
    private String exportingCountry;

    private String petroleumName;
    private String hsCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double pricePerUnit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double basePrice;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double tariffRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double tariffFees;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double vatRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double vatFees;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double totalLandedCost;

    private String currency;

    public LandedCostResponse(String importingCountry, String exportingCountry,
                              String petroleumName, String hsCode,
                              double pricePerUnit, double basePrice,
                              double tariffRate, double tariffFees,
                              double vatRate, double vatFees,
                              double totalLandedCost, String currency) {
        this.importingCountry = importingCountry;
        this.exportingCountry = exportingCountry;
        this.petroleumName = petroleumName;
        this.hsCode = hsCode;
        this.pricePerUnit = pricePerUnit;
        this.basePrice = basePrice;
        this.tariffRate = tariffRate;
        this.tariffFees = tariffFees;
        this.vatRate = vatRate;
        this.vatFees = vatFees;
        this.totalLandedCost = totalLandedCost;
        this.currency = currency;
    }

    // Getters only (immutability)
    public String getImportingCountry() { return importingCountry; }
    public String getExportingCountry() { return exportingCountry; }
    public String getPetroleumName() { return petroleumName; }
    public String getHsCode() { return hsCode; }
    public double getPricePerUnit() { return pricePerUnit; }
    public double getBasePrice() { return basePrice; }
    public double getTariffRate() { return tariffRate; }
    public double getTariffFees() { return tariffFees; }
    public double getVatRate() { return vatRate; }
    public double getVatFees() { return vatFees; }
    public double getTotalLandedCost() { return totalLandedCost; }
    public String getCurrency() { return currency; }
}
