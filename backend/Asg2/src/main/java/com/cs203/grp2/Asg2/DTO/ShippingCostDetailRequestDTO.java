package com.cs203.grp2.Asg2.DTO;

public class ShippingCostDetailRequestDTO {
    private double costPerUnit;
    private String unit;
    public double getCostPerUnit() {
        return costPerUnit;
    }
    public void setCostPerUnit(double costPerUnit) {
        this.costPerUnit = costPerUnit;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

}