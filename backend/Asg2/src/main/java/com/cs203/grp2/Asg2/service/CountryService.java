package com.cs203.grp2.Asg2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203.grp2.Asg2.models.Petroleum;
import com.cs203.grp2.Asg2.models.Country;
import com.cs203.grp2.Asg2.models.VATRate;
import com.cs203.grp2.Asg2.DTO.CountryDTO;
import com.cs203.grp2.Asg2.exceptions.*;
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
import java.time.LocalDate;

@Service
public class CountryService {

    private final FirebaseDatabase firebase;

    private final List<Country> countries = new ArrayList<>();
    // quick indexes after init:
    private final Map<String, Country> byCode = new HashMap<>();
    private final Map<String, Country> byNameCI = new HashMap<>();

    public CountryService(FirebaseDatabase firebase) {
        this.firebase = firebase;
        init(); // eager load on bean creation; or expose a public init if you prefer manual
    }

    /** Loads /Country into memory and builds indexes. */
    public final void init() {
        try {
            DataSnapshot snap = await(firebase.getReference("/Country_NEW"));
            countries.clear();
            byCode.clear();
            byNameCI.clear();

            if (snap.exists()) {
                for (DataSnapshot node : snap.getChildren()) {
                    Country c = node.getValue(Country.class);
                    if (c == null)
                        continue;

                    String code = node.child("code").getValue(String.class);
                    String iso3 = node.child("iso3n").getValue(String.class);
                    try {
                        List<VATRate> rateList = new ArrayList<>();
                        // NEW METHOD TO LOOP
                        for (DataSnapshot priceNode : node.child("vat_rates").getChildren()) {
                            String dateStr = priceNode.child("date").getValue(String.class);
                            Double vatRate = priceNode.child("rate").getValue(Double.class);
                            System.out.println("date " + LocalDate.parse(dateStr) + "VAT rate " + vatRate);

                            if (dateStr != null && vatRate != null) {
                                rateList.add(new VATRate(LocalDate.parse(dateStr), vatRate));
                            }
                        }
                        c.setVatRates(rateList);
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println("no vat rate recorded");
                    }

                    System.out.println("DBG " + node.getKey() + " Code=" + code + " ISO3=" + iso3);

                    // DB key is the country name — capture it
                    c.setName(node.getKey());

                    // normalize all null numeric fields to 0/0.0 as per your rule
                    c.normalize();

                    countries.add(c);

                    if (c.getCode() != null)
                        byCode.put(code, c);
                    if (c.getName() != null)
                        byNameCI.put(c.getName().toLowerCase(Locale.ROOT), c);

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
        if (c == null)
            throw new CountryNotFoundException("No country with code=" + Code);
        return c;
    }

    public Country getCountryByName(String name) {
        if (name == null)
            throw new CountryNotFoundException("No country with name=null");
        Country c = byNameCI.get(name.toLowerCase(Locale.ROOT));
        if (c == null)
            throw new CountryNotFoundException("No country with name=" + name);
        return c;
    }

    // convenience: ISO3 → Country (since some flows use ISO3 strings)
    public Country getCountryByISO3(String iso3) {
        if (iso3 == null)
            throw new CountryNotFoundException("No country with iso3=null");
        String needle = iso3.toUpperCase(Locale.ROOT);
        for (Country c : countries) {
            if (needle.equalsIgnoreCase(c.getISO3()))
                return c;
        }
        throw new CountryNotFoundException("No country with iso3=" + iso3);
    }

    public List<CountryDTO> getLatestVatRatesForAllCountries() {
    try {
        init(); // Reload all countries from Firebase
    } catch (Exception e) {
        System.err.println("Failed to reload countries: " + e.getMessage());
    }
    
    List<CountryDTO> result = new ArrayList<>();

    for (Country country : countries) {
        if (country.getVatRates() != null && !country.getVatRates().isEmpty()) {
           
            Optional<VATRate> latest = country.getVatRates().stream()
                .max(Comparator.comparing(VATRate::getDate));

            if (latest.isPresent()) {
                VATRate rate = latest.get();
                result.add(new CountryDTO(
                    country.getName(),
                    rate.getVATRate(),
                    rate.getDate()
                ));
            }
        }
    }

    return result;
}


    //ADD VAT RATE TO FIREBASE
    public void addVatRate(String countryName, VATRate newRate) throws Exception {
    if (countryName == null || newRate == null) {
        throw new IllegalArgumentException("Country name and VAT rate must not be null.");
    }

    // Ensure country exists in memory
    Country country = getCountryByName(countryName);
    if (country == null) {
        throw new CountryNotFoundException("Country not found: " + countryName);
    }

    DatabaseReference vatRef = firebase.getReference("/Country_NEW")
            .child(countryName)
            .child("vat_rates")
            .push(); // creates a new unique key (e.g. vat_rates/-Nsg92Kdj123)

    Map<String, Object> vatData = new HashMap<>();
    vatData.put("date", newRate.getDate().toString());
    vatData.put("rate", newRate.getVATRate());

    CompletableFuture<Void> future = new CompletableFuture<>();

    vatRef.setValue(vatData, (error, ref) -> {
        if (error != null) {
            System.err.println("❌ Failed to add VAT rate: " + error.getMessage());
            future.completeExceptionally(error.toException());
        } else {
            System.out.println("✅ Added new VAT rate for " + countryName + " under vat_rates/");
            future.complete(null);
        }
    });

    // Wait until Firebase write completes
    future.get();

    System.out.println("🔄 Reloading countries from Firebase after VAT rate update...");
    init();  // This will reload all countries with the new VAT rate
    }


    // ====== Firebase await helper ======
    private DataSnapshot await(DatabaseReference ref) throws Exception {
        CompletableFuture<DataSnapshot> fut = new CompletableFuture<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                fut.complete(snapshot);
            }

            public void onCancelled(DatabaseError error) {
                fut.completeExceptionally(new RuntimeException(error.getMessage()));
            }
        });
        return fut.get();
    }

}
