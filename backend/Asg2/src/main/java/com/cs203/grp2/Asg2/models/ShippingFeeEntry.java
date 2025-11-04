package com.cs203.grp2.Asg2.models;

import java.time.LocalDate;
import java.util.Map;

public class ShippingFeeEntry {
    private LocalDate date;
    private Map<String, ShippingCostDetail> costs; // key: unit ("barrel", "MMBtu", "ton")

    public ShippingFeeEntry() {}

    public ShippingFeeEntry(LocalDate date, Map<String, ShippingCostDetail> costs) {
        this.date = date;
        this.costs = costs;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Map<String, ShippingCostDetail> getCosts() { return costs; }
    public void setCosts(Map<String, ShippingCostDetail> costs) { this.costs = costs; }
}