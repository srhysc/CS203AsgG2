package com.cs203.grp2.Asg2.vat;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class VATService {

    private final Map<String, Double> vatRates = new HashMap<>();

    public VATService() {
        // Example VAT rates
        vatRates.put("France", 20.0);
        vatRates.put("Germany", 19.0);
        vatRates.put("Italy", 22.0);
        vatRates.put("USA", 0.0);  // No VAT in USA
    }

    public VAT getVatRate(String country) {
        Double rate = vatRates.get(country);
        if (rate == null) {
            return null;
        }
        return new VAT(country, rate);
    }

    public void addOrUpdateVatRate(VAT vat) {
        vatRates.put(vat.getCountry(), vat.getVatRate());
    }

    public Collection<VAT> getAllVatRates() {
        return vatRates.entrySet().stream()
                .map(entry -> new VAT(entry.getKey(), entry.getValue()))
                .toList();
    }
}
