package com.cs203.grp2.Asg2.DTO;

import java.time.LocalDate;

public class PetroleumLatestPriceDTO {
    private String name;
    private String hsCode;
    private Double latestPrice;
    private String unit;
    private LocalDate date;

    public PetroleumLatestPriceDTO() {
        // Default constructor for serialization/deserialization
    }

    public PetroleumLatestPriceDTO(String name, String hsCode, Double latestPrice, String unit, LocalDate date) {
        this.name = name;
        this.hsCode = hsCode;
        this.latestPrice = latestPrice;
        this.unit = unit;
        this.date = date;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getHsCode() {
        return hsCode;
    }

    public Double getLatestPrice() {
        return latestPrice;
    }

    public String getUnit() {
        return unit;
    }

    public LocalDate getDate() {
        return date;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public void setLatestPrice(Double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PetroleumLatestPriceDTO{" +
                "name='" + name + '\'' +
                ", hsCode='" + hsCode + '\'' +
                ", latestPrice=" + latestPrice +
                ", unit='" + unit + '\'' +
                ", date=" + date +
                '}';
    }
}
