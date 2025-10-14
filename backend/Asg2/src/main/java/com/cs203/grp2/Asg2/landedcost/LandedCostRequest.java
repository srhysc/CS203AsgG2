package com.cs203.grp2.Asg2.landedcost;

import jakarta.validation.constraints.*;

public class LandedCostRequest {
  
    private String importerCode;
    
    
    private String exporterCode;

    private String importerName;
    private String exporterName;

    @Pattern(regexp="\\d{6}", message="HS Code must be 6 digits")
    private String hsCode;

    @Min(1)
    private int units;

    public LandedCostRequest() {}

    // getters and setters
    public String getImporterCode() { return importerCode; }
    public void setImporterCode(String importerIso3n) { this.importerCode = importerIso3n; }

    public String getExporterCode() { return exporterCode; }
    public void setExporterCode(String exporterIso3n) { this.exporterCode = exporterIso3n; }

    public String getImporterName() { return importerName; }
    public void setImporterName(String importerName) { this.importerName = importerName; }

    public String getExporterName() { return exporterName; }
    public void setExporterName(String exporterName) { this.exporterName = exporterName; }

    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
}
