package com.cs203.grp2.Asg2.models;

import java.time.LocalDate;

public class VATRate {
    private LocalDate date;
    private double rate;

    public VATRate() {}

    public VATRate(LocalDate date, double rate) {
        this.date = date;
        this.rate = rate;
    }

    public LocalDate getDate() { return date; }
    public double getVATRate() { return rate; }

    
}
