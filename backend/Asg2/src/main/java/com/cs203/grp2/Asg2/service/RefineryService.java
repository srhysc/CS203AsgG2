package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.RefineryResponseDTO;
import com.cs203.grp2.Asg2.DTO.CostDetailResponseDTO;
import com.cs203.grp2.Asg2.DTO.RefineryCostResponseDTO;
import com.cs203.grp2.Asg2.DTO.RefineryRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface RefineryService {
    List<RefineryResponseDTO> getAllRefineries(); // For dropdown
    List<RefineryResponseDTO> getRefineriesByCountry(String countryIso3);
    RefineryResponseDTO getRefinery(String countryIso3, String refineryName);
    List<RefineryCostResponseDTO> getAllCosts(String countryIso3, String refineryName);
    RefineryCostResponseDTO getLatestCost(String countryIso3, String refineryName, LocalDate date);
     CostDetailResponseDTO getCostByUnit(String countryIso3, String refineryName, String unit, LocalDate date);
     RefineryResponseDTO addOrUpdateRefinery(String countryIso3, RefineryRequestDTO refineryRequestDTO);
}