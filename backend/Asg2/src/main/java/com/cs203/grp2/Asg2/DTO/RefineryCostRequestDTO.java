package com.cs203.grp2.Asg2.DTO;

import java.util.Map;

public class RefineryCostRequestDTO {
    private String date;
    private Map<String, CostDetailRequestDTO> costs;

    public RefineryCostRequestDTO() {}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, CostDetailRequestDTO> getCosts() {
        return costs;
    }

    public void setCosts(Map<String, CostDetailRequestDTO> costs) {
        this.costs = costs;
    }

    // Getters and setters
}