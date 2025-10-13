package com.cs203.grp2.Asg2.country;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@IgnoreExtraProperties
public class CountryFirebaseDTO {
    @PropertyName("Code") public Long code;          // RTDB numbers come as Long
    @PropertyName("ISO3") public String iso3;
    @PropertyName("VAT rates") public Long vatRates; // <-- keep as Long

    public CountryFirebaseDTO() {}

    public Country toDomain(String nodeKey) {
        return new Country(
            nodeKey,
            code != null ? code.intValue() : null,          // keep Integer for code as before
            iso3 != null ? iso3.trim() : null,
            vatRates                                        // pass through Long
        );
    }
}