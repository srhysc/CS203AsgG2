package com.cs203.grp2.Asg2.petroleum;

import jakarta.persistence.Entity;

import java.time.LocalDate;

import javax.annotation.processing.Generated;

@Entity
public class PetroleumPriceHistory {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    private localDate effectiveDate;
    private double pricePerUnit;

    @ManyToOne(fetch = fetchType.LAZY)
    @JoinColumn(name = "hsCode", nullable = false)
    private Petroleum petroleum;

    public PetroleumPriceHistory() {}

    public PetroleumPriceHistory(LocalDate effectiveDate, double pricePerUnit, Petroleum petroleum) {
        this.effectiveDate = effectiveDate;
        this.pricePerUnit = pricePerUnit;
        this.petroleum = petroleum;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }
    
    public Petroleum getPetroleum() {
        return petroleum;
    }

}
