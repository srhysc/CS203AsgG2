package com.cs203.grp2.Asg2.country;



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
    private final String name;     // node key, e.g. "Albania"
    private final Integer code;    // "Code"
    private final String iso3;     // "ISO3"
    private final Long vatRates;   // <-- now Long

    public Country(String name, Integer code, String iso3, Long vatRates) {
        this.name = name;
        this.code = code;
        this.iso3 = iso3;
        this.vatRates = vatRates;
    }

    public String getName() { return name; }
    public Integer getCode() { return code; }
    public String getIso3n() { return iso3; }
    public Long getVatRate() { return vatRates; }

    @Override public String toString() {
        return "Country{name='" + name + "', code=" + code +
               ", iso3='" + iso3 + "', vatRates=" + vatRates + "}";
    }
}