package com.cs203.grp2.Asg2.petroleum;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Petroleum {

    @Id
    private String hsCode; // HS6 code
    private String name;
    private String unit; // e.g. USD/ton, USD/barrel

    @OnetoMany(mappedBy = "petroleum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetroleumPriceHistory> priceHistory = new ArrayList<>();

    public Petroleum () {}

    public Petroleum(String name, String hsCode, String unit) {
        this.name = name;
        this.hsCode = hsCode;
        this.pricePerUnit = pricePerUnit;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getHsCode() {
        return hsCode;
    }

    public String getUnit() {
        return unit;
    }

    public List<PetroleumPriceHistory> getPriceHistory() {
        return priceHistory;
    }
}
