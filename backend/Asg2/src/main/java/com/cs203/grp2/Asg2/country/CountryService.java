package com.cs203.grp2.Asg2.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203.grp2.Asg2.petroleum.Petroleum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class CountryService {

    private final FirebaseDatabase firebase;

    private final List<Country> countries = new ArrayList<>();
    // quick indexes after init:
    private final Map<String, Country> byCode = new HashMap<>();
    private final Map<String, Country> byNameCI = new HashMap<>();

    public CountryService(FirebaseDatabase firebase) {
        this.firebase = firebase;
        init();  // eager load on bean creation; or expose a public init if you prefer manual
    }

    /** Loads /Country into memory and builds indexes. */
    public final void init() {
        try {
            DataSnapshot snap = await(firebase.getReference("/Country"));
            countries.clear();
            byCode.clear();
            byNameCI.clear();

            if (snap.exists()) {
                System.out.println("✅ is this nsap even exist?");
                for (DataSnapshot node : snap.getChildren()) {
                    System.out.println("✅ is this child even exist?");
                    Country c = node.getValue(Country.class);
                    if (c == null) continue;

                    String code = node.child("Code").getValue(String.class);
                    String iso3 = node.child("ISO3").getValue(String.class);
                    System.out.println("DBG " + node.getKey() + " Code=" + code + " ISO3=" + iso3);

                    // DB key is the country name — capture it
                    c.setName(node.getKey());

                    // normalize all null numeric fields to 0/0.0 as per your rule
                    c.normalize();

                    countries.add(c);

                    if (c.getCode() != null) byCode.put(code, c);
                    if (c.getName() != null) byNameCI.put(c.getName().toLowerCase(Locale.ROOT), c);

                    System.out.println("✅ Country code check: Entries " + byCode.size());
                    System.out.println("✅ Country name check: Entries " + byNameCI.size());
                }
            }
        } catch (Exception e) {
            // You may want a logger here
            throw new RuntimeException("Failed to load /Country from Firebase: " + e.getMessage(), e);
        }
    }

    public List<Country> getAll() {
        return Collections.unmodifiableList(countries);
    }

    // ====== lookups from the in-memory list/index (not hitting the DB) ======

    public Country getCountryByCode(String Code) {
        Country c = byCode.get(Code);
        if (c == null) throw new CountryNotFoundException("No country with code=" + Code);
        return c;
    }

    public Country getCountryByName(String name) {
        if (name == null) throw new CountryNotFoundException("No country with name=null");
        Country c = byNameCI.get(name.toLowerCase(Locale.ROOT));
        if (c == null) throw new CountryNotFoundException("No country with name=" + name);
        return c;
    }

    // convenience: ISO3 → Country (since some flows use ISO3 strings)
    public Country getCountryByISO3(String iso3) {
        if (iso3 == null) throw new CountryNotFoundException("No country with iso3=null");
        String needle = iso3.toUpperCase(Locale.ROOT);
        for (Country c : countries) {
            if (needle.equalsIgnoreCase(c.getISO3())) return c;
        }
        throw new CountryNotFoundException("No country with iso3=" + iso3);
    }

    // ====== Firebase await helper ======
    private DataSnapshot await(DatabaseReference ref) throws Exception {
        CompletableFuture<DataSnapshot> fut = new CompletableFuture<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) { fut.complete(snapshot); }
            public void onCancelled(DatabaseError error) { fut.completeExceptionally(new RuntimeException(error.getMessage())); }
        });
        return fut.get();
    }
}
