package com.cs203.grp2.Asg2.DTO;

import jakarta.validation.constraints.*;

public class RouteOptimizationRequest {

    @Min(1)
    private Integer exporterIso3n;

    private String exporterName;

    @Min(1)
    private Integer importerIso3n;

    private String importerName;

    @Pattern(regexp = "\\d{6}", message = "HS Code must be 6 digits")
    private String hsCode;

    @Min(1)
    private int units;

    @Min(0)
    @Max(2)
    private int maxTransits; // 0, 1, or 2 transit countries

    public RouteOptimizationRequest() {}

    // Exporter
    public Integer getExporterIso3n() { return exporterIso3n; }
    public void setExporterIso3n(Integer exporterIso3n) { this.exporterIso3n = exporterIso3n; }

    public String getExporterName() { return exporterName; }
    public void setExporterName(String exporterName) { this.exporterName = exporterName; }

    // Importer
    public Integer getImporterIso3n() { return importerIso3n; }
    public void setImporterIso3n(Integer importerIso3n) { this.importerIso3n = importerIso3n; }

    public String getImporterName() { return importerName; }
    public void setImporterName(String importerName) { this.importerName = importerName; }

    // HS Code
    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    // Units
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }

    // Max Transits
    public int getMaxTransits() { return maxTransits; }
    public void setMaxTransits(int maxTransits) { this.maxTransits = maxTransits; }
}
