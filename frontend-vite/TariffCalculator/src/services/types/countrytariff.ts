//Defining tariff interface based on response format
export interface Tariff{
    importingCountry: string;
    exportingCountry: string;
    petroleumName: string;
    hsCode: string;
    pricePerUnit: number;
    basePrice: number;
    tariffRate: number;
    tariffFees: number;
    vatRate: number;
    vatFees: number;
    totalLandedCost: number;
    currency: string;
    shippingCost: number;
    alternativeRoutes: Record<string, RouteBreakdown>;
}
//no need for API wrapper because just returning those two values

export interface RouteBreakdown {
    transitCountry: string;
    baseCost: number;
    tariffFees: number;
    vatRate: number;
    vatFees: number;
    totalLandedCost: number;
    shippingCost: number;
}