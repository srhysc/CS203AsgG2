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

import com.cs203.grp2.Asg2.models.*;
import com.cs203.grp2.Asg2.exceptions.*;
import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.controller.*;
import com.cs203.grp2.Asg2.config.*;
import com.cs203.grp2.Asg2.service.*;
@Service
public class PetroleumService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    private final List<Petroleum> petroleumList = new ArrayList<>();

    public List<Petroleum> getAllPetroleum() throws Exception {
        System.out.println("🔥 [DEBUG] Starting getAllPetroleum()");
        DatabaseReference productRef = firebaseDatabase.getReference("product");
        CompletableFuture<List<Petroleum>> future = new CompletableFuture<>();

        petroleumList.clear();

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("✅ [DEBUG] onDataChange triggered! snapshot.exists() = " + snapshot.exists());
                System.out.println("🔎 [DEBUG] snapshot.getChildrenCount() = " + snapshot.getChildrenCount());

                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        System.out.println("🔍 [DEBUG] Child: " + child.getKey());
                        String name = child.getKey();

                        // Retrieve all values as Strings
                        String hsCodeStr = child.child("hsCode").getValue(String.class);
                        String priceStr = child.child("Price").getValue(String.class);

                        // Parse them into proper types
                        String hsCode = hsCodeStr != null ? hsCodeStr.trim() : null;
                        Double price = null;

                        if (priceStr != null && !priceStr.trim().isEmpty()) {
                            try {
                                price = Double.parseDouble(priceStr.trim());
                            } catch (NumberFormatException e) {
                                System.err.println("⚠️ [DEBUG] Invalid price format for " + name + ": " + priceStr);
                            }
                        }

                        System.out.println("    ↳ hsCode=" + hsCode + " | Price=" + price);

                        if (name != null && hsCode != null ) {
                            petroleumList.add(new Petroleum(name, hsCode, price));
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

    // public PetroleumService() {
    // // Get data and add to list

    // // petroleumList.add(new Petroleum("Crude Oil", "2709", 100.0));
    // // petroleumList.add(new Petroleum("Crude Petroleum", "271012", 120.0));
    // // petroleumList.add(new Petroleum("Diesel", "2711", 90.0));
    // }

    // public List<Petroleum> getAllPetroleum() {
    // getPetroleumDB();
    // return petroleumList;
    // }

    public Petroleum getPetroleumByHsCode(String hsCode) {
        return petroleumList.stream()
                .filter(p -> p.getHsCode().equalsIgnoreCase(hsCode))
                .findFirst()
                .orElse(null);
    }
}
