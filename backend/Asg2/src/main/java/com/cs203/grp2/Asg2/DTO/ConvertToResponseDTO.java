package com.cs203.grp2.Asg2.DTO;

public class ConvertToResponseDTO {
    private String hscode;
    private String name;
    private int yield_percent;

    public ConvertToResponseDTO(String hscode, String name, int yield_percent) {
        this.hscode = hscode;
        this.name = name;
        this.yield_percent = yield_percent;
    }

    public String getHscode() { return hscode; }
    public String getName() { return name; }
    public int getYield_percent() { return yield_percent; }
}