package com.cs203.grp2.Asg2.DTO;

public record AddMfnRateDTO(
    String countryIso3,
    double mfnAve,
    int year
) {}