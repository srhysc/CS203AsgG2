package com.cs203.grp2.Asg2.DTO;

import java.util.List;

public class RouteOptimizationResponse {
    private List<RouteBreakdown> topRoutes;

    public RouteOptimizationResponse(List<RouteBreakdown> topRoutes) {
        this.topRoutes = topRoutes;
    }

    public List<RouteBreakdown> getTopRoutes() { return topRoutes; }

    
}
