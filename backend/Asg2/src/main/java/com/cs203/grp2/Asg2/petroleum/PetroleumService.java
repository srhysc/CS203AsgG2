package com.cs203.grp2.Asg2.petroleum;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PetroleumService {

    private final List<Petroleum> petroleumList = new ArrayList<>();

    public PetroleumService() {
        // Add some fake data here
        petroleumList.add(new Petroleum("270900", "Crude Petroleum", 50.0));
        petroleumList.add(new Petroleum("271000", "Petroleum Oils", 70.5));
    }

    public List<Petroleum> getAllPetroleum() {
        return petroleumList;
    }

    public Optional<Petroleum> getPetroleumByHsCode(String hsCode) {
        return petroleumList.stream()
                .filter(p -> p.getHsCode().equals(hsCode))
                .findFirst();
    }

    public Petroleum addPetroleum(Petroleum petroleum) {
        petroleumList.add(petroleum);
        return petroleum;
    }

    public void deletePetroleum(String hsCode) {
        petroleumList.removeIf(p -> p.getHsCode().equals(hsCode));
    }
}
