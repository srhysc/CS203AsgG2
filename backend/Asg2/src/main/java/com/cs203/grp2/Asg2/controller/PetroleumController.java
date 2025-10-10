package com.cs203.grp2.Asg2.controller;

import org.springframework.web.bind.annotation.*;

import com.cs203.grp2.Asg2.models.Petroleum;
import com.cs203.grp2.Asg2.service.PetroleumService;

import jakarta.validation.constraints.Pattern; 

import java.util.List;

@RestController
@RequestMapping("/petroleum")
public class PetroleumController {

    private final PetroleumService service;

    public PetroleumController(PetroleumService service) {
        this.service = service;
    }

    @GetMapping
    public List<Petroleum> getAllPetroleum() {
        return service.getAllPetroleum();
    }

    @GetMapping("/{hsCode}")
    public Petroleum getPetroleumByHsCode(@PathVariable @Pattern(regexp = "\\d{4,6}") String hsCode) {
        return service.getPetroleumByHsCode(hsCode);
    }
}
