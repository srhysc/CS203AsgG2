package com.cs203.grp2.Asg2.DTO;

import java.util.List;

public class RefineryResponseDTO {
    private String name;
    private String company;
    private String location;
    private Integer operational_from;
    private Integer operational_to;
    private boolean can_refine_any;
    private List<RefineryCostResponseDTO> estimated_costs;
    private String countryIso3;
    private String countryIsoNumeric;
    private String countryName;

    public RefineryResponseDTO() {}

    public RefineryResponseDTO(
        String name,
        String company,
        String location,
        Integer operational_from,
        Integer operational_to,
        boolean can_refine_any,
        List<RefineryCostResponseDTO> estimated_costs,
        String countryIso3,
        String countryIsoNumeric,
        String countryName
    ) {
        this.name = name;
        this.company = company;
        this.location = location;
        this.operational_from = operational_from;
        this.operational_to = operational_to;
        this.can_refine_any = can_refine_any;
        this.estimated_costs = estimated_costs;
        this.countryIso3 = countryIso3;
        this.countryIsoNumeric = countryIsoNumeric;
        this.countryName = countryName;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getOperational_from() { return operational_from; }
    public void setOperational_from(Integer operational_from) { this.operational_from = operational_from; }

    public Integer getOperational_to() { return operational_to; }
    public void setOperational_to(Integer operational_to) { this.operational_to = operational_to; }

    public boolean isCan_refine_any() { return can_refine_any; }
    public void setCan_refine_any(boolean can_refine_any) { this.can_refine_any = can_refine_any; }

    public List<RefineryCostResponseDTO> getEstimated_costs() { return estimated_costs; }
    public void setEstimated_costs(List<RefineryCostResponseDTO> estimated_costs) { this.estimated_costs = estimated_costs; }

    public String getCountryIso3() { return countryIso3; }
    public void setCountryIso3(String countryIso3) { this.countryIso3 = countryIso3; }

    public String getCountryIsoNumeric() { return countryIsoNumeric; }
    public void setCountryIsoNumeric(String countryIsoNumeric) { this.countryIsoNumeric = countryIsoNumeric; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
}