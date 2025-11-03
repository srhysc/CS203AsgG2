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
public class RefineryServiceImpl implements RefineryService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    private final List<Refinery> refineryList = new ArrayList<>();

    // Helper: Convert Model to DTO
    private RefineryResponseDTO toDTO(Refinery refinery) {
        List<RefineryCostResponseDTO> costDTOs = new ArrayList<>();
        if (refinery.getEstimatedCosts() != null) {
            for (RefineryCost rc : refinery.getEstimatedCosts()) {
                Map<String, CostDetailResponseDTO> costMap = new HashMap<>();
                if (rc.getCosts() != null) {
                    for (Map.Entry<String, CostDetail> entry : rc.getCosts().entrySet()) {
                        CostDetail cd = entry.getValue();
                        costMap.put(entry.getKey(), new CostDetailResponseDTO(cd.getCostPerUnit(), cd.getUnit()));
                    }
                }
                costDTOs.add(new RefineryCostResponseDTO(rc.getDate(), costMap));
            }
        }
        return new RefineryResponseDTO(
            refinery.getName(),
            refinery.getCompany(),
            refinery.getLocation(),
            refinery.getOperationalFrom(),
            refinery.getOperationalTo(),
            refinery.isCanRefineAny(),
            costDTOs,
            refinery.getCountryIso3(),
            refinery.getCountryIsoNumeric()
        );
    }

    // Helper: Load all refineries from Firebase (once per app run)
    private void loadRefineries() throws Exception {
        DatabaseReference ref = firebaseDatabase.getReference("refineries");
        CompletableFuture<List<Refinery>> future = new CompletableFuture<>();
        refineryList.clear();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    for (DataSnapshot countrySnap : snapshot.getChildren()) {
                        String countryIso3 = countrySnap.child("iso3").getValue(String.class);
                        String countryIsoNumeric = countrySnap.child("iso_numeric").getValue(String.class);
                        for (DataSnapshot refinerySnap : countrySnap.child("refineries").getChildren()) {
                            String name = refinerySnap.child("name").getValue(String.class);
                            String company = refinerySnap.child("company").getValue(String.class);
                            String location = refinerySnap.child("location").getValue(String.class);
                            Integer operationalFrom = refinerySnap.child("operational_from").getValue(Integer.class);
                            Integer operationalTo = refinerySnap.child("operational_to").getValue(Integer.class);
                            Boolean canRefineAny = refinerySnap.child("can_refine_any").getValue(Boolean.class);

                            List<RefineryCost> estimatedCosts = new ArrayList<>();
                            for (DataSnapshot costSnap : refinerySnap.child("estimated_costs").getChildren()) {
                                String dateStr = costSnap.child("date").getValue(String.class);
                                LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : null;
                                Map<String, CostDetail> costs = new HashMap<>();
                                for (DataSnapshot costTypeSnap : costSnap.child("costs").getChildren()) {
                                    String type = costTypeSnap.getKey();
                                    Double costPerUnit = costTypeSnap.child("cost_per_unit").getValue(Double.class);
                                    String unit = costTypeSnap.child("unit").getValue(String.class);
                                    if (costPerUnit != null && unit != null) {
                                        costs.put(type, new CostDetail(costPerUnit, unit));
                                    }
                                }
                                estimatedCosts.add(new RefineryCost(date, costs));
                            }

                            refineryList.add(new Refinery(
                                name, company, location, operationalFrom, operationalTo,
                                canRefineAny != null && canRefineAny, estimatedCosts,
                                countryIso3, countryIsoNumeric
                            ));
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
        try {
            loadRefineries();
            List<RefineryResponseDTO> dtos = new ArrayList<>();
            for (Refinery refinery : refineryList) {
                dtos.add(toDTO(refinery));
            }
            return dtos;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<RefineryResponseDTO> getRefineriesByCountry(String countryIso3) {
        try {
            loadRefineries();
            List<RefineryResponseDTO> dtos = new ArrayList<>();
            for (Refinery refinery : refineryList) {
                if (refinery.getCountryIso3() != null && refinery.getCountryIso3().equalsIgnoreCase(countryIso3)) {
                    dtos.add(toDTO(refinery));
                }
            }
            return dtos;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public RefineryResponseDTO getRefinery(String countryIso3, String refineryName) {
        try {
            loadRefineries();
            for (Refinery refinery : refineryList) {
                if (refinery.getName().equalsIgnoreCase(refineryName) &&
                    refinery.getCountryIso3() != null &&
                    refinery.getCountryIso3().equalsIgnoreCase(countryIso3)) {
                    return toDTO(refinery);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<RefineryCostResponseDTO> getAllCosts(String countryIso3, String refineryName) {
        RefineryResponseDTO refinery = getRefinery(countryIso3, refineryName);
        if (refinery != null) {
            return refinery.getEstimatedCosts();
        }
        return List.of();
    }

    @Override
    public RefineryCostResponseDTO getLatestCost(String countryIso3, String refineryName, LocalDate date) {
        RefineryResponseDTO refinery = getRefinery(countryIso3, refineryName);
        if (refinery != null && refinery.getEstimatedCosts() != null) {
            RefineryCostResponseDTO best = null;
            for (RefineryCostResponseDTO cost : refinery.getEstimatedCosts()) {
                if (cost.getDate() != null && !cost.getDate().isAfter(date)) {
                    if (best == null || cost.getDate().isAfter(best.getDate())) {
                        best = cost;
                    }
                }
            }
            return best;
        }
        return null;
    }

        @Override
    public CostDetailResponseDTO getCostByUnit(String countryIso3, String refineryName, String unit, LocalDate date) {
        RefineryResponseDTO refinery = getRefinery(countryIso3, refineryName);
        if (refinery != null && refinery.getEstimatedCosts() != null) {
            RefineryCostResponseDTO best = null;
            if (date != null) {
                for (RefineryCostResponseDTO cost : refinery.getEstimatedCosts()) {
                    if (cost.getDate() != null && !cost.getDate().isAfter(date)) {
                        if (best == null || cost.getDate().isAfter(best.getDate())) {
                            best = cost;
                        }
                    }
                }
            } else {
                // If no date, get the latest cost
                for (RefineryCostResponseDTO cost : refinery.getEstimatedCosts()) {
                    if (best == null || (cost.getDate() != null && cost.getDate().isAfter(best.getDate()))) {
                        best = cost;
                    }
                }
            }
            if (best != null && best.getCosts() != null) {
                return best.getCosts().get(unit);
            }
        }
        return null;
    }

    @Override
public RefineryResponseDTO addOrUpdateRefinery(String countryIso3, RefineryRequestDTO refineryRequestDTO) {
    DatabaseReference countryRef = firebaseDatabase.getReference("refineries").child(countryIso3);

    // Check if country exists
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

        // Check if refinery exists
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
            // Add new estimated cost entry to existing refinery
            DatabaseReference estimatedCostsRef = refineriesRef.child(refineryKey).child("estimated_costs");
            estimatedCostsRef.push().setValueAsync(refineryRequestDTO.getEstimatedCosts().get(0));
        } else {
            // Add new refinery entry
            Map<String, Object> refineryMap = new HashMap<>();
            refineryMap.put("name", refineryRequestDTO.getName());
            refineryMap.put("company", refineryRequestDTO.getCompany());
            refineryMap.put("location", refineryRequestDTO.getLocation());
            refineryMap.put("operational_from", refineryRequestDTO.getOperationalFrom());
            refineryMap.put("operational_to", refineryRequestDTO.getOperationalTo());
            refineryMap.put("can_refine_any", refineryRequestDTO.isCanRefineAny());
            refineryMap.put("estimated_costs", refineryRequestDTO.getEstimatedCosts());

            refineriesRef.push().setValueAsync(refineryMap);
        }

        // If country does not exist, add country node
        if (!countrySnap.exists()) {
            countryRef.child("iso3").setValueAsync(countryIso3);
            countryRef.child("iso_numeric").setValueAsync(refineryRequestDTO.getCountryIsoNumeric());
        }

        // Return the DTO (optionally reload from DB)
        return new RefineryResponseDTO(
            refineryRequestDTO.getName(),
            refineryRequestDTO.getCompany(),
            refineryRequestDTO.getLocation(),
            refineryRequestDTO.getOperationalFrom(),
            refineryRequestDTO.getOperationalTo(),
            refineryRequestDTO.isCanRefineAny(),
            refineryRequestDTO.getEstimatedCosts(), // or null
            countryIso3,
            refineryRequestDTO.getCountryIsoNumeric()
        );
    } catch (Exception e) {
        // Handle error
        return null;
    }
}
}