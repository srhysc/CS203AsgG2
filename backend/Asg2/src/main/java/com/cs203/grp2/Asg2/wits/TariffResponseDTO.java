package com.cs203.grp2.Asg2.wits;

public record TariffResponseDTO(
        double ratePercent,    // normalized ad-valorem %, e.g., 5.0 means 5%
        String basis,          // "preferential", "mfn", "wits", "none"
        String sourceNote      // short human-readable note on where it came from
) {
    public static TariffResponseDTO none() {
        return new TariffResponseDTO(0.0, "none", "No rate found in DB or WITS");
    }
}