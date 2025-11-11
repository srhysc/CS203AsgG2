package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.ConvertableResponseDTO;
import java.util.List;

public interface ConvertableService {
    List<ConvertableResponseDTO> getAllConvertables();
    ConvertableResponseDTO getConvertableByHscode(String hscode);
}