package com.cs203.grp2.Asg2.controller;

import org.springframework.web.bind.annotation.*;

import com.cs203.grp2.Asg2.DTO.*;
import com.cs203.grp2.Asg2.models.Petroleum;
import com.cs203.grp2.Asg2.service.PetroleumService;
import com.cs203.grp2.Asg2.service.CountryService;
import com.cs203.grp2.Asg2.models.PetroleumPrice;


import com.cs203.grp2.Asg2.config.FirebaseConfig;
import com.cs203.grp2.Asg2.exceptions.PetroleumNotFoundException;

import jakarta.validation.constraints.Pattern; 

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;



@RestController
@RequestMapping("/petroleum")
public class PetroleumController {

    private final PetroleumService service;
     private static final Logger log = LoggerFactory.getLogger(PetroleumController.class);

    public PetroleumController(PetroleumService service) {
        this.service = service;
    }

    @GetMapping
    public List<Petroleum> getAllPetroleum() {
        try {
            List<Petroleum> list = service.getAllPetroleum();
            System.out.println(list);
            return list;
        } catch (Exception e) {
            log.info("error retrieving data:",e);
            throw new PetroleumNotFoundException("Failed to retrieve petroleum data: " + e.getMessage());
            // return List.of();
        }
        
    }

    @GetMapping("/{hsCode}")
    public Petroleum getPetroleumByHsCode(@PathVariable @Pattern(regexp = "\\d{4,8}") String hsCode) {
        Petroleum petroleum = service.getPetroleumByHsCode(hsCode);
        if (petroleum == null) {
            throw new PetroleumNotFoundException("Petroleum not found for HS code: " + hsCode);
        }
        return petroleum;
    }

    @GetMapping("/price-all")
    public List<PetroleumPriceDTO> getAllPetroleumPrices() {
        try {
            return service.getAllPetroleumPrices();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve latest petroleum prices", e);
        }
    }


    @PostMapping("/{hsCode}/price-new")
    public ResponseEntity<String> addPetroleumPrice(
        @PathVariable @Pattern(regexp = "\\d{4,8}") String hsCode,
        @RequestBody PetroleumPrice newPrice
    ) {
        try {           
            service.addPetroleumPrice(hsCode, newPrice);
            return ResponseEntity.ok("Price added successfully for HS code: " + hsCode);
            
        } catch (PetroleumNotFoundException e) {
            System.err.println("Petroleum not found: " + e.getMessage());
            throw e; // handled by GlobalControllerExceptionHandler
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add price: " + e.getMessage(), e);
        }
    }

}
