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


@Service
public class PetroleumService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    private final List<Petroleum> petroleumList = new ArrayList<>();

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
        // return petroleumList.stream()
        //         .filter(p -> p.getHsCode().equalsIgnoreCase(hsCode))
        //         .findFirst()
        //         .orElse(null);
        Petroleum petroleum = petroleumList.stream()
                .filter(p -> p.getHsCode().equalsIgnoreCase(hsCode))
                .findFirst()
                .orElse(null);
        if (petroleum == null) {
            throw new PetroleumNotFoundException("Petroleum not found for HS code: " + hsCode);
        }
        return petroleum;
    }

    public void addPetroleumPrice(String petroleumName, PetroleumPrice newPrice) throws Exception {
    DatabaseReference priceRef = firebaseDatabase.getReference("product_new")
            .child(petroleumName)
            .child("price")
            .push(); // make new child under price/

        //create new pricedata object 
        Map<String, Object> priceData = new HashMap<>();
        priceData.put("date", newPrice.getDate().toString());
        priceData.put("avg_price_per_unit_usd", newPrice.getAvgPricePerUnitUsd());
        priceData.put("unit", newPrice.getUnit());

        CompletableFuture<Void> future = new CompletableFuture<>();

        //update new child with pricedata values
        priceRef.setValue(priceData, (error, ref) -> {
            if (error != null) {
                System.err.println("❌ Failed to add price: " + error.getMessage());
                future.completeExceptionally(error.toException());
            } else {
                System.out.println("✅ Added new price entry under " + petroleumName + "/price/");
                future.complete(null);
            }
        });

    future.get();
}

}
