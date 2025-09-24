package com.cs203.grp2.Asg2.shippingFees;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shippingFees")
@Validated
public class ShippingFeesController {

    private final ShippingFeesService svc;

    public ShippingFeesController(ShippingFeesService svc) {
        this.svc = svc;
    }

    // GET /shippingFees?importing=France&exporting=Germany
    @GetMapping
    public ShippingFees getShippingFee(
            @RequestParam("importing") @NotBlank String importingCountry,
            @RequestParam("exporting") @NotBlank String exportingCountry) {

        ShippingFees fee = svc.getFee(importingCountry, exportingCountry);
        if (fee == null) {
            throw new ShippingFeesNotFoundException(
                String.format("Shipping fee not found for importing='%s', exporting='%s'",
                              importingCountry, exportingCountry));
        }
        return fee;
    }

    // POST /shippingFees
    @PostMapping
    public void addShippingFee(@RequestBody @Valid ShippingFees shippingFees) {
        svc.addShippingFee(shippingFees);
    }
}
