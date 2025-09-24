package com.cs203.grp2.Asg2.petroleum;

import jakarta.validation.Valid;
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
    public Petroleum getPetroleum(@PathVariable String hsCode) {
        return service.getPetroleumByHsCode(hsCode)
                .orElseThrow(() -> new RuntimeException("Petroleum not found for HS code: " + hsCode));
    }

    @PostMapping
    public Petroleum addPetroleum(@Valid @RequestBody Petroleum petroleum) {
        return service.addPetroleum(petroleum);
    }

    @DeleteMapping("/{hsCode}")
    public void deletePetroleum(@PathVariable String hsCode) {
        service.deletePetroleum(hsCode);
    }
}
