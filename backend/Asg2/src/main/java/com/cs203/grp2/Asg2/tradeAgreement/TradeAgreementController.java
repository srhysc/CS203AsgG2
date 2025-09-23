package com.cs203.grp2.Asg2.tradeAgreements;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tradeAgreements")
@Validated
public class TradeAgreementController {

    private final TradeAgreementService svc;

    public TradeAgreementController(TradeAgreementService svc) {
        this.svc = svc;
    }

    // GET /tradeAgreements
    @GetMapping
    public List<TradeAgreement> getAllAgreements() {
        return svc.getAllAgreements();
    }

    // GET /tradeAgreements/{agreementName}
    @GetMapping("/{agreementName}")
    public TradeAgreement getAgreementByName(@PathVariable @NotBlank String agreementName) {
        return svc.getByAgreementName(agreementName)
                .orElseThrow(() -> new TradeAgreementNotFoundException("Trade agreement not found: " + agreementName));
    }

    // POST /tradeAgreements
    @PostMapping
    public void addAgreement(@RequestBody @Valid TradeAgreement agreement) {
        svc.addTradeAgreement(agreement);
    }

    // PUT /tradeAgreements/{agreementName}
    @PutMapping("/{agreementName}")
    public void updateAgreement(@PathVariable @NotBlank String agreementName, @RequestBody @Valid TradeAgreement updatedAgreement) {
        boolean updated = svc.updateAgreement(agreementName, updatedAgreement);
        if (!updated) {
            throw new TradeAgreementNotFoundException("Trade agreement not found: " + agreementName);
        }
    }

    // DELETE /tradeAgreements/{agreementName}
    @DeleteMapping("/{agreementName}")
    public void deleteAgreement(@PathVariable @NotBlank String agreementName) {
        boolean deleted = svc.deleteAgreement(agreementName);
        if (!deleted) {
            throw new TradeAgreementNotFoundException("Trade agreement not found: " + agreementName);
        }
    }
}
