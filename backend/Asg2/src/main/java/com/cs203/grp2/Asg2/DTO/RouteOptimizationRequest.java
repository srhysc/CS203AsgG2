package com.cs203.grp2.Asg2.DTO;

import jakarta.validation.constraints.*;
import com.cs203.grp2.Asg2.models.Country;


public class RouteOptimizationRequest {

    private Country exporter;
    private Country importer;

    @Pattern(regexp = "\\d{6}", message = "HS Code must be 6 digits")
    private String hsCode;

    @Min(1)
    private int units;

    public RouteOptimizationRequest() {}

    // Exporter
    public Country getExportingCountry(){return exporter;}
    public Country getImportingCountry(){return importer;}

    public void setExporter(Country exporter) { this.exporter = exporter; }
    public void setImporter(Country importer) { this.importer = importer; }

    // HS Code
    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    // Units
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
}
