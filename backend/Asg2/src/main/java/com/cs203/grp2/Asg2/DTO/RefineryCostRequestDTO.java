package com.cs203.grp2.Asg2.DTO;

import java.time.LocalDate;
import java.util.Map;

public class RefineryCostRequestDTO {
    private LocalDate date;
    private Map<String, CostDetailRequestDTO> costs;

    public RefineryCostRequestDTO() {}

    public RefineryCostRequestDTO(LocalDate date, Map<String, CostDetailRequestDTO> costs) {
        this.date = date;
        this.costs = costs;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, CostDetailRequestDTO> getCosts() {
        return costs;
    }

    public void setCosts(Map<String, CostDetailRequestDTO> costs) {
        this.costs = costs;
    }
}