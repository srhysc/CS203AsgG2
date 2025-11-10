package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.firebase.database.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ShippingFeesServiceImpl implements ShippingFeesService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;
        private static final Logger logger = LoggerFactory.getLogger(ShippingFeesServiceImpl.class);

    // Only allow these units
    private static final Set<String> ALLOWED_UNITS = Set.of("barrel", "ton", "MMBtu");

    private final List<ShippingFee> shippingFeeList = new ArrayList<>();

    private ShippingFeeResponseDTO toDTO(ShippingFee fee) {
        List<ShippingFeeEntryResponseDTO> entryDTOs = new ArrayList<>();
        for (ShippingFeeEntry entry : fee.getShippingFees()) {
            Map<String, ShippingCostDetailResponseDTO> costMap = new HashMap<>();
            for (Map.Entry<String, ShippingCostDetail> cost : entry.getCosts().entrySet()) {
                if (ALLOWED_UNITS.contains(cost.getKey())) {
                    costMap.put(cost.getKey(),
                        new ShippingCostDetailResponseDTO(cost.getValue().getCostPerUnit(), cost.getValue().getUnit()));
                }
            }
            entryDTOs.add(new ShippingFeeEntryResponseDTO(entry.getDate(), costMap));
        }
        ShippingFeeResponseDTO dto = new ShippingFeeResponseDTO();
        dto.setCountry1Name(fee.getCountry1Name());
        dto.setCountry2Name(fee.getCountry2Name());
        dto.setCountry1Iso3(fee.getCountry1Iso3());
        dto.setCountry2Iso3(fee.getCountry2Iso3());
        dto.setCountry1IsoNumeric(fee.getCountry1IsoNumeric());
        dto.setCountry2IsoNumeric(fee.getCountry2IsoNumeric());
        dto.setShippingFees(entryDTOs);
        return dto;
    }

    private void loadShippingFees() throws Exception {
        DatabaseReference ref = firebaseDatabase.getReference("Shipping_cost");
        CompletableFuture<List<ShippingFee>> future = new CompletableFuture<>();
        shippingFeeList.clear();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    for (DataSnapshot feeSnap : snapshot.getChildren()) {
                        String country1Name = feeSnap.child("country1").child("name").getValue(String.class);
                        String country1Iso3 = feeSnap.child("country1").child("iso3").getValue(String.class);
                        String country1IsoNumeric = feeSnap.child("country1").child("iso_numeric").getValue(String.class);

                        String country2Name = feeSnap.child("country2").child("name").getValue(String.class);
                        String country2Iso3 = feeSnap.child("country2").child("iso3").getValue(String.class);
                        String country2IsoNumeric = feeSnap.child("country2").child("iso_numeric").getValue(String.class);

                        List<ShippingFeeEntry> entries = new ArrayList<>();
                        for (DataSnapshot entrySnap : feeSnap.child("shipping_fees").getChildren()) {
                            String dateStr = entrySnap.child("date").getValue(String.class);
                            LocalDate date = (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : null;
                            Map<String, ShippingCostDetail> costs = new HashMap<>();
                            for (DataSnapshot costSnap : entrySnap.getChildren()) {
                                String unitKey = costSnap.getKey();
                                if ("date".equals(unitKey)) continue;
                                if (!ALLOWED_UNITS.contains(unitKey)) continue;

                                // READ cost_per_unit robustly: Firebase can store as Long/Double/String
                                Object rawCost = costSnap.child("cost_per_unit").getValue();
                                Double costPerUnit = null;
                                if (rawCost instanceof Number) {
                                    costPerUnit = ((Number) rawCost).doubleValue();
                                } else if (rawCost instanceof String) {
                                    try {
                                        costPerUnit = Double.parseDouble((String) rawCost);
                                    } catch (NumberFormatException ignored) { }
                                }

                                String unit = costSnap.child("unit").getValue(String.class);
                                if (costPerUnit != null && unit != null) {
                                    costs.put(unitKey, new ShippingCostDetail(costPerUnit, unit));
                                }
                            }
                            entries.add(new ShippingFeeEntry(date, costs));
                        }
                        shippingFeeList.add(new ShippingFee(
                            country1Name, country1Iso3, country1IsoNumeric,
                            country2Name, country2Iso3, country2IsoNumeric,
                            entries
                        ));
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
                String iso1 = fee.getCountry1Iso3();
                String iso2 = fee.getCountry2Iso3();
                if (iso1 == null || iso2 == null) continue;
                boolean match = (iso1.equalsIgnoreCase(country1Iso3) && iso2.equalsIgnoreCase(country2Iso3)) ||
                                (iso1.equalsIgnoreCase(country2Iso3) && iso2.equalsIgnoreCase(country1Iso3));
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
        if (!ALLOWED_UNITS.contains(unit)) return List.of();
        ShippingFeeResponseDTO fee = getShippingFees(country1Iso3, country2Iso3);
        if (fee != null && fee.getShippingFees() != null) {
            List<ShippingFeeEntryResponseDTO> filteredEntries = new ArrayList<>();
            for (ShippingFeeEntryResponseDTO entry : fee.getShippingFees()) {
                if (entry.getCosts() != null && entry.getCosts().containsKey(unit)) {
                    Map<String, ShippingCostDetailResponseDTO> filteredCosts = new HashMap<>();
                    filteredCosts.put(unit, entry.getCosts().get(unit));
                    filteredEntries.add(new ShippingFeeEntryResponseDTO(entry.getDate(), filteredCosts));
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
        if (!ALLOWED_UNITS.contains(unit)) return null;
        ShippingFeeEntryResponseDTO entry = getLatestCost(country1Iso3, country2Iso3, date);
        if (entry != null && entry.getCosts() != null) {
            return entry.getCosts().get(unit);
        }
        return null;
    }

    // // @Override
    // public ShippingFeeResponseDTO addOrUpdateShippingFee(ShippingFeeRequestDTO requestDTO) {
    //     DatabaseReference ref = firebaseDatabase.getReference("Shipping_cost");
    //     CompletableFuture<String> future = new CompletableFuture<>();

    //     try {
    //         // Search for existing entry
    //         ref.orderByChild("country1/iso3").equalTo(requestDTO.getCountry1Iso3())
    //            .addListenerForSingleValueEvent(new ValueEventListener() {
    //             @Override
    //             public void onDataChange(DataSnapshot snapshot) {
    //                 String existingKey = null;
    //                 for (DataSnapshot feeSnap : snapshot.getChildren()) {
    //                     String c2 = feeSnap.child("country2/iso3").getValue(String.class);
    //                     if (c2 != null && c2.equalsIgnoreCase(requestDTO.getCountry2Iso3())) {
    //                         existingKey = feeSnap.getKey();
    //                         break;
    //                     }
    //                 }
    //                 future.complete(existingKey);
    //             }

    //             @Override
    //             public void onCancelled(DatabaseError error) {
    //                 future.completeExceptionally(error.toException());
    //             }
    //         });

    //         String key = future.get();
    //         // Map<String, Object> updates = new HashMap<>();

    //         if (key != null) {
    //             // Update existing entry
    //             // String newEntryKey = ref.child(key).child("shipping_fees").push().getKey();
    //             // for (ShippingFeeEntryRequestDTO entryReq : requestDTO.getShippingFees()) {
    //             //     Map<String, Object> entryMap = new HashMap<>();
    //             //     entryMap.put("date", LocalDate.now().toString());
                    
    //             //     for (Map.Entry<String, ShippingCostDetailRequestDTO> cost : entryReq.getCosts().entrySet()) {
    //             //         if (ALLOWED_UNITS.contains(cost.getKey())) {
    //             //             Map<String, Object> costMap = new HashMap<>();
    //             //             costMap.put("cost_per_unit", cost.getValue().getCostPerUnit());
    //             //             costMap.put("unit", cost.getValue().getUnit());
    //             //             entryMap.put(cost.getKey(), costMap);
    //             //         }
    //             //     }
                    
    //             //     updates.put("/Shipping_cost/" + key + "/shipping_fees/" + newEntryKey, entryMap);
    //             // }
    //              DatabaseReference shippingFeesRef = ref.child(key).child("shipping_fees");
    //         CompletableFuture<DataSnapshot> arrayFuture = new CompletableFuture<>();
            
    //         shippingFeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
    //             @Override
    //             public void onDataChange(DataSnapshot snapshot) {
    //                 arrayFuture.complete(snapshot);
    //             }
                
    //             @Override
    //             public void onCancelled(DatabaseError error) {
    //                 arrayFuture.completeExceptionally(error.toException());
    //             }
    //         });
            
    //         DataSnapshot currentArray = arrayFuture.get();
    //         List<Map<String, Object>> entriesList = new ArrayList<>();
            
    //         // Copy existing entries
    //         for (DataSnapshot entrySnap : currentArray.getChildren()) {
    //             Map<String, Object> existingEntry = new HashMap<>();
    //             existingEntry.put("date", entrySnap.child("date").getValue(String.class));
                
    //             for (String unit : ALLOWED_UNITS) {
    //                 if (entrySnap.hasChild(unit)) {
    //                     Map<String, Object> costMap = new HashMap<>();
    //                     costMap.put("cost_per_unit", entrySnap.child(unit).child("cost_per_unit").getValue(Double.class));
    //                     costMap.put("unit", entrySnap.child(unit).child("unit").getValue(String.class));
    //                     existingEntry.put(unit, costMap);
    //                 }
    //             }
    //             entriesList.add(existingEntry);
    //         }
            
    //         // Add new entry from request
    //         for (ShippingFeeEntryRequestDTO entryReq : requestDTO.getShippingFees()) {
    //             Map<String, Object> newEntry = new HashMap<>();
    //             newEntry.put("date", LocalDate.now().toString());
                
    //             for (Map.Entry<String, ShippingCostDetailRequestDTO> cost : entryReq.getCosts().entrySet()) {
    //                 if (ALLOWED_UNITS.contains(cost.getKey())) {
    //                     Map<String, Object> costMap = new HashMap<>();
    //                     costMap.put("cost_per_unit", cost.getValue().getCostPerUnit());
    //                     costMap.put("unit", cost.getValue().getUnit());
    //                     newEntry.put(cost.getKey(), costMap);
    //                 }
    //             }
    //             entriesList.add(newEntry);
    //         }
            
    //         // Replace entire array
    //         shippingFeesRef.setValueAsync(entriesList).get();
    //         } else {
    //             // Create new entry
    //             key = ref.push().getKey();
    //             Map<String, Object> feeMap = new HashMap<>();
                
    //             Map<String, Object> country1Map = new HashMap<>();
    //             country1Map.put("name", requestDTO.getCountry1Name());
    //             country1Map.put("iso3", requestDTO.getCountry1Iso3());
                
    //             Map<String, Object> country2Map = new HashMap<>();
    //             country2Map.put("name", requestDTO.getCountry2Name());
    //             country2Map.put("iso3", requestDTO.getCountry2Iso3());
                
    //             feeMap.put("country1", country1Map);
    //             feeMap.put("country2", country2Map);
                
    //             // String entryKey = ref.child(key).child("shipping_fees").push().getKey();
    //             // Map<String, Object> shippingFees = new HashMap<>();

    //             List<Map<String, Object>> shippingFeesList = new ArrayList<>();
                
    //             for (ShippingFeeEntryRequestDTO entryReq : requestDTO.getShippingFees()) {
    //             Map<String, Object> entryMap = new HashMap<>();
    //             entryMap.put("date", LocalDate.now().toString());
                
    //             for (Map.Entry<String, ShippingCostDetailRequestDTO> cost : entryReq.getCosts().entrySet()) {
    //                 if (ALLOWED_UNITS.contains(cost.getKey())) {
    //                     Map<String, Object> costMap = new HashMap<>();
    //                     costMap.put("cost_per_unit", cost.getValue().getCostPerUnit());
    //                     costMap.put("unit", cost.getValue().getUnit());
    //                     entryMap.put(cost.getKey(), costMap);
    //                 }
    //             }
    //             shippingFeesList.add(entryMap);
    //         }
                
    //             // feeMap.put("shipping_fees", shippingFees);
    //             // updates.put("/Shipping_cost/" + key, feeMap);
    //             feeMap.put("shipping_fees", shippingFeesList);
    //             ref.child(key).setValueAsync(feeMap).get();
    //         }
            
    //         // Wait for Firebase to complete the update
    //         Thread.sleep(1000);
            
    //         return getShippingFees(requestDTO.getCountry1Iso3(), requestDTO.getCountry2Iso3());
    //     } catch (Exception e) {
    //         logger.error("Error updating shipping fee: ", e);
    //         return null;
    //     }
    // }
    @Override
    public ShippingFeeResponseDTO addOrUpdateShippingFee(ShippingFeeRequestDTO requestDTO) {
        DatabaseReference ref = firebaseDatabase.getReference("Shipping_cost");
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
                String c1 = feeSnap.child("country1").child("iso3").getValue(String.class);
                String c2 = feeSnap.child("country2").child("iso3").getValue(String.class);
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
                DatabaseReference entriesRef = ref.child(key).child("shipping_fees");
                for (ShippingFeeEntryRequestDTO entryReq : requestDTO.getShippingFees()) {
                    Map<String, Object> entryMap = new HashMap<>();
                    entryMap.put("date", entryReq.getDate());
                    for (Map.Entry<String, ShippingCostDetailRequestDTO> cost : entryReq.getCosts().entrySet()) {
                        if (!ALLOWED_UNITS.contains(cost.getKey())) continue;
                        Map<String, Object> costMap = new HashMap<>();
                        costMap.put("cost_per_unit", cost.getValue().getCostPerUnit());
                        costMap.put("unit", cost.getValue().getUnit());
                        entryMap.put(cost.getKey(), costMap);
                    }
                    entriesRef.push().setValueAsync(entryMap);
                }
            } else {
                Map<String, Object> feeMap = new HashMap<>();

                Map<String, Object> country1Map = new HashMap<>();
                country1Map.put("name", requestDTO.getCountry1Name());
                country1Map.put("iso3", requestDTO.getCountry1Iso3());
                country1Map.put("iso_numeric", requestDTO.getCountry1IsoNumeric());

                Map<String, Object> country2Map = new HashMap<>();
                country2Map.put("name", requestDTO.getCountry2Name());
                country2Map.put("iso3", requestDTO.getCountry2Iso3());
                country2Map.put("iso_numeric", requestDTO.getCountry2IsoNumeric());

                feeMap.put("country1", country1Map);
                feeMap.put("country2", country2Map);

                Map<String, Object> entriesMap = new HashMap<>();
                for (ShippingFeeEntryRequestDTO entryReq : requestDTO.getShippingFees()) {
                    Map<String, Object> entryMap = new HashMap<>();
                    entryMap.put("date", entryReq.getDate());
                    for (Map.Entry<String, ShippingCostDetailRequestDTO> cost : entryReq.getCosts().entrySet()) {
                        if (!ALLOWED_UNITS.contains(cost.getKey())) continue;
                        Map<String, Object> costMap = new HashMap<>();
                        costMap.put("cost_per_unit", cost.getValue().getCostPerUnit());
                        costMap.put("unit", cost.getValue().getUnit());
                        entryMap.put(cost.getKey(), costMap);
                    }
                    String entryKey = UUID.randomUUID().toString();
                    entriesMap.put(entryKey, entryMap);
                }
                feeMap.put("shipping_fees", entriesMap);
                ref.push().setValueAsync(feeMap);
            }

            Thread.sleep(500);
            return getShippingFees(requestDTO.getCountry1Iso3(), requestDTO.getCountry2Iso3());
        } catch (Exception e) {
            return null;
        }
    }
}