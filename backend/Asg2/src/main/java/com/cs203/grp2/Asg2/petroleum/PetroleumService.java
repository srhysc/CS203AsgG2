package com.cs203.grp2.Asg2.petroleum;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Service
public class PetroleumService {

    private final PetroleumRepository petroleumRepo;
    private final PetroleumPriceHistoryRepository priceHistoryRepo;

    public PetroleumService(PetroleumRepository petroleumRepo, PetroleumPriceHistoryRepository priceHistoryRepo) {
        this.petroleumRepo = petroleumRepo;
        this.priceHistoryRepo = priceHistoryRepo;
    }

    public List<Petroleum> getAllPetroleum() {
        return petroleumRepo.findAll();
    }

    public Petroleum getPetroleumByHsCode(String hsCode) {
        return petroleumRepo.findById(hsCode)
                   .orElseThrow(() -> new PetroleumNotFoundException("Petroleum not found: " + hsCode));
    }

    public double getCurrentPrice(String hscode) {
        return priceHistoryRepo.findMostRecentPrice(hscode)
                .orElseThrow(() -> new PetroleumNotFoundException("No price history found for: " + hscode))
                .getPricePerUnit();
    }

    public double getPriceOnDate(String hscode, LocalDate date) {
        return priceHistoryRepo.findLatestPriceBeforeDate(hscode, date)
                .orElseThrow(() -> new PetroleumNotFoundException("No price history found for: " + hscode + " on or before " + date))
                .getPricePerUnit();
    }
}
