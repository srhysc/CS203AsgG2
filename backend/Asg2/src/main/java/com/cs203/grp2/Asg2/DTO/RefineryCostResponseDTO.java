package com.cs203.grp2.Asg2.DTO;

import java.util.Map;

public class RefineryCostResponseDTO {
    private String date;
    private Map<String, CostDetailResponseDTO> costs;

    public RefineryCostResponseDTO() {}

    public RefineryCostResponseDTO(String date, Map<String, CostDetailResponseDTO> costs) {
        this.date = date;
        this.costs = costs;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Map<String, CostDetailResponseDTO> getCosts() { return costs; }
    public void setCosts(Map<String, CostDetailResponseDTO> costs) { this.costs = costs; }
}