package com.cs203.grp2.Asg2.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203.grp2.Asg2.petroleum.Petroleum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class CountryService {


    @Autowired
    private final FirebaseDatabase firebaseDatabase;
    private final List<Country> countryArray = new ArrayList<>();
    private volatile boolean initialized = false;

    public CountryService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public CompletableFuture<List<Country>> init() {
        if (initialized) return CompletableFuture.completedFuture(List.copyOf(countryArray));

        DatabaseReference ref = firebaseDatabase.getReference("Country");
        CompletableFuture<List<Country>> fut = new CompletableFuture<>();
        countryArray.clear();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                try {
                    for (DataSnapshot child : snap.getChildren()) {
                        String key = child.getKey();
                        CountryFirebaseDTO dto = child.getValue(CountryFirebaseDTO.class);
                        if (key != null && dto != null) countryArray.add(dto.toDomain(key));
                    }
                    countryArray.sort(Comparator.comparing(Country::getName, String.CASE_INSENSITIVE_ORDER));
                    initialized = true;
                    fut.complete(List.copyOf(countryArray));
                } catch (Exception e) {
                    fut.completeExceptionally(e);
                }
            }
            @Override public void onCancelled(DatabaseError error) {
                fut.completeExceptionally(error.toException());
            }
        });

        return fut;
    }

private void ensureInitialized() {
    if (!initialized) {
        try { init(); } 
        catch (Exception e) { throw new RuntimeException("Failed to init countries", e); }
    }
}

public Country getCountryByISO3n(Integer iso3n) {
    ensureInitialized();
    return countryArray.stream()
            .filter(c -> Objects.equals(c.getCode(), iso3n))   // "Code" in RTDB == ISO3N numeric
            .findFirst()
            .orElseThrow(() ->
                new CountryNotFoundException("No country with iso3n=" + iso3n));
}

public Country getCountryByName(String name) {
    ensureInitialized();
    return countryArray.stream()
            .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() ->
                new CountryNotFoundException("No country with name=" + name));
}

    /** Force re-load from RTDB (e.g., admin changed data) */
        public CompletableFuture<List<Country>> refresh() {
            initialized = false;
            return init();
        }

}