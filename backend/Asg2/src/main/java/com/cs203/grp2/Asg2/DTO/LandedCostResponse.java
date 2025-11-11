package com.cs203.grp2.Asg2.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Map;
import java.util.HashMap;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double shippingCost;

    private String currency;

    //map of alternative routes
    private Map<String, RouteBreakdown> alternativeRoutes;

    public LandedCostResponse(){};

    public LandedCostResponse(String importingCountry, String exportingCountry,
                              String petroleumName, String hsCode,
                              double pricePerUnit, double basePrice,
                              double tariffRate, double tariffFees,
                              double vatRate, double vatFees,
                              double totalLandedCost, String currency, double shippingCost,
                            Map<String, RouteBreakdown> alternativeRoutes) {
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
        this.shippingCost = shippingCost;
        this.alternativeRoutes = alternativeRoutes;
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
    public double getShippingCost() { return shippingCost; }
    public Map<String, RouteBreakdown> getAlternativeRoutes() { return alternativeRoutes; }

    //setters so firebase can populate
    public void setImportingCountry(String importingCountry) { this.importingCountry = importingCountry; }
    public void setExportingCountry(String exportingCountry) { this.exportingCountry = exportingCountry; }
    public void setPetroleumName(String petroleumName) { this.petroleumName = petroleumName; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public void setTariffRate(double tariffRate) { this.tariffRate = tariffRate; }
    public void setTariffFees(double tariffFees) { this.tariffFees = tariffFees; }
    public void setVatRate(double vatRate) { this.vatRate = vatRate; }
    public void setVatFees(double vatFees) { this.vatFees = vatFees; }
    public void setTotalLandedCost(double totalLandedCost) { this.totalLandedCost = totalLandedCost; }
    public void setShippingCost(double shippingCost) { this.shippingCost = shippingCost; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setAlternativeRoutes(Map<String, RouteBreakdown> alternativeRoutes) {
        this.alternativeRoutes = alternativeRoutes;
    }
}
