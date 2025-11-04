package com.cs203.grp2.Asg2.DTO;

public class ShippingCostDetailResponseDTO {
    private double costPerUnit;
    private String unit;

    public ShippingCostDetailResponseDTO() {}
    public double getCostPerUnit() {
        return costPerUnit;
    }
    public String getUnit() {
        return unit;
    }
    public ShippingCostDetailResponseDTO(double costPerUnit, String unit) {
        this.costPerUnit = costPerUnit;
        this.unit = unit;
    }

    // getters and setters
}