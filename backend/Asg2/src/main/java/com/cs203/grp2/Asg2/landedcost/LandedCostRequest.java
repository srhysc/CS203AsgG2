package com.cs203.grp2.Asg2.landedcost;

public class LandedCostRequest {
    private int importerIso3n;   // numeric ISO code
    private int exporterIso3n;   // numeric ISO code
    private String hsCode;
    private int units;

    public LandedCostRequest() {}

    public int getImporterIso3n() { return importerIso3n; }
    public void setImporterIso3n(int importerIso3n) { this.importerIso3n = importerIso3n; }

    public int getExporterIso3n() { return exporterIso3n; }
    public void setExporterIso3n(int exporterIso3n) { this.exporterIso3n = exporterIso3n; }

    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
}
