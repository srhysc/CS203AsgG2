package com.cs203.grp2.Asg2.DTO;

import java.time.LocalDate;

public class PetroleumPriceDTO {
    private String name;
    private String hsCode;
    private Double price;
    private String unit;
    private LocalDate date;

    public PetroleumPriceDTO() {

    }

    public PetroleumPriceDTO(String name, String hsCode, Double price, String unit, LocalDate date) {
        this.name = name;
        this.hsCode = hsCode;
        this.price = price;
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

    public Double getPrice() {
        return price;
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

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PetroleumPriceDTO{" +
                "name='" + name + '\'' +
                ", hsCode='" + hsCode + '\'' +
                ", price=" + price +
                ", unit='" + unit + '\'' +
                ", date=" + date +
                '}';
    }
}
