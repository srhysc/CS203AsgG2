package com.cs203.grp2.Asg2.DTO;

import java.time.LocalDate;
import java.util.Map;

public class RefineryCostResponseDTO {
    private LocalDate date;
    private Map<String, CostDetailResponseDTO> costs;

    public RefineryCostResponseDTO() {}

    public RefineryCostResponseDTO(LocalDate date, Map<String, CostDetailResponseDTO> costs) {
        this.date = date;
        this.costs = costs;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, CostDetailResponseDTO> getCosts() {
        return costs;
    }

    public void setCosts(Map<String, CostDetailResponseDTO> costs) {
        this.costs = costs;
    }
}