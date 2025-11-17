package com.cs203.grp2.Asg2.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203.grp2.Asg2.DTO.ShippingCostDetailRequestDTO;
import com.cs203.grp2.Asg2.DTO.ShippingCostDetailResponseDTO;
import com.cs203.grp2.Asg2.DTO.ShippingFeeEntryRequestDTO;
import com.cs203.grp2.Asg2.DTO.ShippingFeeEntryResponseDTO;
import com.cs203.grp2.Asg2.DTO.ShippingFeeRequestDTO;
import com.cs203.grp2.Asg2.DTO.ShippingFeeResponseDTO;
import com.cs203.grp2.Asg2.models.ShippingCostDetail;
import com.cs203.grp2.Asg2.models.ShippingFee;
import com.cs203.grp2.Asg2.models.ShippingFeeEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@Service
public class ShippingFeesServiceImpl implements ShippingFeesService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    private static final Logger logger = LoggerFactory.getLogger(ShippingFeesServiceImpl.class);
    private static final Set<String> ALLOWED_UNITS = Set.of("barrel", "ton","mmbtu");

    private final List<ShippingFee> shippingFeeList = new ArrayList<>();
    private final Map<String, ShippingFeeResponseDTO> overrideCache = new ConcurrentHashMap<>();

    @Override
    public List<ShippingFeeResponseDTO> getAllShippingFees() {
        try {
            loadShippingFees();
            List<ShippingFeeResponseDTO> dtos = new ArrayList<>();
            synchronized (shippingFeeList) {
                for (ShippingFee fee : shippingFeeList) {
                    dtos.add(toDTO(fee));
                }
            }
            return dtos;
        } catch (Exception e) {
            logger.error("Failed to load shipping fees", e);
            return List.of();
        }
    }

    @Override
    public ShippingFeeResponseDTO getShippingFees(String country1Iso3, String country2Iso3) {
        try {
            String routeKey = buildRouteKey(country1Iso3, country2Iso3);
            ShippingFeeResponseDTO override = overrideCache.get(routeKey);
            if (override != null) {
                return deepCopy(override);
            }
            return findShippingFee(country1Iso3, country2Iso3);
        } catch (Exception e) {
            logger.error("Failed to fetch shipping fee pair", e);
            return null;
        }
    }

    @Override
    public List<ShippingFeeEntryResponseDTO> getAllCosts(String country1Iso3, String country2Iso3) {
        ShippingFeeResponseDTO fee = getShippingFees(country1Iso3, country2Iso3);
        if (fee == null || fee.getShippingFees() == null) {
            return List.of();
        }
        return fee.getShippingFees();
    }

    @Override
    public List<ShippingFeeEntryResponseDTO> getAllCostsByUnit(String country1Iso3, String country2Iso3, String unit) {
        String normalizedUnit = normalizeUnit(unit);
        if (!ALLOWED_UNITS.contains(normalizedUnit)) {
            return List.of();
        }

        ShippingFeeResponseDTO fee = getShippingFees(country1Iso3, country2Iso3);
        if (fee == null || fee.getShippingFees() == null) {
            return List.of();
        }

        List<ShippingFeeEntryResponseDTO> filtered = new ArrayList<>();
        for (ShippingFeeEntryResponseDTO entry : fee.getShippingFees()) {
            if (entry.getCosts() != null && entry.getCosts().containsKey(normalizedUnit)) {
                Map<String, ShippingCostDetailResponseDTO> costs = Map.of(
                        normalizedUnit, entry.getCosts().get(normalizedUnit));
                filtered.add(new ShippingFeeEntryResponseDTO(entry.getDate(), costs));
            }
        }
        return filtered;
    }

    @Override
    public ShippingFeeEntryResponseDTO getLatestCost(String country1Iso3, String country2Iso3, LocalDate date) {
        ShippingFeeResponseDTO fee = getShippingFees(country1Iso3, country2Iso3);
        if (fee == null || fee.getShippingFees() == null) {
            return null;
        }

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

    @Override
    public ShippingCostDetailResponseDTO getCostByUnit(String country1Iso3, String country2Iso3, String unit,
                                                       LocalDate date) {
        String normalizedUnit = normalizeUnit(unit);
        if (!ALLOWED_UNITS.contains(normalizedUnit)) {
            return null;
        }
        ShippingFeeEntryResponseDTO entry = getLatestCost(country1Iso3, country2Iso3, date);
        if (entry == null || entry.getCosts() == null) {
            return null;
        }
        return entry.getCosts().get(normalizedUnit);
    }

    @Override
    public ShippingFeeResponseDTO addOrUpdateShippingFee(ShippingFeeRequestDTO requestDTO) {
        validateRequest(requestDTO);
        List<ShippingFeeEntryResponseDTO> sanitizedEntries = sanitizeEntries(requestDTO.getShippingFees());

        try {
            DatabaseReference ref = firebaseDatabase.getReference("Shipping_cost");
            DataSnapshot snapshot = fetchSnapshot(ref);

            String key = findExistingKey(snapshot, requestDTO.getCountry1Iso3(), requestDTO.getCountry2Iso3());

            if (key != null) {
                // Update existing
                DatabaseReference entriesRef = ref.child(key).child("shipping_fees");
                for (ShippingFeeEntryResponseDTO entry : sanitizedEntries) {
                    Map<String, Object> entryMap = buildEntryMap(entry);
                    entriesRef.push().setValueAsync(entryMap).get();
                }
            } else {
                String newKey = ref.push().getKey();
                Map<String, Object> feeMap = buildFeeMap(requestDTO, sanitizedEntries);
                ref.child(newKey).setValueAsync(feeMap).get();
            }

            return findShippingFee(requestDTO.getCountry1Iso3(), requestDTO.getCountry2Iso3());

        } catch (Exception e) {
            logger.error("Error writing shipping fee", e);
            return null;
        }
    }

    private void validateRequest(ShippingFeeRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Shipping fee request body cannot be null.");
        }
        if (isBlank(requestDTO.getCountry1Iso3()) || isBlank(requestDTO.getCountry2Iso3())) {
            throw new IllegalArgumentException("Both origin and destination ISO3 codes are required.");
        }
        if (isBlank(requestDTO.getCountry1Name()) || isBlank(requestDTO.getCountry2Name())) {
            throw new IllegalArgumentException("Both origin and destination country names are required.");
        }
        if (requestDTO.getShippingFees() == null || requestDTO.getShippingFees().isEmpty()) {
            requestDTO.setShippingFees(new ArrayList<>());
        }
    }

    private String findExistingKey(DataSnapshot snapshot, String iso1, String iso2) {
        if (snapshot == null || !snapshot.exists()) return null;
        String reqC1 = iso1.trim().toUpperCase();
        String reqC2 = iso2.trim().toUpperCase();

        for (DataSnapshot feeSnap : snapshot.getChildren()) {
            String c1 = feeSnap.child("country1").child("iso3").getValue(String.class);
            String c2 = feeSnap.child("country2").child("iso3").getValue(String.class);
            if (c1 != null) c1 = c1.trim().toUpperCase();
            if (c2 != null) c2 = c2.trim().toUpperCase();

            if (c1 != null && c2 != null &&
                ((c1.equals(reqC1) && c2.equals(reqC2)) || (c1.equals(reqC2) && c2.equals(reqC1)))) {
                return feeSnap.getKey();
            }
        }
        return null;
    }

    private Map<String, Object> buildEntryMap(ShippingFeeEntryResponseDTO entry) {
        Map<String, Object> entryMap = new HashMap<>();
        entryMap.put("date", entry.getDate().toString());
        if (entry.getCosts() != null) {
            for (Map.Entry<String, ShippingCostDetailResponseDTO> cost : entry.getCosts().entrySet()) {
                Map<String, Object> costMap = new HashMap<>();
                costMap.put("cost_per_unit", cost.getValue().getCostPerUnit());
                costMap.put("unit", cost.getValue().getUnit());
                entryMap.put(cost.getKey(), costMap);
            }
        }
        return entryMap;
    }

    private Map<String, Object> buildFeeMap(ShippingFeeRequestDTO requestDTO,
                                        List<ShippingFeeEntryResponseDTO> entries) {
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
        for (ShippingFeeEntryResponseDTO entry : entries) {
            entriesMap.put(UUID.randomUUID().toString(), buildEntryMap(entry));
        }
        feeMap.put("shipping_fees", entriesMap);

        return feeMap;
    }

    private void loadShippingFees() throws Exception {
        DatabaseReference ref = firebaseDatabase.getReference("Shipping_cost");
        DataSnapshot snapshot = fetchSnapshot(ref);

        synchronized (shippingFeeList) {
            shippingFeeList.clear();
            if (snapshot == null || !snapshot.exists()) {
                return;
            }

            for (DataSnapshot feeSnap : snapshot.getChildren()) {
                String country1Name = trimToNull(feeSnap.child("country1").child("name").getValue(String.class));
                String country1Iso3 = trimToNull(feeSnap.child("country1").child("iso3").getValue(String.class));
                String country1IsoNumeric = trimToNull(
                        feeSnap.child("country1").child("iso_numeric").getValue(String.class));

                String country2Name = trimToNull(feeSnap.child("country2").child("name").getValue(String.class));
                String country2Iso3 = trimToNull(feeSnap.child("country2").child("iso3").getValue(String.class));
                String country2IsoNumeric = trimToNull(
                        feeSnap.child("country2").child("iso_numeric").getValue(String.class));

                if (!isValidIso(country1Iso3) || !isValidIso(country2Iso3)) {
                    continue;
                }

                List<ShippingFeeEntry> entries = extractEntries(feeSnap.child("shipping_fees"));
                if (entries.isEmpty()) {
                    continue;
                }

                shippingFeeList.add(new ShippingFee(
                        country1Name, country1Iso3, country1IsoNumeric,
                        country2Name, country2Iso3, country2IsoNumeric,
                        entries));
            }
        }
    }

    private ShippingFeeResponseDTO findShippingFee(String country1Iso3, String country2Iso3) throws Exception {
        loadShippingFees();
        synchronized (shippingFeeList) {
            for (ShippingFee fee : shippingFeeList) {
                String iso1 = fee.getCountry1Iso3();
                String iso2 = fee.getCountry2Iso3();
                if (iso1 == null || iso2 == null) continue;
                boolean match = (iso1.equalsIgnoreCase(country1Iso3) && iso2.equalsIgnoreCase(country2Iso3))
                        || (iso1.equalsIgnoreCase(country2Iso3) && iso2.equalsIgnoreCase(country1Iso3));
                if (match) {
                    return toDTO(fee);
                }
            }
        }
        return null;
    }

    private ShippingFeeResponseDTO toDTO(ShippingFee fee) {
        List<ShippingFeeEntryResponseDTO> entryDTOs = new ArrayList<>();
        for (ShippingFeeEntry entry : fee.getShippingFees()) {
            Map<String, ShippingCostDetailResponseDTO> costMap = new HashMap<>();
            for (Map.Entry<String, ShippingCostDetail> cost : entry.getCosts().entrySet()) {
                String normalizedUnit = normalizeUnit(cost.getKey());
                if (!ALLOWED_UNITS.contains(normalizedUnit)) {
                    continue;
                }
                costMap.put(normalizedUnit, new ShippingCostDetailResponseDTO(
                        cost.getValue().getCostPerUnit(),
                        cost.getValue().getUnit()));
            }
            entryDTOs.add(new ShippingFeeEntryResponseDTO(entry.getDate(), costMap));
        }

        ShippingFeeResponseDTO dto = new ShippingFeeResponseDTO();
        dto.setCountry1Name(fee.getCountry1Name());
        dto.setCountry1Iso3(fee.getCountry1Iso3());
        dto.setCountry1IsoNumeric(fee.getCountry1IsoNumeric());
        dto.setCountry2Name(fee.getCountry2Name());
        dto.setCountry2Iso3(fee.getCountry2Iso3());
        dto.setCountry2IsoNumeric(fee.getCountry2IsoNumeric());
        dto.setShippingFees(entryDTOs);
        return dto;
    }

    private List<ShippingFeeEntry> extractEntries(DataSnapshot feesSnapshot) {
        List<ShippingFeeEntry> entries = new ArrayList<>();
        if (feesSnapshot == null || !feesSnapshot.exists()) {
            return entries;
        }

        for (DataSnapshot entrySnap : feesSnapshot.getChildren()) {
            String dateStr = entrySnap.child("date").getValue(String.class);
            if (isBlank(dateStr)) {
                continue;
            }
            LocalDate date = LocalDate.parse(dateStr);
            Map<String, ShippingCostDetail> costs = new HashMap<>();
            for (DataSnapshot costSnap : entrySnap.getChildren()) {
                String rawKey = costSnap.getKey();
                if ("date".equals(rawKey)) {
                    continue;
                }
                String normalizedKey = normalizeUnit(rawKey);
                if (!ALLOWED_UNITS.contains(normalizedKey)) {
                    continue;
                }

                Double costPerUnit = readCost(costSnap.child("cost_per_unit").getValue());
                if (costPerUnit == null) {
                    continue;
                }

                String unit = Optional.ofNullable(costSnap.child("unit").getValue(String.class))
                        .map(String::trim)
                        .filter(str -> !str.isEmpty())
                        .orElse(normalizedKey);

                costs.put(normalizedKey, new ShippingCostDetail(costPerUnit, unit));
            }
            if (!costs.isEmpty()) {
                entries.add(new ShippingFeeEntry(date, costs));
            }
        }
        return entries;
    }

    private List<ShippingFeeEntryResponseDTO> sanitizeEntries(List<ShippingFeeEntryRequestDTO> entries) {
        if (entries == null) {
            return List.of();
        }

        List<ShippingFeeEntryResponseDTO> sanitized = new ArrayList<>();
        for (ShippingFeeEntryRequestDTO entryReq : entries) {
            if (entryReq == null || isBlank(entryReq.getDate())) {
                continue;
            }

            LocalDate date = LocalDate.parse(entryReq.getDate());
            Map<String, ShippingCostDetailResponseDTO> costs = new HashMap<>();

            if (entryReq.getCosts() != null) {
                for (Map.Entry<String, ShippingCostDetailRequestDTO> cost : entryReq.getCosts().entrySet()) {
                    String normalizedUnit = normalizeUnit(cost.getKey());
                    if (!ALLOWED_UNITS.contains(normalizedUnit)) {
                        continue;
                    }
                    ShippingCostDetailRequestDTO costDetail = cost.getValue();
                    if (costDetail == null) {
                        continue;
                    }
                    costs.put(normalizedUnit, new ShippingCostDetailResponseDTO(
                            costDetail.getCostPerUnit(),
                            costDetail.getUnit() != null ? costDetail.getUnit() : normalizedUnit));
                }
            }

            if (!costs.isEmpty()) {
                sanitized.add(new ShippingFeeEntryResponseDTO(date, costs));
            }
        }

        sanitized.sort(Comparator.comparing(ShippingFeeEntryResponseDTO::getDate).reversed());
        return sanitized;
    }

    private ShippingFeeResponseDTO mergeEntries(ShippingFeeResponseDTO base,
                                                ShippingFeeRequestDTO request,
                                                List<ShippingFeeEntryResponseDTO> newEntries) {
        ShippingFeeResponseDTO response = new ShippingFeeResponseDTO();
        response.setCountry1Name(firstNonBlank(request.getCountry1Name(), base != null ? base.getCountry1Name() : null));
        response.setCountry2Name(firstNonBlank(request.getCountry2Name(), base != null ? base.getCountry2Name() : null));
        response.setCountry1Iso3(firstNonBlank(request.getCountry1Iso3(), base != null ? base.getCountry1Iso3() : null));
        response.setCountry2Iso3(firstNonBlank(request.getCountry2Iso3(), base != null ? base.getCountry2Iso3() : null));
        response.setCountry1IsoNumeric(firstNonBlank(request.getCountry1IsoNumeric(),
                base != null ? base.getCountry1IsoNumeric() : null));
        response.setCountry2IsoNumeric(firstNonBlank(request.getCountry2IsoNumeric(),
                base != null ? base.getCountry2IsoNumeric() : null));

        Map<LocalDate, ShippingFeeEntryResponseDTO> merged = new HashMap<>();
        if (base != null && base.getShippingFees() != null) {
            for (ShippingFeeEntryResponseDTO entry : base.getShippingFees()) {
                if (entry.getDate() != null) {
                    merged.put(entry.getDate(), cloneEntry(entry));
                }
            }
        }
        for (ShippingFeeEntryResponseDTO entry : newEntries) {
            merged.put(entry.getDate(), cloneEntry(entry));
        }

        List<ShippingFeeEntryResponseDTO> mergedList = new ArrayList<>(merged.values());
        mergedList.sort(Comparator.comparing(ShippingFeeEntryResponseDTO::getDate).reversed());
        response.setShippingFees(mergedList);
        return response;
    }

    private ShippingFeeEntryResponseDTO cloneEntry(ShippingFeeEntryResponseDTO entry) {
        Map<String, ShippingCostDetailResponseDTO> clonedCosts = new HashMap<>();
        if (entry.getCosts() != null) {
            entry.getCosts().forEach((key, value) -> {
                if (value != null) {
                    clonedCosts.put(key, new ShippingCostDetailResponseDTO(value.getCostPerUnit(), value.getUnit()));
                }
            });
        }
        return new ShippingFeeEntryResponseDTO(entry.getDate(), clonedCosts);
    }

    private ShippingFeeResponseDTO deepCopy(ShippingFeeResponseDTO source) {
        if (source == null) {
            ShippingFeeResponseDTO empty = new ShippingFeeResponseDTO();
            empty.setShippingFees(Collections.emptyList());
            return empty;
        }

        ShippingFeeResponseDTO copy = new ShippingFeeResponseDTO();
        copy.setCountry1Name(source.getCountry1Name());
        copy.setCountry2Name(source.getCountry2Name());
        copy.setCountry1Iso3(source.getCountry1Iso3());
        copy.setCountry2Iso3(source.getCountry2Iso3());
        copy.setCountry1IsoNumeric(source.getCountry1IsoNumeric());
        copy.setCountry2IsoNumeric(source.getCountry2IsoNumeric());

        if (source.getShippingFees() == null) {
            copy.setShippingFees(Collections.emptyList());
        } else {
            List<ShippingFeeEntryResponseDTO> cloned = new ArrayList<>();
            for (ShippingFeeEntryResponseDTO entry : source.getShippingFees()) {
                cloned.add(cloneEntry(entry));
            }
            copy.setShippingFees(cloned);
        }
        return copy;
    }

    private String buildRouteKey(String iso1, String iso2) {
        String first = iso1 == null ? "" : iso1.trim().toUpperCase();
        String second = iso2 == null ? "" : iso2.trim().toUpperCase();
        if (first.compareTo(second) <= 0) {
            return first + "-" + second;
        }
        return second + "-" + first;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidIso(String iso) {
        return iso != null && iso.trim().length() == 3;
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeUnit(String unit) {
        return unit == null ? "" : unit.trim().toLowerCase();
    }

    private Double readCost(Object rawCost) {
        if (rawCost instanceof Number number) {
            return number.doubleValue();
        }
        if (rawCost instanceof String text) {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private String firstNonBlank(String preferred, String fallback) {
        return isBlank(preferred) ? fallback : preferred;
    }

    private DataSnapshot fetchSnapshot(DatabaseReference ref) throws Exception {
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
            return future.get();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw ie;
        }
    }
}
