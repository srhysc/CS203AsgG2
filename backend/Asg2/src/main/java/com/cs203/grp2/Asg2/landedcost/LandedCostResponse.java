package com.cs203.grp2.Asg2.landedcost;

public class LandedCostResponse {

    private String hsCode;
    private int importerIso6;
    private int exporterIso6;
    private int units;
    private double totalLandedCost;

    public LandedCostResponse(String hsCode, int importerIso6, int exporterIso6, int units, double totalLandedCost) {
        this.hsCode = hsCode;
        this.importerIso6 = importerIso6;
        this.exporterIso6 = exporterIso6;
        this.units = units;
        this.totalLandedCost = totalLandedCost;
    }

    // Getters & Setters
    public String getHsCode() {
        return hsCode;
    }
    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public int getImporterIso6() {
        return importerIso6;
    }
    public void setImporterIso6(int importerIso6) {
        this.importerIso6 = importerIso6;
    }

    public int getExporterIso6() {
        return exporterIso6;
    }
    public void setExporterIso6(int exporterIso6) {
        this.exporterIso6 = exporterIso6;
    }

    public int getUnits() {
        return units;
    }
    public void setUnits(int units) {
        this.units = units;
    }

    public double getTotalLandedCost() {
        return totalLandedCost;
    }
    public void setTotalLandedCost(double totalLandedCost) {
        this.totalLandedCost = totalLandedCost;
    }
}
