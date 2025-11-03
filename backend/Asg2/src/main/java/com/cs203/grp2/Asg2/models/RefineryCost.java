package com.cs203.grp2.Asg2.models;

import java.time.LocalDate;
import java.util.Map;

public class RefineryCost {
    private LocalDate date;
    private Map<String, CostDetail> costs;

    public RefineryCost() {}

    public RefineryCost(LocalDate date, Map<String, CostDetail> costs) {
        this.date = date;
        this.costs = costs;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, CostDetail> getCosts() {
        return costs;
    }

    public void setCosts(Map<String, CostDetail> costs) {
        this.costs = costs;
    }
}