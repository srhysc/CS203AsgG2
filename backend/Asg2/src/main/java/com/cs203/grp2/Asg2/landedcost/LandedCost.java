package com.cs203.grp2.Asg2.landedcost;

public class LandedCost {

    private String productHsCode;
    private int importerIso6Code;
    private int exporterIso6Code;
    private int units;

    private double productUnitCost;  // price per unit
    private double tariffRate;       // %
    private double vatRate;          // %
    private double tariffAmount;
    private double vatAmount;
    private double totalLandedCost;

    public LandedCost() {}

    public LandedCost(String productHsCode, int importerIso6Code, int exporterIso6Code, int units,
                      double productUnitCost, double tariffRate, double vatRate,
                      double tariffAmount, double vatAmount, double totalLandedCost) {
        this.productHsCode = productHsCode;
        this.importerIso6Code = importerIso6Code;
        this.exporterIso6Code = exporterIso6Code;
        this.units = units;
        this.productUnitCost = productUnitCost;
        this.tariffRate = tariffRate;
        this.vatRate = vatRate;
        this.tariffAmount = tariffAmount;
        this.vatAmount = vatAmount;
        this.totalLandedCost = totalLandedCost;
    }

    // Getters & Setters (generate with IDE or Lombok)
    public String getProductHsCode() {
        return productHsCode;
    }
    public void setProductHsCode(String productHsCode) {
        this.productHsCode = productHsCode;
    }

    public int getImporterIso6Code() {
        return importerIso6Code;
    }
    public void setImporterIso6Code(int importerIso6Code) {
        this.importerIso6Code = importerIso6Code;
    }

    public int getExporterIso6Code() {
        return exporterIso6Code;
    }
    public void setExporterIso6Code(int exporterIso6Code) {
        this.exporterIso6Code = exporterIso6Code;
    }

    public int getUnits() {
        return units;
    }
    public void setUnits(int units) {
        this.units = units;
    }

    public double getProductUnitCost() {
        return productUnitCost;
    }
    public void setProductUnitCost(double productUnitCost) {
        this.productUnitCost = productUnitCost;
    }

    public double getTariffRate() {
        return tariffRate;
    }
    public void setTariffRate(double tariffRate) {
        this.tariffRate = tariffRate;
    }

    public double getVatRate() {
        return vatRate;
    }
    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    public double getTariffAmount() {
        return tariffAmount;
    }
    public void setTariffAmount(double tariffAmount) {
        this.tariffAmount = tariffAmount;
    }

    public double getVatAmount() {
        return vatAmount;
    }
    public void setVatAmount(double vatAmount) {
        this.vatAmount = vatAmount;
    }

    public double getTotalLandedCost() {
        return totalLandedCost;
    }
    public void setTotalLandedCost(double totalLandedCost) {
        this.totalLandedCost = totalLandedCost;
    }
}
