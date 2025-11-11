package com.cs203.grp2.Asg2.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class PetroleumPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @JsonProperty("avgPricePerUnitUsd")
    private double avgPricePerUnitUsd;

    private String unit;

    @ManyToOne
    @JoinColumn(name = "petroleum_hs_code")
    private Petroleum petroleum;

    public PetroleumPrice() {}

    public PetroleumPrice(LocalDate date, double avgPricePerUnitUsd, String unit) {
        this.date = date;
        this.avgPricePerUnitUsd = avgPricePerUnitUsd;
        this.unit = unit;
    }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { 
        this.date = date; 
    }

    public double getAvgPricePerUnitUsd() { return avgPricePerUnitUsd; }
    public void setAvgPricePerUnitUsd(double avgPricePerUnitUsd) { 
        this.avgPricePerUnitUsd = avgPricePerUnitUsd; 
    }

    public String getUnit() { return unit; }
     public void setUnit(String unit) { 
        this.unit = unit; 
    }
    
    public Petroleum getPetroleum() { return petroleum; }
    
    public void setPetroleum(Petroleum petroleum) { 
        this.petroleum = petroleum; 
    }
}
