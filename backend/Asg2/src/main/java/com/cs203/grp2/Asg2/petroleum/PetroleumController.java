package com.cs203.grp2.Asg2.petroleum;

import org.springframework.web.bind.annotation.*;

import com.cs203.grp2.Asg2.config.FirebaseConfig;

import jakarta.validation.constraints.Pattern; 

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
            System.out.println(service.getAllPetroleum());
            return service.getAllPetroleum();
        } catch (Exception e) {
            log.info("error retrieving data ");
     
            return null;
        }
        
    }

    @GetMapping("/{hsCode}")
    public Petroleum getPetroleumByHsCode(@PathVariable @Pattern(regexp = "\\d{4,6}") String hsCode) {
        return service.getPetroleumByHsCode(hsCode);
    }
}
