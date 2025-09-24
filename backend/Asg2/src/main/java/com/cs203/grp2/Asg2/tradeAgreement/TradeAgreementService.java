package com.cs203.grp2.Asg2.tradeAgreements;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TradeAgreementService {

    private final List<TradeAgreement> agreements = new ArrayList<>();

    // Add a new trade agreement
    public TradeAgreement addTradeAgreement(TradeAgreement agreement) {
        agreements.add(agreement);
        return agreement;
    }

    // Get all agreements
    public List<TradeAgreement> getAllAgreements() {
        return agreements;
    }

    // Find by agreement name
    public Optional<TradeAgreement> getByAgreementName(String agreementName) {
        return agreements.stream()
                .filter(a -> a.getAgreementName().equalsIgnoreCase(agreementName))
                .findFirst();
    }

    // Update an agreement by name
    public boolean updateAgreement(String agreementName, TradeAgreement updatedAgreement) {
        Optional<TradeAgreement> existing = getByAgreementName(agreementName);
        if (existing.isPresent()) {
            TradeAgreement agreement = existing.get();
            agreement.setAgreementName(updatedAgreement.getAgreementName());
            agreement.setCountryA(updatedAgreement.getCountryA());
            agreement.setCountryB(updatedAgreement.getCountryB());
            return true;
        }
        return false;
    }

    // Delete by agreement name
    public boolean deleteAgreement(String agreementName) {
        return agreements.removeIf(a -> a.getAgreementName().equalsIgnoreCase(agreementName));
    }
}
