package com.cs203.grp2.Asg2.models;

import java.util.*;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class Petroleum {
    @Id
    private String hsCode;
    
    private String name;

    @OneToMany(mappedBy = "petroleum", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PetroleumPrice> prices = new ArrayList<>();


    public Petroleum(String name, String hsCode, List<PetroleumPrice> prices) {
        this.name = name;
        this.hsCode = hsCode;
        this.prices = prices;
    }

    public String getName() {
        return name;
    }

    public String getHsCode() {
        return hsCode;
    }

    public List<PetroleumPrice> getPrices() {
         return prices; 
    }

    // to get latest price based on date 
    public Double getPricePerUnit(LocalDate inputDate) {
        return prices.stream()
            .filter(p -> !p.getDate().isAfter(inputDate)) // not later than date
            .max(Comparator.comparing(PetroleumPrice::getDate)) // pick the latest date
            .map(PetroleumPrice::getAvgPricePerUnitUsd) //extract price per unit to return
            .orElse(null);
    }

}
