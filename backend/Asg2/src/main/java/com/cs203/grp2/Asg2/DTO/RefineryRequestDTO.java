package com.cs203.grp2.Asg2.DTO;

import java.util.List;

public class RefineryRequestDTO {
    private String name;
    private String company;
    private String location;
    private Integer operationalFrom;
    private Integer operationalTo;
    private boolean canRefineAny;
    private List<RefineryCostRequestDTO> estimatedCosts;
    private String countryIso3;
    private String countryIsoNumeric;

    public RefineryRequestDTO() {
    }

    public RefineryRequestDTO(String name, String company, String location, Integer operationalFrom,
            Integer operationalTo, boolean canRefineAny, List<RefineryCostRequestDTO> estimatedCosts,
            String countryIso3, String countryIsoNumeric) {
        this.name = name;
        this.company = company;
        this.location = location;
        this.operationalFrom = operationalFrom;
        this.operationalTo = operationalTo;
        this.canRefineAny = canRefineAny;
        this.estimatedCosts = estimatedCosts;
        this.countryIso3 = countryIso3;
        this.countryIsoNumeric = countryIsoNumeric;
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

    public Integer getOperationalFrom() {
        return operationalFrom;
    }

    public void setOperationalFrom(Integer operationalFrom) {
        this.operationalFrom = operationalFrom;
    }

    public Integer getOperationalTo() {
        return operationalTo;
    }

    public void setOperationalTo(Integer operationalTo) {
        this.operationalTo = operationalTo;
    }

    public boolean isCanRefineAny() {
        return canRefineAny;
    }

    public void setCanRefineAny(boolean canRefineAny) {
        this.canRefineAny = canRefineAny;
    }

    public List<RefineryCostRequestDTO> getEstimatedCosts() {
        return estimatedCosts;
    }

    public void setEstimatedCosts(List<RefineryCostRequestDTO> estimatedCosts) {
        this.estimatedCosts = estimatedCosts;
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
}