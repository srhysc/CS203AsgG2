package com.cs203.grp2.Asg2.country;

import java.util.List;

import com.google.firebase.database.PropertyName;

//import jakarta.persistence.*;


//@Table(name = "country_view") // mapping to SQL View
public class Country {

    // @Id
    // private Integer iso3n;   // use Integer consistently

    // private String name;

    // @Column(name = "vatRate")
    // private Double vatRate;

    // required by JPA
    //protected Country() {}

    // Parameterized constructor
    private String name;     // node key, e.g. "Albania"
    @PropertyName("Code")
    private String Code;    // "Code" like 702
    @PropertyName("ISO3")
    private String ISO3;     // "ISO3" like SGP

    @PropertyName("VAT rates")
    private Long vatRates;   // <-- now Long

    private Hs27Taxes hs27_taxes;

    public Country() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @PropertyName("ISO3")
    public String getISO3() { return ISO3; }
    @PropertyName("ISO3")
    public void setISO3(String ISO3) { this.ISO3 = ISO3; }

    @PropertyName("Code")
    public String getCode() { return Code; }
    @PropertyName("Code")
    public void setCode(String Code) { this.Code = Code; }

    @PropertyName("VAT rates")
    public Long getVatRates() { return vatRates; }
    @PropertyName("VAT rates")
    public void setVatRates(Long vatRates) { this.vatRates = vatRates; }

    public Hs27Taxes getHs27_taxes() { return hs27_taxes; }
    public void setHs27_taxes(Hs27Taxes hs27_taxes) { this.hs27_taxes = hs27_taxes; }

    // --- convenience: normalize nulls to 0 / 0.0 ---
    public void normalize() {
        if (vatRates == null) vatRates = 0L;
        if (hs27_taxes == null) hs27_taxes = new Hs27Taxes();
        hs27_taxes.normalize();
    }

    // ---------- nested classes mirror your /Country/{Name}/hs27_taxes ----------
    public static class Hs27Taxes {
        @PropertyName("vat_gst_percent")
        private Double vat_gst_percent;            // integer-like (0.0 if absent)
        private Double excise_specific_per_liter;  // per-litre amount
        private String excise_currency;            // e.g., "GBP","AUD","USD"
        @PropertyName("customs_fees")
        private CustomsFees customs_fees;          // MPF/HMF etc.
        @PropertyName("carbon_tax_per_tCO2e")
        private Double carbon_tax_per_tCO2e;       // usually 0.0 in your DB now
        @PropertyName("carbon_tax_currency")
        private String carbon_tax_currency;

        private List<String> notes;                // optional
        private List<String> sources;              // optional

        public Hs27Taxes() {}

        public Double getVat_gst_percent() { return vat_gst_percent; }
        public void setVat_gst_percent(Double v) { this.vat_gst_percent = v; }

        public Double getExcise_specific_per_liter() { return excise_specific_per_liter; }
        public void setExcise_specific_per_liter(Double v) { this.excise_specific_per_liter = v; }

        public String getExcise_currency() { return excise_currency; }
        public void setExcise_currency(String excise_currency) { this.excise_currency = excise_currency; }

        public CustomsFees getCustoms_fees() { return customs_fees; }
        public void setCustoms_fees(CustomsFees customs_fees) { this.customs_fees = customs_fees; }

        public Double getCarbon_tax_per_tCO2e() { return carbon_tax_per_tCO2e; }
        public void setCarbon_tax_per_tCO2e(Double v) { this.carbon_tax_per_tCO2e = v; }

        public String getCarbon_tax_currency() { return carbon_tax_currency; }
        public void setCarbon_tax_currency(String carbon_tax_currency) { this.carbon_tax_currency = carbon_tax_currency; }

        public List<String> getNotes() { return notes; }
        public void setNotes(List<String> notes) { this.notes = notes; }

        public List<String> getSources() { return sources; }
        public void setSources(List<String> sources) { this.sources = sources; }

        public void normalize() {
            if (vat_gst_percent == null) vat_gst_percent = 0.0;
            if (excise_specific_per_liter == null) excise_specific_per_liter = 0.0;
            if (customs_fees == null) customs_fees = new CustomsFees();
            customs_fees.normalize();
            if (carbon_tax_per_tCO2e == null) carbon_tax_per_tCO2e = 0.0;
            // currencies can stay null; callers should handle display/assumptions
        }
    }

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