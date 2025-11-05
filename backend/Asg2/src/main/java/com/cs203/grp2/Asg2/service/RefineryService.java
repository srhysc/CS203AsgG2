package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import java.util.List;

public interface RefineryService {
    List<RefineryResponseDTO> getAllRefineries();

    List<RefineryResponseDTO> getRefineriesByCountry(String countryIso3);

    RefineryResponseDTO getRefinery(String countryIso3, String refineryName);

    List<RefineryCostResponseDTO> getAllCosts(String countryIso3, String refineryName);

    List<RefineryCostResponseDTO> getLatestCost(String countryIso3, String refineryName, String dateStr);

    CostDetailResponseDTO getCostByUnit(String countryIso3, String refineryName, String unit, String dateStr);

    RefineryResponseDTO addOrUpdateRefinery(String countryIso3, RefineryRequestDTO refineryRequestDTO);
}