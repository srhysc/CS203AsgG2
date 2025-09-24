package com.cs203.grp2.Asg2.landedcost;

public class LandedCostResponse {
    private double totalCost;
    private String currency;

    public LandedCostResponse() {}

    public LandedCostResponse(double totalCost, String currency) {
        this.totalCost = totalCost;
        this.currency = currency;
    }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}

