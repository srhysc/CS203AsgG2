package com.cs203.grp2.Asg2.vat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/vat")
@Validated
public class VATController {

    private final VATService vatService;

    public VATController(VATService vatService) {
        this.vatService = vatService;
    }

    // GET /vat?country=France  OR GET /vat to get all VAT rates
    @GetMapping
    public Object getVatRate(@RequestParam(value = "country", required = false) String country) {
        if (country == null || country.isBlank()) {
            return vatService.getAllVatRates();
        }
        VAT vat = vatService.getVatRate(country);
        if (vat == null) {
            throw new VATNotFoundException("VAT rate not found for country: " + country);
        }
        return vat;
    }

    // POST /vat to add or update VAT
    @PostMapping
    public void addOrUpdateVatRate(@RequestBody @Valid VAT vat) {
        vatService.addOrUpdateVatRate(vat);
    }
}
