package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.models.*;
import com.cs203.grp2.Asg2.exceptions.*;
import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.controller.*;
import com.cs203.grp2.Asg2.config.*;
import com.cs203.grp2.Asg2.service.*;
import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class WitsService {
    private static final Logger log = LoggerFactory.getLogger(WitsService.class);

    private final FirebaseDatabase firebase;   // already configured in your app
    private final RestTemplate http = new RestTemplate(); // swap for WebClient if you prefer

    public WitsService(FirebaseDatabase firebase) {
        this.firebase = firebase;
    }

    /** Public entrypoint: DB → WITS → 0.0 */
    @Cacheable
    public WitsTariff resolveTariff(TariffRequestDTO req) {
        // 1) Preferential from Firebase (if exporter+importer in same agreement & hs covered & date in force)
        Optional<WitsTariff> pref = findPreferentialFromDb(req);
        if (pref.isPresent()) {
            System.out.println("🔎 pref present");
            return pref.get();
        } 

        // 2) MFN from Firebase (importer HS6)
        Optional<WitsTariff> mfn = findMfnFromDb(req);
        if (mfn.isPresent()){
            System.out.println("🔎 mfc present = " + mfn.get().ratePercent());
            mfn.get().ratePercent();
            return mfn.get();
        } 
         System.out.println("🔎 doing wits....");
       // 3) Fallback to WITS (or any HTTP data source you wire in)
        Optional<WitsTariff> wits = fetchFromWits(req);
        return wits.orElseGet(() ->
        
            new WitsTariff(req.importerIso3(), req.exporterIso3(), req.hs6(), req.date(), 0.0, "none",
                           "No rate found in DB; WITS returned none/invalid")
        );

  
    }

    /* =====================  DB LOOKUPS  ===================== */

    private Optional<WitsTariff> findPreferentialFromDb(TariffRequestDTO req) {
        try {
            // 1) Load importer’s agreements + EIF date
            Map<String, LocalDate> importerAgreements = getAgreementsFor(req.importerIso3());
            if (importerAgreements.isEmpty()) return Optional.empty();

            // 2) Load exporter’s agreements
            Map<String, LocalDate> exporterAgreements = getAgreementsFor(req.exporterIso3());
            if (exporterAgreements.isEmpty()) return Optional.empty();

            // 3) Intersect by agreements where both are in & EIF <= req.date
            for (var entry : importerAgreements.entrySet()) {
                String agreement = entry.getKey();
                LocalDate importerEIF = entry.getValue();
                LocalDate exporterEIF = exporterAgreements.get(agreement);
                if (exporterEIF == null) continue;
                if (importerEIF != null && importerEIF.isAfter(req.date())) continue;
                if (exporterEIF != null && exporterEIF.isAfter(req.date())) continue;

                // 4) Read preferential HS6 rate for this agreement (adjust path to your schema)
                Double rate = readDouble("/Tariff/agreementRates/" + agreement + "/" + req.hs6() + "/ratePercent");
                if (rate != null) {
                    return Optional.of(new WitsTariff(
                            req.importerIso3(), req.exporterIso3(), req.hs6(), req.date(),
                            normalize(rate), "preferential", "DB: " + agreement + " HS6"
                    ));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Preferential DB lookup failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<WitsTariff> findMfnFromDb(TariffRequestDTO req) {
        
        System.out.println("🎣 checking db for " + "/Tariff/mfnRates/" + req.importerIso3() + "/0" + "/MFNave");
        try {
            // Adjust to your MFN/normal-rate location if you keep one
            Double rate = readDouble("/Tariff/mfnRates/" + req.importerIso3() + "/0/" + "MFNave");
            if (rate == null) return Optional.empty();
            return Optional.of(new WitsTariff(
                    req.importerIso3(), req.exporterIso3(), req.hs6(), req.date(),
                    normalize(rate), "mfn", "DB: MFN HS6"
            ));
        } catch (Exception e) {
            log.warn("MFN DB lookup failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /* =====================  WITS LOOKUP  ===================== */

    private Optional<WitsTariff> fetchFromWits(TariffRequestDTO req) {
        try {
          
            // You may need reporter=importer, partner=exporter or partner=World, product=HS6, year=date.getYear(), etc.
            String url = String.format(
                "https://wits.worldbank.org/API/V1/SDMX/V21/rest/data/DF_WITS_Tariff_TRAINS/.%s.%s.%s.reported/",
                 iso3nOrIso3(req.importerIso3()), iso3nOrIso3(req.exporterIso3()),req.hs6()
            );

            ResponseEntity<String> resp = http.getForEntity(url, String.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) return Optional.empty();

            // TODO: parse SDMX-XML/JSON → extract the rate. For now, use a parser stub:
            Double parsedRate = parseWitsRateFromBody(resp.getBody());
            if (parsedRate == null) return Optional.empty();

            return Optional.of(new WitsTariff(
                    req.importerIso3(), req.exporterIso3(), req.hs6(), req.date(),
                    normalize(parsedRate), "wits", "WITS fallback"
            ));
        } catch (RestClientException e) {
            log.warn("WITS call failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /* =====================  HELPERS  ===================== */

    private Map<String, LocalDate> getAgreementsFor(String iso3) throws Exception {
        // Reads: /countryMembers/{ISO3}/* → {eif: "YYYY-MM-DD"}
        String base = "/countryMembers/" + iso3;
        DataSnapshot snap = await(firebase.getReference(base));
        Map<String, LocalDate> out = new HashMap<>();
        if (snap.exists()) {
            for (DataSnapshot child : snap.getChildren()) {
                DataSnapshot eifNode = child.child("eif");
                if (eifNode.exists()) {
                    try {
                        out.put(child.getKey(), LocalDate.parse(eifNode.getValue(String.class)));
                    } catch (Exception ignored) {
                        out.put(child.getKey(), null); // treat as present but undated
                    }
                } else {
                    out.put(child.getKey(), null);
                }
            }
        }
        return out;
    }

    private Double readDouble(String path) throws Exception {
        DataSnapshot snap = await(firebase.getReference(path));
        if (!snap.exists()) return null;
        try {
            Object v = snap.getValue();
            if (v == null) return null;
            return Double.parseDouble(v.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private DataSnapshot await(DatabaseReference ref) throws Exception {
        CompletableFuture<DataSnapshot> fut = new CompletableFuture<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) { fut.complete(snapshot); }
            public void onCancelled(DatabaseError error) { fut.completeExceptionally(new RuntimeException(error.getMessage())); }
        });
        return fut.get(); // waits
    }

    private Double parseWitsRateFromBody(String body) {
        // TODO: Implement proper SDMX/XML parsing. Return null if not parseable.
        return null;
    }

    private String iso3nOrIso3(String iso) {
        // TODO: If your WITS endpoint needs numeric codes, map here.
        return iso;
    }

    private double normalize(Double v) {
        if (v == null || v.isNaN() || v < 0) return 0.0;
        // Ensure one decimal place if you prefer: return Math.round(v * 10.0) / 10.0;
        return v;
    }
}