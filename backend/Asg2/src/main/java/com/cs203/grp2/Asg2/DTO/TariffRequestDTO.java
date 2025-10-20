package com.cs203.grp2.Asg2.DTO;

import java.time.LocalDate;

public record TariffRequestDTO(
        String importerIso3,   // e.g., "BGR"
        String exporterIso3,   // e.g., "CHN"
        String hs6,            // e.g., "271012"
        LocalDate date         // effective date to check
) {
    public TariffRequestDTO(Integer importerIso3n, Integer exporterIso3n, String hs6, java.time.LocalDate date) {
        this(String.valueOf(importerIso3n), String.valueOf(exporterIso3n), hs6, date);
    }
}