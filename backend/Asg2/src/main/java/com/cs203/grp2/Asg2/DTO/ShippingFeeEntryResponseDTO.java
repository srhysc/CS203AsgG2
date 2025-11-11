package com.cs203.grp2.Asg2.DTO;

import java.time.LocalDate;
import java.util.Map;

public class ShippingFeeEntryResponseDTO {
    private LocalDate date;
    private Map<String, ShippingCostDetailResponseDTO> costs;

    public ShippingFeeEntryResponseDTO() {}
    public ShippingFeeEntryResponseDTO(LocalDate date, Map<String, ShippingCostDetailResponseDTO> costs) {
        this.date = date;
        this.costs = costs;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public Map<String, ShippingCostDetailResponseDTO> getCosts() {
        return costs;
    }
    public void setCosts(Map<String, ShippingCostDetailResponseDTO> costs) {
        this.costs = costs;
    }

    // getters and setters
}