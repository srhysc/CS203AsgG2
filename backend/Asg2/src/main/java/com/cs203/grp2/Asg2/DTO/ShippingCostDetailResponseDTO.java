package com.cs203.grp2.Asg2.DTO;

public class ShippingCostDetailResponseDTO {
    private Double costPerUnit;
    private String unit;

    public ShippingCostDetailResponseDTO() {}
    public Double getCostPerUnit() {
        return costPerUnit;
    }
    public String getUnit() {
        return unit;
    }
    public ShippingCostDetailResponseDTO(Double costPerUnit, String unit) {
        this.costPerUnit = costPerUnit;
        this.unit = unit;
    }

    // getters and setters
}