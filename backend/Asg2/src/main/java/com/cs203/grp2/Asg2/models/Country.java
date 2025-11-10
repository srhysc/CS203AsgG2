package com.cs203.grp2.Asg2.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import java.time.LocalDate;

import com.google.firebase.database.PropertyName;

import com.cs203.grp2.Asg2.models.VATRate;


//@Table(name = "country_view") // mapping to SQL View
public class Country {

    // Parameterized constructor
    private String name;     // node key, e.g. "Albania"
    private String Code;    // "Code" like 702
    private String ISO3;     // "ISO3" like SGP

    private List<VATRate> rates = new ArrayList<>();



    public Country() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getISO3() { return ISO3; }
    public void setISO3(String ISO3) { this.ISO3 = ISO3; }

    public String getCode() { return Code; }
    public void setCode(String Code) { this.Code = Code; }

    public List<VATRate> getVatRates() { return rates; }
    public double getVatRates(LocalDate inputDate){
        return rates.stream()
            .filter(p -> !p.getDate().isAfter(inputDate)) // not later than date
            .max(Comparator.comparing(VATRate::getDate)) // pick the latest date
            .map(VATRate::getVATRate) //extract VAT rate to return
            .orElse(null);
    }

    public void setVatRates(List<VATRate> rates) { this.rates = rates; }

  

    public static class CustomsFees {
        private Double mpf_percent;    // e.g., 0.3464
        private Double hmf_percent;    // e.g., 0.125
        private Double mpf_min;        // min USD amount (if applicable)
        private Double mpf_max;        // max USD amount (if applicable)
        private String currency;       // e.g., "USD"

        public CustomsFees() {}

        public Double getMpf_percent() { return mpf_percent; }
        public void setMpf_percent(Double mpf_percent) { this.mpf_percent = mpf_percent; }

        public Double getHmf_percent() { return hmf_percent; }
        public void setHmf_percent(Double hmf_percent) { this.hmf_percent = hmf_percent; }

        public Double getMpf_min() { return mpf_min; }
        public void setMpf_min(Double mpf_min) { this.mpf_min = mpf_min; }

        public Double getMpf_max() { return mpf_max; }
        public void setMpf_max(Double mpf_max) { this.mpf_max = mpf_max; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public void normalize() {
            if (mpf_percent == null) mpf_percent = 0.0;
            if (hmf_percent == null) hmf_percent = 0.0;
            if (mpf_min == null) mpf_min = 0.0;
            if (mpf_max == null) mpf_max = 0.0;
        }
    }

    // public Country(String name, Integer code, String iso3, Long vatRates) {
    //     this.name = name;
    //     this.code = code;
    //     this.iso3 = iso3;
    //     this.vatRates = vatRates;

    // }

    // public String getName() { return name; }
    // public Integer getCode() { return code; }
    // public String getIso3n() { return iso3; }
    // public Long getVatRate() { return vatRates; }

    // @Override public String toString() {
    //     return "Country{name='" + name + "', code=" + code +
    //            ", iso3='" + iso3 + "', vatRates=" + vatRates + "}";
    // }
}