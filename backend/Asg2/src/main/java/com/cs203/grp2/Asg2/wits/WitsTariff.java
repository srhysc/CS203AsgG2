package com.cs203.grp2.Asg2.wits;

import java.time.LocalDate;

/** Simple value object you can also persist if you later add a repository. */
public class WitsTariff {
    private final String importerIso3;
    private final String exporterIso3;
    private final String hs6;
    private final LocalDate date;
    private final double ratePercent;  // 0.0 if unknown
    private final String basis;        // "preferential" | "mfn" | "wits" | "none"
    private final String sourceNote;

    public WitsTariff(String importerIso3, String exporterIso3, String hs6,
                      LocalDate date, double ratePercent, String basis, String sourceNote) {
        this.importerIso3 = importerIso3;
        this.exporterIso3 = exporterIso3;
        this.hs6 = hs6;
        this.date = date;
        this.ratePercent = ratePercent;
        this.basis = basis;
        this.sourceNote = sourceNote;
    }

    public double ratePercent() { return ratePercent; }
    public String basis() { return basis; }
    public String sourceNote() { return sourceNote; }
    public String importerIso3() { return importerIso3; }
    public String exporterIso3() { return exporterIso3; }
    public String hs6() { return hs6; }
    public LocalDate date() { return date; }
}