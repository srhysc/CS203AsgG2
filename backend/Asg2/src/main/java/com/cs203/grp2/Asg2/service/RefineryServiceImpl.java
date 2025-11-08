package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.exceptions.GeneralBadRequestException;
import com.cs203.grp2.Asg2.exceptions.RefineryNotFoundException;
import com.cs203.grp2.Asg2.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.firebase.database.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class RefineryServiceImpl implements RefineryService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    private final List<Refinery> refineryList = new ArrayList<>();

    private RefineryResponseDTO toDTO(Refinery refinery) {
        List<RefineryCostResponseDTO> costDTOs = new ArrayList<>();
        if (refinery.getEstimated_costs() != null) {
            for (RefineryCost rc : refinery.getEstimated_costs()) {
                Map<String, CostDetailResponseDTO> costMap = new HashMap<>();
                if (rc.getCosts() != null) {
                    for (Map.Entry<String, CostDetail> entry : rc.getCosts().entrySet()) {
                        CostDetail cd = entry.getValue();
                        costMap.put(entry.getKey(), new CostDetailResponseDTO(cd.getCost_per_unit(), cd.getUnit()));
                    }
                }
                costDTOs.add(new RefineryCostResponseDTO(rc.getDate(), costMap));
            }
        }
        return new RefineryResponseDTO(
                refinery.getName(),
                refinery.getCompany(),
                refinery.getLocation(),
                refinery.getOperational_from(),
                refinery.getOperational_to(),
                refinery.isCan_refine_any(),
                costDTOs,
                refinery.getCountryIso3(),
                refinery.getCountryIsoNumeric(),
                refinery.getCountryName());
    }

    private void loadRefineries() throws Exception {
        DatabaseReference ref = firebaseDatabase.getReference("Refineries");
        CompletableFuture<List<Refinery>> future = new CompletableFuture<>();
        refineryList.clear();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    for (DataSnapshot countrySnap : snapshot.getChildren()) {
                        String countryName = countrySnap.getKey();
                        String countryIso3 = countrySnap.child("iso3").getValue(String.class);
                        String countryIsoNumeric = countrySnap.child("iso_numeric").getValue(String.class);
                        for (DataSnapshot refinerySnap : countrySnap.child("refineries").getChildren()) {
                            String name = refinerySnap.child("name").getValue(String.class);
                            String company = refinerySnap.child("company").getValue(String.class);
                            String location = refinerySnap.child("location").getValue(String.class);
                            Integer operational_from = refinerySnap.child("operational_from").getValue(Integer.class);
                            Integer operational_to = refinerySnap.child("operational_to").getValue(Integer.class);
                            Boolean can_refine_any = refinerySnap.child("can_refine_any").getValue(Boolean.class);

                            List<RefineryCost> estimated_costs = new ArrayList<>();
                            for (DataSnapshot costSnap : refinerySnap.child("estimated_costs").getChildren()) {
                                String dateStr = costSnap.child("date").getValue(String.class);
                                Map<String, CostDetail> costs = new HashMap<>();
                                for (DataSnapshot costTypeSnap : costSnap.child("costs").getChildren()) {
                                    String type = costTypeSnap.getKey();
                                    Double cost_per_unit = costTypeSnap.child("cost_per_unit").getValue(Double.class);
                                    String unit = costTypeSnap.child("unit").getValue(String.class);
                                    if (cost_per_unit != null && unit != null) {
                                        costs.put(type, new CostDetail(cost_per_unit, unit));
                                    }
                                }
                                estimated_costs.add(new RefineryCost(dateStr, costs));
                            }

                            refineryList.add(new Refinery(
                                    name, company, location, operational_from, operational_to,
                                    can_refine_any != null && can_refine_any, estimated_costs,
                                    countryIso3, countryIsoNumeric, countryName));
                        }
                    }
                    future.complete(refineryList);
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
    public List<RefineryResponseDTO> getAllRefineries() {
        // try {
        //     loadRefineries();
        //     List<RefineryResponseDTO> dtos = new ArrayList<>();
        //     for (Refinery refinery : refineryList) {
        //         dtos.add(toDTO(refinery));
        //     }
        //     return dtos;
        // } catch (Exception e) {
        //     return List.of();
        // }
        try {
            loadRefineries();
            List<RefineryResponseDTO> dtos = new ArrayList<>();
            for (Refinery refinery : refineryList) {
                dtos.add(toDTO(refinery));
            }
            if (dtos.isEmpty()) {
                throw new RefineryNotFoundException("No refineries found.");
            }
            return dtos;
        } catch (Exception e) {
            throw new RefineryNotFoundException("Failed to load refineries: " + e.getMessage());
        }
    }

    @Override
    public List<RefineryResponseDTO> getRefineriesByCountry(String countryIso3) {
        // try {
        //     loadRefineries();
        //     List<RefineryResponseDTO> dtos = new ArrayList<>();
        //     for (Refinery refinery : refineryList) {
        //         if (refinery.getCountryIso3() != null && refinery.getCountryIso3().equalsIgnoreCase(countryIso3)) {
        //             dtos.add(toDTO(refinery));
        //         }
        //     }
        //     return dtos;
        // } catch (Exception e) {
        //     return List.of();
        // }
        try {
            loadRefineries();
            List<RefineryResponseDTO> dtos = new ArrayList<>();
            for (Refinery refinery : refineryList) {
                if (refinery.getCountryIso3() != null && refinery.getCountryIso3().equalsIgnoreCase(countryIso3)) {
                    dtos.add(toDTO(refinery));
                }
            }
            if (dtos.isEmpty()) {
                throw new RefineryNotFoundException("No refineries found for country: " + countryIso3);
            }
            return dtos;
        } catch (Exception e) {
            throw new RefineryNotFoundException("Failed to load refineries for country: " + countryIso3);
        }
    }

    @Override
    public RefineryResponseDTO getRefinery(String countryIso3, String refineryName) {
        // try {
        //     loadRefineries();
        //     for (Refinery refinery : refineryList) {
        //         if (refinery.getName().equalsIgnoreCase(refineryName) &&
        //                 refinery.getCountryIso3() != null &&
        //                 refinery.getCountryIso3().equalsIgnoreCase(countryIso3)) {
        //             return toDTO(refinery);
        //         }
        //     }
        //     return null;
        // } catch (Exception e) {
        //     return null;
        // }
        try {
            loadRefineries();
            for (Refinery refinery : refineryList) {
                if (refinery.getName().equalsIgnoreCase(refineryName) &&
                        refinery.getCountryIso3() != null &&
                        refinery.getCountryIso3().equalsIgnoreCase(countryIso3)) {
                    return toDTO(refinery);
                }
            }
            throw new RefineryNotFoundException("Refinery not found: " + refineryName + " in " + countryIso3);
        } catch (Exception e) {
            throw new RefineryNotFoundException("Failed to load refinery: " + refineryName + " in " + countryIso3);
        }
    }

    @Override
    public List<RefineryCostResponseDTO> getAllCosts(String countryIso3, String refineryName) {
        // RefineryResponseDTO refinery = getRefinery(countryIso3, refineryName);
        // if (refinery != null) {
        //     return refinery.getEstimated_costs();
        // }
        // return List.of();
         RefineryResponseDTO refinery = getRefinery(countryIso3, refineryName);
        if (refinery != null && refinery.getEstimated_costs() != null && !refinery.getEstimated_costs().isEmpty()) {
            return refinery.getEstimated_costs();
        }
        throw new RefineryNotFoundException("No costs found for refinery: " + refineryName + " in " + countryIso3);
    }

    @Override
public List<RefineryCostResponseDTO> getLatestCost(String countryIso3, String refineryName, String dateStr) {
    // RefineryResponseDTO refinery = getRefinery(countryIso3, refineryName);
    // if (refinery != null && refinery.getEstimated_costs() != null) {
    //     if (dateStr == null || dateStr.isEmpty()) {
    //         // No date: return all historical costs
    //         return refinery.getEstimated_costs();
    //     } else {
    //         // Date provided: return only the most applicable cost
    //         RefineryCostResponseDTO best = null;
    //         for (RefineryCostResponseDTO cost : refinery.getEstimated_costs()) {
    //             if (cost.getDate() != null && cost.getDate().compareTo(dateStr) <= 0) {
    //                 if (best == null || cost.getDate().compareTo(best.getDate()) > 0) {
    //                     best = cost;
    //                 }
    //             }
    //         }
    //         return best != null ? List.of(best) : List.of();
    //     }
    // }
    // return List.of();
    RefineryResponseDTO refinery = getRefinery(countryIso3, refineryName);
        if (refinery != null && refinery.getEstimated_costs() != null) {
            if (dateStr == null || dateStr.isEmpty()) {
                if (!refinery.getEstimated_costs().isEmpty()) {
                    return refinery.getEstimated_costs();
                }
            } else {
                RefineryCostResponseDTO best = null;
                for (RefineryCostResponseDTO cost : refinery.getEstimated_costs()) {
                    if (cost.getDate() != null && cost.getDate().compareTo(dateStr) <= 0) {
                        if (best == null || cost.getDate().compareTo(best.getDate()) > 0) {
                            best = cost;
                        }
                    }
                }
                if (best != null) {
                    return List.of(best);
                }
            }
        }
        throw new RefineryNotFoundException("No latest cost found for refinery: " + refineryName + " in " + countryIso3);
}

    @Override
public CostDetailResponseDTO getCostByUnit(String countryIso3, String refineryName, String unit, String dateStr) {
    // List<RefineryCostResponseDTO> costs = getLatestCost(countryIso3, refineryName, dateStr);
    // if (costs != null && !costs.isEmpty()) {
    //     RefineryCostResponseDTO cost = costs.get(0);
    //     if (cost.getCosts() != null) {
    //         return cost.getCosts().get(unit);
    //     }
    // }
    // return null;
    if (unit == null || unit.isEmpty()) {
            throw new GeneralBadRequestException("Unit parameter is required.");
        }
        List<RefineryCostResponseDTO> costs = getLatestCost(countryIso3, refineryName, dateStr);
        if (costs != null && !costs.isEmpty()) {
            RefineryCostResponseDTO cost = costs.get(0);
            if (cost.getCosts() != null && cost.getCosts().get(unit) != null) {
                return cost.getCosts().get(unit);
            }
        }
        throw new RefineryNotFoundException("No cost found for unit: " + unit + " in refinery: " + refineryName + " (" + countryIso3 + ")");
}

    @Override
    public RefineryResponseDTO addOrUpdateRefinery(String countryIso3, RefineryRequestDTO refineryRequestDTO) {
    //     DatabaseReference countryRef = firebaseDatabase.getReference("Refineries").child(countryIso3);

    //     CompletableFuture<DataSnapshot> countryFuture = new CompletableFuture<>();
    //     countryRef.addListenerForSingleValueEvent(new ValueEventListener() {
    //         @Override
    //         public void onDataChange(DataSnapshot snapshot) {
    //             countryFuture.complete(snapshot);
    //         }

    //         @Override
    //         public void onCancelled(DatabaseError error) {
    //             countryFuture.completeExceptionally(error.toException());
    //         }
    //     });

    //     try {
    //         DataSnapshot countrySnap = countryFuture.get();
    //         DatabaseReference refineriesRef = countryRef.child("refineries");

    //         boolean refineryExists = false;
    //         String refineryKey = null;
    //         for (DataSnapshot refinerySnap : countrySnap.child("refineries").getChildren()) {
    //             String name = refinerySnap.child("name").getValue(String.class);
    //             if (name != null && name.equalsIgnoreCase(refineryRequestDTO.getName())) {
    //                 refineryExists = true;
    //                 refineryKey = refinerySnap.getKey();
    //                 break;
    //             }
    //         }

    //         if (refineryExists) {
    //             DatabaseReference estimatedCostsRef = refineriesRef.child(refineryKey).child("estimated_costs");
    //             estimatedCostsRef.push().setValueAsync(refineryRequestDTO.getEstimated_costs().get(0));
    //         } else {
    //             Map<String, Object> refineryMap = new HashMap<>();
    //             refineryMap.put("name", refineryRequestDTO.getName());
    //             refineryMap.put("company", refineryRequestDTO.getCompany());
    //             refineryMap.put("location", refineryRequestDTO.getLocation());
    //             refineryMap.put("operational_from", refineryRequestDTO.getOperational_from());
    //             refineryMap.put("operational_to", refineryRequestDTO.getOperational_to());
    //             refineryMap.put("can_refine_any", refineryRequestDTO.isCan_refine_any());
    //             refineryMap.put("estimated_costs", refineryRequestDTO.getEstimated_costs());

    //             refineriesRef.push().setValueAsync(refineryMap);
    //         }

    //         if (!countrySnap.exists()) {
    //             countryRef.child("iso3").setValueAsync(countryIso3);
    //             countryRef.child("iso_numeric").setValueAsync(refineryRequestDTO.getCountryIsoNumeric());
    //         }

    //         // Build response DTO from request
    //         List<RefineryCostResponseDTO> costResponses = new ArrayList<>();
    //         if (refineryRequestDTO.getEstimated_costs() != null) {
    //             for (RefineryCostRequestDTO req : refineryRequestDTO.getEstimated_costs()) {
    //                 Map<String, CostDetailResponseDTO> costMap = new HashMap<>();
    //                 if (req.getCosts() != null) {
    //                     for (Map.Entry<String, CostDetailRequestDTO> entry : req.getCosts().entrySet()) {
    //                         CostDetailRequestDTO cdReq = entry.getValue();
    //                         costMap.put(entry.getKey(),
    //                                 new CostDetailResponseDTO(cdReq.getCost_per_unit(), cdReq.getUnit()));
    //                     }
    //                 }
    //                 costResponses.add(new RefineryCostResponseDTO(req.getDate(), costMap));
    //             }
    //         }

    //         return new RefineryResponseDTO(
    //                 refineryRequestDTO.getName(),
    //                 refineryRequestDTO.getCompany(),
    //                 refineryRequestDTO.getLocation(),
    //                 refineryRequestDTO.getOperational_from(),
    //                 refineryRequestDTO.getOperational_to(),
    //                 refineryRequestDTO.isCan_refine_any(),
    //                 costResponses,
    //                 countryIso3,
    //                 refineryRequestDTO.getCountryIsoNumeric(),
    //                 refineryRequestDTO.getCountryName());
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
    DatabaseReference countryRef = firebaseDatabase.getReference("Refineries").child(countryIso3);

        CompletableFuture<DataSnapshot> countryFuture = new CompletableFuture<>();
        countryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                countryFuture.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                countryFuture.completeExceptionally(error.toException());
            }
        });

        try {
            DataSnapshot countrySnap = countryFuture.get();
            DatabaseReference refineriesRef = countryRef.child("refineries");

            boolean refineryExists = false;
            String refineryKey = null;
            for (DataSnapshot refinerySnap : countrySnap.child("refineries").getChildren()) {
                String name = refinerySnap.child("name").getValue(String.class);
                if (name != null && name.equalsIgnoreCase(refineryRequestDTO.getName())) {
                    refineryExists = true;
                    refineryKey = refinerySnap.getKey();
                    break;
                }
            }

            if (refineryExists) {
                DatabaseReference estimatedCostsRef = refineriesRef.child(refineryKey).child("estimated_costs");
                estimatedCostsRef.push().setValueAsync(refineryRequestDTO.getEstimated_costs().get(0));
            } else {
                Map<String, Object> refineryMap = new HashMap<>();
                refineryMap.put("name", refineryRequestDTO.getName());
                refineryMap.put("company", refineryRequestDTO.getCompany());
                refineryMap.put("location", refineryRequestDTO.getLocation());
                refineryMap.put("operational_from", refineryRequestDTO.getOperational_from());
                refineryMap.put("operational_to", refineryRequestDTO.getOperational_to());
                refineryMap.put("can_refine_any", refineryRequestDTO.isCan_refine_any());
                refineryMap.put("estimated_costs", refineryRequestDTO.getEstimated_costs());

                refineriesRef.push().setValueAsync(refineryMap);
            }

            if (!countrySnap.exists()) {
                countryRef.child("iso3").setValueAsync(countryIso3);
                countryRef.child("iso_numeric").setValueAsync(refineryRequestDTO.getCountryIsoNumeric());
            }

            // Build response DTO from request
            List<RefineryCostResponseDTO> costResponses = new ArrayList<>();
            if (refineryRequestDTO.getEstimated_costs() != null) {
                for (RefineryCostRequestDTO req : refineryRequestDTO.getEstimated_costs()) {
                    Map<String, CostDetailResponseDTO> costMap = new HashMap<>();
                    if (req.getCosts() != null) {
                        for (Map.Entry<String, CostDetailRequestDTO> entry : req.getCosts().entrySet()) {
                            CostDetailRequestDTO cdReq = entry.getValue();
                            costMap.put(entry.getKey(),
                                    new CostDetailResponseDTO(cdReq.getCost_per_unit(), cdReq.getUnit()));
                        }
                    }
                    costResponses.add(new RefineryCostResponseDTO(req.getDate(), costMap));
                }
            }

            return new RefineryResponseDTO(
                    refineryRequestDTO.getName(),
                    refineryRequestDTO.getCompany(),
                    refineryRequestDTO.getLocation(),
                    refineryRequestDTO.getOperational_from(),
                    refineryRequestDTO.getOperational_to(),
                    refineryRequestDTO.isCan_refine_any(),
                    costResponses,
                    countryIso3,
                    refineryRequestDTO.getCountryIsoNumeric(),
                    refineryRequestDTO.getCountryName());
        } catch (Exception e) {
            throw new RefineryNotFoundException("Failed to add or update refinery: " + e.getMessage());
        }
    }
}