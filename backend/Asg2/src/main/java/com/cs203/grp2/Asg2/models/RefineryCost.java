package com.cs203.grp2.Asg2.models;

import java.util.Map;

public class RefineryCost {
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, CostDetail> getCosts() {
        return costs;
    }

    public void setCosts(Map<String, CostDetail> costs) {
        this.costs = costs;
    }

    private String date;
    private Map<String, CostDetail> costs;

    public RefineryCost() {}

    public RefineryCost(String date, Map<String, CostDetail> costs) {
        this.date = date;
        this.costs = costs;
    }

    // Getters and setters
}