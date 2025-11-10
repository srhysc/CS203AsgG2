package com.cs203.grp2.Asg2.models;

import java.util.List;

public class Convertable {
    private String hscode;
    private String name;
    private List<ConvertTo> to;

    public Convertable() {}

    public Convertable(String hscode, String name, List<ConvertTo> to) {
        this.hscode = hscode;
        this.name = name;
        this.to = to;
    }

    public String getHscode() { return hscode; }
    public void setHscode(String hscode) { this.hscode = hscode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ConvertTo> getTo() { return to; }
    public void setTo(List<ConvertTo> to) { this.to = to; }
}