package com.cs203.grp2.Asg2.models;

import java.time.LocalDate;

public class PetroleumPrice {
    private LocalDate date;
    private double avgPricePerUnitUsd;
    private String unit;

    public PetroleumPrice() {}

    public PetroleumPrice(LocalDate date, double avgPricePerUnitUsd, String unit) {
        this.date = date;
        this.avgPricePerUnitUsd = avgPricePerUnitUsd;
        this.unit = unit;
    }

    public LocalDate getDate() { return date; }
    public double getAvgPricePerUnitUsd() { return avgPricePerUnitUsd; }
    public String getUnit() { return unit; }
}
