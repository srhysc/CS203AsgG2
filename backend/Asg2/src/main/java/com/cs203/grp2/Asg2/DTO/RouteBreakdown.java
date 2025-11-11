package com.cs203.grp2.Asg2.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;


public class RouteBreakdown {
    private String exportingCountry;
    private String transitCountry;
    private String importingCountry;

    private double baseCost;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double tariffFees;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double vatFees;          // only applied for importing country
     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double totalLandedCost;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double vatRate;          // only importing country
    private String petroleumName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private double shippingCost;

    public RouteBreakdown(){}

    public RouteBreakdown(String exportingCountry, String transitCountry, String importingCountry,
                          double baseCost, double tariffFees, double vatFees,
                          double totalLandedCost, double vatRate, String petroleumName, double shippingCost) {
        this.exportingCountry = exportingCountry;
        this.transitCountry = transitCountry;
        this.importingCountry = importingCountry;
        this.baseCost = baseCost;
        this.tariffFees = tariffFees;
        this.vatFees = vatFees;
        this.totalLandedCost = totalLandedCost;
        this.vatRate = vatRate;
        this.petroleumName = petroleumName;
        this.shippingCost = shippingCost;
    }

    public String getExportingCountry() { return exportingCountry; }
    public String getTransitCountry() { return transitCountry; }
    public String getImportingCountry() { return importingCountry; }
    public double getBaseCost() { return baseCost; }
    public double getTariffFees() { return tariffFees; }
    public double getVatFees() { return vatFees; }
    public double getTotalLandedCost() { return totalLandedCost; }
    public double getVatRate() { return vatRate; }
    public double getShippingCost() { return shippingCost; }


        //setters so firebase can populate
    public void setImportingCountry(String importingCountry) { this.importingCountry = importingCountry; }
    public void setExportingCountry(String exportingCountry) { this.exportingCountry = exportingCountry; }
    public void setPetroleumName(String petroleumName) { this.petroleumName = petroleumName; }
    public void setTariffFees(double tariffFees) { this.tariffFees = tariffFees; }
    public void setVatRate(double vatRate) { this.vatRate = vatRate; }
    public void setTotalLandedCost(double totalLandedCost) { this.totalLandedCost = totalLandedCost; }
    public void setShippingCost(double shippingCost) { this.shippingCost = shippingCost; }

}
