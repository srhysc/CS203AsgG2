package com.cs203.grp2.Asg2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.time.LocalDate;

import com.cs203.grp2.Asg2.models.*;
import com.cs203.grp2.Asg2.exceptions.*;
import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.controller.*;
import com.cs203.grp2.Asg2.config.*;
import com.cs203.grp2.Asg2.service.*;

import java.time.LocalDateTime;

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
        System.out.println("🔥 [DEBUG] Starting getAllPetroleum()");
        // CHANGE FIREBASEREF LATER
        DatabaseReference productRef = firebaseDatabase.getReference("product_new");
        CompletableFuture<List<Petroleum>> future = new CompletableFuture<>();

        petroleumList.clear();

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("✅ [DEBUG] onDataChange triggered! snapshot.exists() = " + snapshot.exists());
                System.out.println("🔎 [DEBUG] snapshot.getChildrenCount() = " + snapshot.getChildrenCount());

                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        System.out.println("🔍 [DEBUG] PETROLEUM Child: " + child.getKey());
                        String name = child.getKey();

                        // Retrieve HSCODE as Strings
                        String hsCodeStr = child.child("hscode").getValue(String.class);
                        //try parse into proper type
                        String hsCode = hsCodeStr != null ? hsCodeStr.trim() : null;
                        
                        // Retrieve price lists
                        List<PetroleumPrice> prices = new ArrayList<>();
                        //for each row's "price" children
                        for (DataSnapshot priceNode : child.child("price").getChildren()) {
                        //get date, average price, unit
                        String dateStr = priceNode.child("date").getValue(String.class);
                        Double avgPrice = priceNode.child("avg_price_per_unit_usd").getValue(Double.class);
                        String unit = priceNode.child("unit").getValue(String.class);

                        if (dateStr != null && avgPrice != null) {
                            prices.add(new PetroleumPrice(LocalDate.parse(dateStr), avgPrice, unit));
                            }
                        }                        


                        if (name != null && hsCode != null ) {
                            petroleumList.add(new Petroleum(name, hsCode, prices));
                        }
                    }

                    System.out.println("✅ [DEBUG] Total petroleum entries fetched: " + petroleumList.size());
                    future.complete(petroleumList);

                } catch (Exception e) {
                    System.err.println("❌ [DEBUG] Exception while parsing: " + e.getMessage());
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("❌ [DEBUG] Firebase read failed: " + error.getMessage());
                future.completeExceptionally(error.toException());
            }
        });

        List<Petroleum> result = future.get();
        System.out.println("✅ [DEBUG] Returning " + result.size() + " Petroleum objects: " + result);
        return result;
    }

    public Petroleum getPetroleumByHsCode(String hsCode) {
        Petroleum petroleum = petroleumList.stream()
                .filter(p -> p.getHsCode().equalsIgnoreCase(hsCode))
                .findFirst()
                .orElse(null);
        if (petroleum == null) {
            throw new PetroleumNotFoundException("Petroleum not found for HS code: " + hsCode);
        }
        return petroleum;
    }

    public List<PetroleumLatestPriceDTO> getLatestPetroleumPrices() throws Exception {
        DatabaseReference productRef = firebaseDatabase.getReference("product_new");
        CompletableFuture<List<PetroleumLatestPriceDTO>> future = new CompletableFuture<>();
        List<PetroleumLatestPriceDTO> latestList = new ArrayList<>();

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String name = child.getKey();
                        String hsCode = child.child("hscode").getValue(String.class);

                        List<PetroleumPrice> prices = new ArrayList<>();
                        for (DataSnapshot priceNode : child.child("price").getChildren()) {
                            String dateStr = priceNode.child("date").getValue(String.class);
                            Double avgPrice = priceNode.child("avg_price_per_unit_usd").getValue(Double.class);
                            String unit = priceNode.child("unit").getValue(String.class);

                            if (dateStr != null && avgPrice != null) {
                                prices.add(new PetroleumPrice(LocalDate.parse(dateStr), avgPrice, unit));
                            }
                        }

                        // Sort and get latest
                        prices.sort((a, b) -> b.getDate().compareTo(a.getDate()));
                        if (!prices.isEmpty()) {
                            PetroleumPrice latest = prices.get(0);
                            latestList.add(new PetroleumLatestPriceDTO(name, hsCode, latest.getAvgPricePerUnitUsd(), latest.getUnit(), latest.getDate()));
                        }
                    }

                    future.complete(latestList);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            future.completeExceptionally(error.toException());
        }
    });

    return future.get();
    }

    public void addPetroleumPrice(String hsCode, PetroleumPrice newPrice) throws Exception { 
        // Find petroleum by hsCode first
        Petroleum petroleum = getPetroleumByHsCode(hsCode);
            if (petroleum == null) {
                throw new PetroleumNotFoundException("Petroleum not found for HS code: " + hsCode);
            }
            
            String petroleumName = petroleum.getName();
            
            // Validate the price object
            if (newPrice.getDate() == null) {
                throw new IllegalArgumentException("Date cannot be null");
            }
            if (newPrice.getUnit() == null || newPrice.getUnit().isEmpty()) {
                throw new IllegalArgumentException("Unit cannot be null or empty");
            }
            
            DatabaseReference priceRef = firebaseDatabase.getReference("product_new")
                    .child(petroleumName)
                    .child("price")
                    .push(); // creates a new unique key

            System.out.println("📝 Firebase path: product_new/" + petroleumName + "/price/<new-key>");

            Map<String, Object> priceData = new HashMap<>();
            priceData.put("date", newPrice.getDate().toString());
            priceData.put("avg_price_per_unit_usd", newPrice.getAvgPricePerUnitUsd());
            priceData.put("unit", newPrice.getUnit());

            System.out.println("📦 Price data to save: " + priceData);

            CompletableFuture<Void> future = new CompletableFuture<>();

            priceRef.setValue(priceData, (error, ref) -> {
                if (error != null) {
                    System.err.println("❌ Failed to add price: " + error.getMessage());
                    future.completeExceptionally(error.toException());
                } else {
                    System.out.println("✅ Successfully added new price entry under " + petroleumName + "/price/");
                    System.out.println("✅ Firebase key: " + ref.getKey());
                    future.complete(null);
                }
            });

            try {
                future.get(); // Wait for Firebase write to complete
                System.out.println("✅ Firebase write completed successfully");
            } catch (Exception e) {
                System.err.println("❌ Firebase write failed: " + e.getMessage());
                throw e;
            }
    }
}