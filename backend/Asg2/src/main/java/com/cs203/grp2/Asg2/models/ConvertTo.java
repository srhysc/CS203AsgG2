package com.cs203.grp2.Asg2.models;

public class ConvertTo {
    private String hscode;
    private String name;
    private int yield_percent;

    public ConvertTo() {}

    public ConvertTo(String hscode, String name, int yield_percent) {
        this.hscode = hscode;
        this.name = name;
        this.yield_percent = yield_percent;
    }

    public String getHscode() { return hscode; }
    public void setHscode(String hscode) { this.hscode = hscode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getYield_percent() { return yield_percent; }
    public void setYield_percent(int yield_percent) { this.yield_percent = yield_percent; }
}