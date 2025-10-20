package com.cs203.grp2.Asg2.DTO;

import java.util.List;

public class RouteBreakdown {
    private String exportingCountry;
    private List<String> transitCountries;
    private String importingCountry;

    private double baseCost;
    private double tariffFees;
    private double vatFees;          // only applied for importing country
    private double totalLandedCost;
    private double vatRate;          // only importing country

    public RouteBreakdown(String exportingCountry, List<String> transitCountries, String importingCountry,
                          double baseCost, double tariffFees, double vatFees,
                          double totalLandedCost, double vatRate) {
        this.exportingCountry = exportingCountry;
        this.transitCountries = transitCountries;
        this.importingCountry = importingCountry;
        this.baseCost = baseCost;
        this.tariffFees = tariffFees;
        this.vatFees = vatFees;
        this.totalLandedCost = totalLandedCost;
        this.vatRate = vatRate;
    }

    public String getExportingCountry() { return exportingCountry; }
    public List<String> getTransitCountries() { return transitCountries; }
    public String getImportingCountry() { return importingCountry; }
    public double getBaseCost() { return baseCost; }
    public double getTariffFees() { return tariffFees; }
    public double getVatFees() { return vatFees; }
    public double getTotalLandedCost() { return totalLandedCost; }
    public double getVatRate() { return vatRate; }
}
