package com.cs203.grp2.Asg2.models;

import java.util.List;

public class RouteOption {
    private String exporter;
    private List<String> transitCountries;
    private String importer;
    private double totalCost;

    public RouteOption(String exporter, List<String> transitCountries, String importer, double totalCost) {
        this.exporter = exporter;
        this.transitCountries = transitCountries;
        this.importer = importer;
        this.totalCost = totalCost;
    }

    public String getExporter() { return exporter; }
    public List<String> getTransitCountries() { return transitCountries; }
    public String getImporter() { return importer; }
    public double getTotalCost() { return totalCost; }
}
