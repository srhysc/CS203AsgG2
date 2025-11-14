package com.cs203.grp2.Asg2.controller;

import com.cs203.grp2.Asg2.DTO.ConvertableResponseDTO;
import com.cs203.grp2.Asg2.exceptions.ConvertableNotFoundException;
import com.cs203.grp2.Asg2.service.ConvertableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/convertables")
public class ConvertableController {

    @Autowired
    private ConvertableService convertableService;

    @GetMapping
    public List<ConvertableResponseDTO> getAllConvertables() {
        return convertableService.getAllConvertables();
    }

    @GetMapping("/{hscode}")
    public ConvertableResponseDTO getConvertableByHscode(@PathVariable String hscode) {
        ConvertableResponseDTO dto = convertableService.getConvertableByHscode(hscode);
        if (dto == null) {
            throw new ConvertableNotFoundException(hscode);
        }
        return dto;
    }
}