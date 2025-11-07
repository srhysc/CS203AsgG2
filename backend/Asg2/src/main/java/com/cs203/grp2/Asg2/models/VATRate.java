package com.cs203.grp2.Asg2.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VATRate {
    private LocalDate date;
    private double rate;

    public VATRate() {}

    public VATRate(LocalDate date, double rate) {
        this.date = date;
        this.rate = rate;
    }

    public LocalDate getDate() { return date; }


    //added json properly to connect to the front end
    @JsonProperty("rate")
    public double getVATRate() { return rate; }


    
    
}
