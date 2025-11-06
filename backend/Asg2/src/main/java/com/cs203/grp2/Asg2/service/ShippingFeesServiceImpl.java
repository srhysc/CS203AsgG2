package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.firebase.database.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ShippingFeesServiceImpl implements ShippingFeesService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    public final List<ShippingFee> shippingFeeList = new ArrayList<>();

    // Helper: Convert Model to DTO
    private ShippingFeeResponseDTO toDTO(ShippingFee fee) {
        List<ShippingFeeEntryResponseDTO> entryDTOs = new ArrayList<>();
        for (ShippingFeeEntry entry : fee.getShippingFees()) {
            Map<String, ShippingCostDetailResponseDTO> costMap = new HashMap<>();
            for (Map.Entry<String, ShippingCostDetail> cost : entry.getCosts().entrySet()) {
                costMap.put(cost.getKey(), new ShippingCostDetailResponseDTO(cost.getValue().getCostPerUnit(), cost.getValue().getUnit()));
            }
            entryDTOs.add(new ShippingFeeEntryResponseDTO(entry.getDate(), costMap));
        }
        ShippingFeeResponseDTO dto = new ShippingFeeResponseDTO();
        dto.setCountry1Name(fee.getCountry1().getName());
        dto.setCountry2Name(fee.getCountry2().getName());
        dto.setCountry1Iso3(fee.getCountry1().getISO3());
        dto.setCountry2Iso3(fee.getCountry2().getISO3());
        dto.setCountry1IsoNumeric(fee.getCountry1().getCode());
        dto.setCountry2IsoNumeric(fee.getCountry2().getCode());
        dto.setShippingFees(entryDTOs);
        return dto;
    }

    // Helper: Load all shipping fees from Firebase
    private void loadShippingFees() throws Exception {
        DatabaseReference ref = firebaseDatabase.getReference("shipping_fees");
        CompletableFuture<List<ShippingFee>> future = new CompletableFuture<>();
        shippingFeeList.clear();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    for (DataSnapshot feeSnap : snapshot.getChildren()) {
                        Country country1 = new Country();
                        country1.setName(feeSnap.child("country1").child("name").getValue(String.class));
                        country1.setISO3(feeSnap.child("country1").child("ISO3").getValue(String.class));
                        country1.setCode(feeSnap.child("country1").child("Code").getValue(String.class));

                        Country country2 = new Country();
                        country2.setName(feeSnap.child("country2").child("name").getValue(String.class));
                        country2.setISO3(feeSnap.child("country2").child("ISO3").getValue(String.class));
                        country2.setCode(feeSnap.child("country2").child("Code").getValue(String.class));

                        List<ShippingFeeEntry> entries = new ArrayList<>();
                        for (DataSnapshot entrySnap : feeSnap.child("shipping_fees").getChildren()) {
                            String dateStr = entrySnap.child("date").getValue(String.class);
                            LocalDate date = (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : null;
                            Map<String, ShippingCostDetail> costs = new HashMap<>();
                            for (DataSnapshot costSnap : entrySnap.getChildren()) {
                                if (costSnap.getKey().equals("date")) continue;
                                Double costPerUnit = costSnap.child("cost_per_unit").getValue(Double.class);
                                String unit = costSnap.child("unit").getValue(String.class);
                                if (costPerUnit != null && unit != null) {
                                    costs.put(costSnap.getKey(), new ShippingCostDetail(costPerUnit, unit));
                                }
                            }
                            entries.add(new ShippingFeeEntry(date, costs));
                        }
                        shippingFeeList.add(new ShippingFee(country1, country2, entries));
                    }
                    future.complete(shippingFeeList);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        future.get();
    }

    @Override
    public List<ShippingFeeResponseDTO> getAllShippingFees() {
        try {
            loadShippingFees();
            List<ShippingFeeResponseDTO> dtos = new ArrayList<>();
            for (ShippingFee fee : shippingFeeList) {
                dtos.add(toDTO(fee));
            }
            return dtos;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public ShippingFeeResponseDTO getShippingFees(String country1Iso3, String country2Iso3) {
        try {
            loadShippingFees();
            for (ShippingFee fee : shippingFeeList) {
                boolean match =
                    (fee.getCountry1().getISO3().equalsIgnoreCase(country1Iso3) && fee.getCountry2().getISO3().equalsIgnoreCase(country2Iso3)) ||
                    (fee.getCountry1().getISO3().equalsIgnoreCase(country2Iso3) && fee.getCountry2().getISO3().equalsIgnoreCase(country1Iso3));
                if (match) {
                    return toDTO(fee);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ShippingFeeEntryResponseDTO> getAllCosts(String country1Iso3, String country2Iso3) {
        ShippingFeeResponseDTO fee = getShippingFees(country1Iso3, country2Iso3);
        if (fee != null) {
            return fee.getShippingFees();
        }
        return List.of();
    }

    @Override
    public List<ShippingFeeEntryResponseDTO> getAllCostsByUnit(String country1Iso3, String country2Iso3, String unit) {
        ShippingFeeResponseDTO fee = getShippingFees(country1Iso3, country2Iso3);
        if (fee != null && fee.getShippingFees() != null) {
            List<ShippingFeeEntryResponseDTO> filteredEntries = new ArrayList<>();
            for (ShippingFeeEntryResponseDTO entry : fee.getShippingFees()) {
                if (entry.getCosts() != null && entry.getCosts().containsKey(unit)) {
                    // Create a new entry with only the requested unit
                    Map<String, ShippingCostDetailResponseDTO> filteredCosts = new HashMap<>();
                    filteredCosts.put(unit, entry.getCosts().get(unit));
                    ShippingFeeEntryResponseDTO filteredEntry = new ShippingFeeEntryResponseDTO(
                        entry.getDate(),
                        filteredCosts
                    );
                    filteredEntries.add(filteredEntry);
                }
            }
            return filteredEntries;
        }
        return List.of();
    }

    @Override
    public ShippingFeeEntryResponseDTO getLatestCost(String country1Iso3, String country2Iso3, LocalDate date) {
        ShippingFeeResponseDTO fee = getShippingFees(country1Iso3, country2Iso3);
        if (fee != null && fee.getShippingFees() != null) {
            ShippingFeeEntryResponseDTO best = null;
            for (ShippingFeeEntryResponseDTO entry : fee.getShippingFees()) {
                if (entry.getDate() != null && !entry.getDate().isAfter(date)) {
                    if (best == null || entry.getDate().isAfter(best.getDate())) {
                        best = entry;
                    }
                }
            }
            return best;
        }
        return null;
    }

    @Override
    public ShippingCostDetailResponseDTO getCostByUnit(String country1Iso3, String country2Iso3, String unit, LocalDate date) {
        ShippingFeeEntryResponseDTO entry = getLatestCost(country1Iso3, country2Iso3, date);
        if (entry != null && entry.getCosts() != null) {
            return entry.getCosts().get(unit);
        }
        return null;
    }

    /**
     * Adds or updates shipping fee entries.
     * Business Logic:
     * 1. If country pair exists -> Add new price entry to existing country pair
     * 2. If country pair does NOT exist -> Create new country pair with the provided entries
     */
    @Override
    public ShippingFeeResponseDTO addOrUpdateShippingFee(ShippingFeeRequestDTO requestDTO) {
        DatabaseReference ref = firebaseDatabase.getReference("shipping_fees");
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        try {
            DataSnapshot snap = future.get();
            String key = null;
            
            // Search for existing country pair (bidirectional match)
            for (DataSnapshot feeSnap : snap.getChildren()) {
                String c1 = feeSnap.child("country1").child("ISO3").getValue(String.class);
                String c2 = feeSnap.child("country2").child("ISO3").getValue(String.class);
                if (
                    c1 != null && c2 != null &&
                    ((c1.equalsIgnoreCase(requestDTO.getCountry1Iso3()) && c2.equalsIgnoreCase(requestDTO.getCountry2Iso3())) ||
                     (c1.equalsIgnoreCase(requestDTO.getCountry2Iso3()) && c2.equalsIgnoreCase(requestDTO.getCountry1Iso3())))
                ) {
                    key = feeSnap.getKey();
                    break;
                }
            }

            if (key != null) {
                // CASE 1: Country pair EXISTS -> Add new price entries only
                DatabaseReference entriesRef = ref.child(key).child("shipping_fees");
                for (ShippingFeeEntryRequestDTO entryReq : requestDTO.getShippingFees()) {
                    Map<String, Object> entryMap = new HashMap<>();
                    entryMap.put("date", entryReq.getDate().toString());
                    for (Map.Entry<String, ShippingCostDetailRequestDTO> cost : entryReq.getCosts().entrySet()) {
                        Map<String, Object> costMap = new HashMap<>();
                        costMap.put("cost_per_unit", cost.getValue().getCostPerUnit());
                        costMap.put("unit", cost.getValue().getUnit());
                        entryMap.put(cost.getKey(), costMap);
                    }
                    entriesRef.push().setValueAsync(entryMap);
                }
            } else {
                // CASE 2: Country pair does NOT exist -> Create new country pair
                Map<String, Object> feeMap = new HashMap<>();
                
                // Add country 1 details
                Map<String, Object> country1Map = new HashMap<>();
                country1Map.put("name", requestDTO.getCountry1Name());
                country1Map.put("ISO3", requestDTO.getCountry1Iso3());
                country1Map.put("Code", requestDTO.getCountry1IsoNumeric());
                
                // Add country 2 details
                Map<String, Object> country2Map = new HashMap<>();
                country2Map.put("name", requestDTO.getCountry2Name());
                country2Map.put("ISO3", requestDTO.getCountry2Iso3());
                country2Map.put("Code", requestDTO.getCountry2IsoNumeric());
                
                feeMap.put("country1", country1Map);
                feeMap.put("country2", country2Map);

                // Add shipping fee entries
                Map<String, Object> entriesMap = new HashMap<>();
                for (ShippingFeeEntryRequestDTO entryReq : requestDTO.getShippingFees()) {
                    Map<String, Object> entryMap = new HashMap<>();
                    entryMap.put("date", entryReq.getDate().toString());
                    for (Map.Entry<String, ShippingCostDetailRequestDTO> cost : entryReq.getCosts().entrySet()) {
                        Map<String, Object> costMap = new HashMap<>();
                        costMap.put("cost_per_unit", cost.getValue().getCostPerUnit());
                        costMap.put("unit", cost.getValue().getUnit());
                        entryMap.put(cost.getKey(), costMap);
                    }
                    // Use push() to generate unique keys for each entry
                    DatabaseReference newEntryRef = ref.push();
                    entriesMap.put(newEntryRef.getKey(), entryMap);
                }
                feeMap.put("shipping_fees", entriesMap);
                ref.push().setValueAsync(feeMap);
            }

            // Wait a moment for Firebase to persist, then reload and return
            Thread.sleep(500);
            return getShippingFees(requestDTO.getCountry1Iso3(), requestDTO.getCountry2Iso3());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}