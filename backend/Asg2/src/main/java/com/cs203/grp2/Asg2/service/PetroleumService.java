package com.cs203.grp2.Asg2.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203.grp2.Asg2.DTO.PetroleumPriceDTO;
import com.cs203.grp2.Asg2.exceptions.PetroleumNotFoundException;
import com.cs203.grp2.Asg2.models.Petroleum;
import com.cs203.grp2.Asg2.models.PetroleumPrice;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jakarta.annotation.PostConstruct;

@Service
public class PetroleumService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    private final List<Petroleum> petroleumList = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            getAllPetroleum();
        } catch (Exception e) {
            System.err.println("Failed to initialize petroleum list: " + e.getMessage());
        }
    }
    
    public List<Petroleum> getAllPetroleum() throws Exception {
        DatabaseReference productRef = firebaseDatabase.getReference("product_new");
        DataSnapshot snapshot = fetchSnapshot(productRef);

        synchronized (petroleumList) {
            petroleumList.clear();
            if (snapshot != null && snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.getKey();
                    String hsCode = trimToNull(child.child("hscode").getValue(String.class));

                    if (name == null || hsCode == null) {
                        continue;
                    }

                    List<PetroleumPrice> prices = new ArrayList<>();
                    for (DataSnapshot priceNode : child.child("price").getChildren()) {
                        String dateStr = priceNode.child("date").getValue(String.class);
                        Double avgPrice = priceNode.child("avg_price_per_unit_usd").getValue(Double.class);
                        String unit = priceNode.child("unit").getValue(String.class);

                        if (dateStr != null && avgPrice != null) {
                            prices.add(new PetroleumPrice(LocalDate.parse(dateStr), avgPrice, unit));
                        }
                    }

                    petroleumList.add(new Petroleum(name, hsCode, prices));
                }
            }
            return new ArrayList<>(petroleumList);
        }
    }

    public Petroleum getPetroleumByHsCode(String hsCode) {
        String normalized = normalizeKey(hsCode);
        if (normalized.isEmpty()) {
            throw new PetroleumNotFoundException("Petroleum not found for HS code: " + hsCode);
        }

        synchronized (petroleumList) {
            if (petroleumList.isEmpty()) {
                try {
                    getAllPetroleum();
                } catch (Exception e) {
                    throw new PetroleumNotFoundException("Unable to load petroleum catalogue.");
                }
            }

            return petroleumList.stream()
                    .filter(p -> matchesKey(p, normalized))
                    .findFirst()
                    .orElseThrow(() -> new PetroleumNotFoundException("Petroleum not found for HS code: " + hsCode));
        }
    }

    public List<PetroleumPriceDTO> getAllPetroleumPrices() throws Exception {
        try {
            DatabaseReference productRef = firebaseDatabase.getReference("product_new");
            DataSnapshot snapshot = fetchSnapshot(productRef);

            List<PetroleumPriceDTO> allPricesList = new ArrayList<>();
            
            if (snapshot != null && snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.getKey();
                    String hsCode = child.child("hscode").getValue(String.class);
                    
                    DataSnapshot priceSnapshot = child.child("price");
                    
                    if (priceSnapshot.exists()) {
                        for (DataSnapshot priceNode : priceSnapshot.getChildren()) {
                            Double avgPrice = priceNode.child("avg_price_per_unit_usd").getValue(Double.class);
                            String dateStr = priceNode.child("date").getValue(String.class);
                            String unit = priceNode.child("unit").getValue(String.class);

                            if (dateStr != null && avgPrice != null) {
                                try {
                                    LocalDate parsedDate = LocalDate.parse(dateStr);
                                    allPricesList.add(new PetroleumPriceDTO(
                                            name,
                                            hsCode,
                                            avgPrice,
                                            unit, 
                                            parsedDate));
                                } catch (DateTimeParseException e) {
                                    System.err.println("Invalid date format for " + name + ": " + dateStr);
                                }
                            }
                        }
                    }
                }
            }
            
            System.out.println("Total prices collected: " + allPricesList.size());
            return allPricesList;
            
        } catch (Exception e) {
            System.err.println("Error in getAllPetroleumPrices: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    public void addPetroleumPrice(String hsCode, PetroleumPrice newPrice) throws Exception {
        Petroleum petroleum = getPetroleumByHsCode(hsCode);
        String petroleumName = petroleum.getName();

        if (newPrice.getDate() == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (newPrice.getUnit() == null || newPrice.getUnit().isEmpty()) {
            throw new IllegalArgumentException("Unit cannot be null or empty");
        }

        DatabaseReference priceRef = firebaseDatabase.getReference("product_new")
                .child(petroleumName)
                .child("price")
                .push();

        Map<String, Object> priceData = new HashMap<>();
        priceData.put("date", newPrice.getDate().toString());
        priceData.put("avg_price_per_unit_usd", newPrice.getAvgPricePerUnitUsd());
        priceData.put("unit", newPrice.getUnit());

        CompletableFuture<Void> writeFuture = new CompletableFuture<>();
        priceRef.setValue(priceData, (error, ignored) -> {
            if (error != null) {
                writeFuture.completeExceptionally(error.toException());
            } else {
                writeFuture.complete(null);
            }
        });
        writeFuture.get();

        synchronized (petroleumList) {
            newPrice.setPetroleum(petroleum);
            petroleum.getPrices().add(newPrice);
        }
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeKey(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private boolean matchesKey(Petroleum petroleum, String normalizedKey) {
        String hsCodeKey = normalizeKey(petroleum.getHsCode());
        String nameKey = normalizeKey(petroleum.getName());
        return normalizedKey.equals(hsCodeKey) || normalizedKey.equals(nameKey);
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
