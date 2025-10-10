package com.cs203.grp2.Asg2.controller;

import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cs203.grp2.Asg2.models.TariffLatest;
import com.cs203.grp2.Asg2.service.WitsTariffService;

@RestController
@RequestMapping("/tariffs")
@Validated
public class TariffController {
  private final WitsTariffService svc;
  public TariffController(WitsTariffService svc) { this.svc = svc; }

  // Example:
  // /tariffs/latest?reporter=156&partner=702&product=271012&datatype=aveestimated
  @GetMapping("/latest")
  public TariffLatest latest(@RequestParam int reporter,
                             @RequestParam int partner,
                             @RequestParam @Pattern(regexp="\\d{6}") String product,
                             @RequestParam(defaultValue = "aveestimated")
                             @Pattern(regexp="(?i)(reported|aveestimated)") String datatype) {
    return svc.getLatest(reporter, partner, product, datatype);
  }
}
