package com.cs203.grp2.Asg2.petroleum;

import org.springframework.web.bind.annotation.*;

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
    public Petroleum getPetroleumByHsCode(@PathVariable String hsCode) {
        return service.getPetroleumByHsCode(hsCode);
    }
}
