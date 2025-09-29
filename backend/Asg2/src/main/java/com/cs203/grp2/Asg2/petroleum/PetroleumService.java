package com.cs203.grp2.Asg2.petroleum;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PetroleumService {

    private final PetroleumRepository repo;

    private final List<Petroleum> petroleumList = new ArrayList<>();

    public PetroleumService() {
        this.repo = repo; 
    }

    public List<Petroleum> getAllPetroleum() {
        //return petroleumList;
        return repo.findAll();
    }

    public Petroleum getPetroleumByHsCode(String hsCode) {
        return repo.findByHsCode(hsCode)
                   .orElseThrow(() -> new PetroleumNotFoundException("Petroleum not found: " + hsCode));
    }

}
