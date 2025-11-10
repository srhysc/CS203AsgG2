package com.cs203.grp2.Asg2.DTO;

import java.util.Map;

public class ShippingFeeEntryRequestDTO {
    private String date; // ISO string
    private Map<String, ShippingCostDetailRequestDTO> costs;
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Map<String, ShippingCostDetailRequestDTO> getCosts() {
        return costs;
    }
    public void setCosts(Map<String, ShippingCostDetailRequestDTO> costs) {
        this.costs = costs;
    }

    // getters and setters
}