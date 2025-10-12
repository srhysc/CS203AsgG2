package com.cs203.grp2.Asg2.petroleum;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Pattern; 

import java.util.List;
import java.time.LocalDate;

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
    public Petroleum getPetroleumByHsCode(@PathVariable @Pattern(regexp = "^[0-9]{6}$") String hsCode) {
        return service.getPetroleumByHsCode(hsCode);
    }

    @GetMapping("/{hsCode}/price/current")
    public double getCurrentPrice(@PathVariable @Pattern(regexp = "^[0-9]{6}$") String hsCode) {
        return service.getCurrentPrice(hsCode);
    }

    @GetMapping("/{hsCode}/price/{date}")
    public double getPriceOnDate(@PathVariable @Pattern(regexp = "^[0-9]{6}$") String hsCode, @PathVariable String date) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD.");
        }

        return service.getPriceOnDate(hsCode, localDate);
    }

}
