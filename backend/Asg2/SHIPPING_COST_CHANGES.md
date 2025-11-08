# Shipping Cost Query Feature - Implementation Summary

## Overview
Enhanced shipping cost query functionality to support flexible filtering by date and unit parameters.

## Business Requirements
1. **No date, no unit**: Return all historical shipping costs for country pair
2. **No date, with unit**: Return historical shipping costs filtered by specific unit
3. **With date, with unit**: Return single most applicable entry for specific unit at/before given date
4. **With date, no unit**: Return single most applicable entry (all units) at/before given date
5. **POST with existing pair**: Add new price entry to existing country combination
6. **POST with new pair**: Create new country pair with provided entries

## Modified Files

### 1. ShippingFeesController.java
**Endpoint**: `GET /{country1Iso3}/{country2Iso3}/cost`

**Query Parameters**:
- `date` (optional): LocalDate in format YYYY-MM-DD
- `unit` (optional): String - unit type (e.g., "barrel", "ton")

**Logic Flow**:
```java
// Case 1: No date, no unit → all historical costs
if ((date == null || date.isEmpty()) && (unit == null || unit.isEmpty()))
    return getAllCosts(country1Iso3, country2Iso3);

// Case 2: No date, with unit → historical costs for specific unit
if (date == null || date.isEmpty())
    return getAllCostsByUnit(country1Iso3, country2Iso3, unit);

// Case 3: Date + unit → single entry for specific unit
if (unit != null && !unit.isEmpty())
    return getCostByUnit(country1Iso3, country2Iso3, unit, date);

// Case 4: Date, no unit → single entry with all units
return getLatestCost(country1Iso3, country2Iso3, date);
```

### 2. ShippingFeesService.java (Interface)
**Added Method**:
```java
List<ShippingFeeEntryResponseDTO> getAllCostsByUnit(
    String country1Iso3, 
    String country2Iso3, 
    String unit
);
```

### 3. ShippingFeesServiceImpl.java
**Implemented getAllCostsByUnit()**:
- Loads shipping fee data from Firebase
- Filters all historical entries to include only those with specified unit
- Returns list of `ShippingFeeEntryResponseDTO` with filtered costs map
- Maintains bidirectional country matching (c1↔c2 or c2↔c1)

**Implementation Details**:
```java
// Iterate through all entries
for (ShippingFeeEntryResponseDTO entry : fee.getShippingFees()) {
    if (entry.getCosts().containsKey(unit)) {
        // Create filtered response with only requested unit
        Map<String, ShippingCostDetailResponseDTO> filteredCosts = new HashMap<>();
        filteredCosts.put(unit, entry.getCosts().get(unit));
        filteredEntries.add(new ShippingFeeEntryResponseDTO(
            entry.getDate(), 
            filteredCosts
        ));
    }
}
```

**Enhanced addOrUpdateShippingFee()**:
- Added comprehensive documentation
- **Case 1**: Country pair exists → Add new price entries to existing pair
- **Case 2**: Country pair doesn't exist → Create new country pair with entries
- Bidirectional country pair search (c1↔c2 matching)
- Proper Firebase structure mapping

## API Examples

### Example 1: Get All Historical Costs
```http
GET /api/shipping-fees/USA/CHN/cost
```
Returns all shipping entries for USA↔CHN with all units.

### Example 2: Get Historical Costs for Specific Unit
```http
GET /api/shipping-fees/USA/CHN/cost?unit=barrel
```
Returns all shipping entries that have "barrel" unit pricing.

### Example 3: Get Single Entry for Unit at Date
```http
GET /api/shipping-fees/USA/CHN/cost?date=2024-01-15&unit=barrel
```
Returns the most applicable entry at/before 2024-01-15 with only "barrel" pricing.

### Example 4: Get Single Entry (All Units) at Date
```http
GET /api/shipping-fees/USA/CHN/cost?date=2024-01-15
```
Returns the most applicable entry at/before 2024-01-15 with all available units.

### Example 5: Add New Entry to Existing Pair
```http
POST /api/shipping-fees
Content-Type: application/json

{
  "country1Iso3": "USA",
  "country2Iso3": "CHN",
  "shippingFees": [{
    "date": "2024-02-01",
    "costs": {
      "barrel": {"costPerUnit": 55.0, "unit": "barrel"},
      "ton": {"costPerUnit": 450.0, "unit": "ton"}
    }
  }]
}
```

## Data Structure (Firebase)
```
shipping_fees/
  ├─ {key}/
  │   ├─ country1: {name, ISO3, Code}
  │   ├─ country2: {name, ISO3, Code}
  │   └─ shipping_fees/
  │       ├─ {entry_key}/
  │       │   ├─ date: "2024-01-01"
  │       │   ├─ barrel: {cost_per_unit, unit}
  │       │   └─ ton: {cost_per_unit, unit}
  │       └─ ...
  └─ ...
```

## Build Status
✅ **Compilation Successful** (`mvn clean compile`)
- 50 source files compiled
- No compilation errors
- Build time: ~4 seconds

## Testing Recommendations
1. Test all 4 GET query parameter combinations
2. Test bidirectional country matching (USA→CHN vs CHN→USA)
3. Test POST for existing vs new country pairs
4. Test edge cases:
   - Non-existent country pair
   - Invalid date format
   - Unit that doesn't exist in entries
   - Empty date/unit parameters

## Next Steps
- [ ] Integration testing with Firebase
- [ ] Manual API testing using Postman/curl
- [ ] Add validation for date format
- [ ] Add validation for unit values
- [ ] Consider adding pagination for large historical datasets
- [ ] Add proper error handling for edge cases
