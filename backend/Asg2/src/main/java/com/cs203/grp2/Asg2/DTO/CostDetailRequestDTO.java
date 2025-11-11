package com.cs203.grp2.Asg2.DTO;

public class CostDetailRequestDTO {
    private double cost_per_unit;
    private String unit;

    public CostDetailRequestDTO() {}

    public double getCost_per_unit() {
        return cost_per_unit;
    }

    public void setCost_per_unit(double cost_per_unit) {
        this.cost_per_unit = cost_per_unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // Getters and setters
}