package com.cs203.grp2.Asg2.DTO;

import java.util.List;

public class RouteOptimizationResponse {
    private List<RouteBreakdown> topRoutes;

    private double petroleumPrice;

    public RouteOptimizationResponse(List<RouteBreakdown> topRoutes, double petroleumPrice) {
        this.topRoutes = topRoutes;
        this.petroleumPrice = petroleumPrice;
    }

    public List<RouteBreakdown> getTopRoutes() { return topRoutes; }
    public double getPetroleumPrice() {return petroleumPrice;}
}
