package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import java.time.LocalDate;
import java.util.List;

public interface ShippingFeesService {
    List<ShippingFeeResponseDTO> getAllShippingFees();
    ShippingFeeResponseDTO getShippingFees(String country1Iso3, String country2Iso3);
    List<ShippingFeeEntryResponseDTO> getAllCosts(String country1Iso3, String country2Iso3);
    List<ShippingFeeEntryResponseDTO> getAllCostsByUnit(String country1Iso3, String country2Iso3, String unit);
    ShippingFeeEntryResponseDTO getLatestCost(String country1Iso3, String country2Iso3, LocalDate date);
    ShippingCostDetailResponseDTO getCostByUnit(String country1Iso3, String country2Iso3, String unit, LocalDate date);
    ShippingFeeResponseDTO addOrUpdateShippingFee(ShippingFeeRequestDTO requestDTO);
}