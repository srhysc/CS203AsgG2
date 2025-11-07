export interface RouteBreakdown {
    exportingCountry: string;
    transitCountry?: string; // optional in case there's no transit
    importingCountry: string;
  
    baseCost: number;
    tariffFees: number;
    vatFees: number; 
    totalLandedCost: number;
    vatRate: number;
}

export interface RouteOptimizationResponse {
    topRoutes: RouteBreakdown[];
    petroleumPrice: number;
}