package com.cs203.grp2.Asg2.wits;

import com.cs203.grp2.Asg2.wits.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/wits")
public class WitsController {

    private final WitsService witsService;

    public WitsController(WitsService witsService) {
        this.witsService = witsService;
    }

    // Example: GET /wits/tariff?importer=BGR&exporter=CHN&hs6=271012&date=2025-10-14
    @GetMapping("/tariff")
    public TariffResponseDTO getTariff(
            @RequestParam String importer,
            @RequestParam String exporter,
            @RequestParam String hs6,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        var req = new TariffRequestDTO(importer.toUpperCase(), exporter.toUpperCase(), hs6, date);
        var res = witsService.resolveTariff(req);
        return new TariffResponseDTO(res.ratePercent(), res.basis(), res.sourceNote());
    }
}
