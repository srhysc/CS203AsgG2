package com.cs203.grp2.Asg2.shippingFees;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ShippingFeesService {

    private final Map<ShippingRouteKey, Double> feesMap = new HashMap<>();

    public ShippingFeesService() {
        // Hardcoded fees by (importing, exporting) country pair
        feesMap.put(new ShippingRouteKey("France", "Germany"), 5.0);
        feesMap.put(new ShippingRouteKey("Germany", "France"), 7.5);
        feesMap.put(new ShippingRouteKey("France", "Italy"), 6.0);
        feesMap.put(new ShippingRouteKey("Italy", "France"), 6.5);
        feesMap.put(new ShippingRouteKey("USA", "Canada"), 10.0);
        feesMap.put(new ShippingRouteKey("Canada", "USA"), 9.0);
    }

    public ShippingFees getFee(String importingCountry, String exportingCountry) {
        Double fee = feesMap.get(new ShippingRouteKey(importingCountry, exportingCountry));
        if (fee == null) {
            return null;
        }
        return new ShippingFees(fee, importingCountry, exportingCountry);
    }

    public void addShippingFee(ShippingFees shippingFees) {
        feesMap.put(new ShippingRouteKey(shippingFees.getImportingCountry(), shippingFees.getExportingCountry()),
                shippingFees.getFee());
    }

    private static class ShippingRouteKey {
        private final String importingCountry;
        private final String exportingCountry;

        public ShippingRouteKey(String importingCountry, String exportingCountry) {
            this.importingCountry = importingCountry.toLowerCase();
            this.exportingCountry = exportingCountry.toLowerCase();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShippingRouteKey)) return false;
            ShippingRouteKey that = (ShippingRouteKey) o;
            return importingCountry.equals(that.importingCountry) &&
                    exportingCountry.equals(that.exportingCountry);
        }

        @Override
        public int hashCode() {
            return Objects.hash(importingCountry, exportingCountry);
        }
    }
}
