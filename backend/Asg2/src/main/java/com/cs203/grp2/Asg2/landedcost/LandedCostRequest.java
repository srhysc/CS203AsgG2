package com.cs203.grp2.Asg2.landedcost;

import jakarta.validation.constraints.*;

public class LandedCostRequest {
    @Min(1)
    private Integer importerIso3n;
    
    @Min(1)
    private Integer exporterIso3n;

    private String importerName;
    private String exporterName;

    @Pattern(regexp="\\d{6}", message="HS Code must be 6 digits")
    private String hsCode;

    @Min(1)
    private int units;

    public LandedCostRequest() {}

    // getters and setters
    public Integer getImporterIso3n() { return importerIso3n; }
    public void setImporterIso3n(Integer importerIso3n) { this.importerIso3n = importerIso3n; }

    public Integer getExporterIso3n() { return exporterIso3n; }
    public void setExporterIso3n(Integer exporterIso3n) { this.exporterIso3n = exporterIso3n; }

    public String getImporterName() { return importerName; }
    public void setImporterName(String importerName) { this.importerName = importerName; }

    public String getExporterName() { return exporterName; }
    public void setExporterName(String exporterName) { this.exporterName = exporterName; }

    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
}
