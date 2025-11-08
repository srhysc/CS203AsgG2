package com.cs203.grp2.Asg2.models;

public class CostDetail {
    private double cost_per_unit;
    private String unit;

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

    public CostDetail() {}

    public CostDetail(double cost_per_unit, String unit) {
        this.cost_per_unit = cost_per_unit;
        this.unit = unit;
    }

    // Getters and setters
}