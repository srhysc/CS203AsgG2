package com.cs203.grp2.Asg2.DTO;

import java.util.List;

public class RefineryRequestDTO {
    private String name;
    private String company;
    private String location;
    private Integer operational_from;
    private Integer operational_to;
    private boolean can_refine_any;
    private List<RefineryCostRequestDTO> estimated_costs;
    private String countryIso3;
    private String countryIsoNumeric;
    private String countryName;

    public RefineryRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getOperational_from() {
        return operational_from;
    }

    public void setOperational_from(Integer operational_from) {
        this.operational_from = operational_from;
    }

    public Integer getOperational_to() {
        return operational_to;
    }

    public void setOperational_to(Integer operational_to) {
        this.operational_to = operational_to;
    }

    public boolean isCan_refine_any() {
        return can_refine_any;
    }

    public void setCan_refine_any(boolean can_refine_any) {
        this.can_refine_any = can_refine_any;
    }

    public List<RefineryCostRequestDTO> getEstimated_costs() {
        return estimated_costs;
    }

    public void setEstimated_costs(List<RefineryCostRequestDTO> estimated_costs) {
        this.estimated_costs = estimated_costs;
    }

    public String getCountryIso3() {
        return countryIso3;
    }

    public void setCountryIso3(String countryIso3) {
        this.countryIso3 = countryIso3;
    }

    public String getCountryIsoNumeric() {
        return countryIsoNumeric;
    }

    public void setCountryIsoNumeric(String countryIsoNumeric) {
        this.countryIsoNumeric = countryIsoNumeric;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    // Getters and setters
}