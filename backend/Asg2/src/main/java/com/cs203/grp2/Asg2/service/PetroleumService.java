package com.cs203.grp2.Asg2.service;

import org.springframework.stereotype.Service;

import com.cs203.grp2.Asg2.models.Petroleum;

import java.util.ArrayList;
import java.util.List;

@Service
public class PetroleumService {

    private final List<Petroleum> petroleumList = new ArrayList<>();

    public PetroleumService() {
        // Mock data
        petroleumList.add(new Petroleum("Crude Oil", "2709", 100.0));
        petroleumList.add(new Petroleum("crude petroleum", "271012", 120.0));
        petroleumList.add(new Petroleum("Diesel", "2711", 90.0));
    }

    public List<Petroleum> getAllPetroleum() {
        return petroleumList;
    }

    public Petroleum getPetroleumByHsCode(String hsCode) {
        return petroleumList.stream()
                .filter(p -> p.getHsCode().equalsIgnoreCase(hsCode))
                .findFirst()
                .orElse(null);
    }
}
