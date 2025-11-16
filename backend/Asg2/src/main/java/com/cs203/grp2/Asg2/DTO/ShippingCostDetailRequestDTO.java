package com.cs203.grp2.Asg2.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ShippingCostDetailRequestDTO {
    @JsonProperty("cost_per_unit")
    private Double costPerUnit;
    private String unit;
    public Double getCostPerUnit() {
        return costPerUnit;
    }
    public void setCostPerUnit(Double costPerUnit) {
        this.costPerUnit = costPerUnit;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

}