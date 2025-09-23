package com.cs203.grp2.Asg2.country;

public class Country {

    private int iso6Code;
    private String name;

    // Default constructor
    public Country() {
    }

    // Constructor with fields
    public Country(int iso6Code, String name) {
        this.iso6Code = iso6Code;
        this.name = name;
    }

    // Getters and setters

    public int getIso6Code() {
        return iso6Code;
    }

    public void setIso6Code(int iso6Code) {
        this.iso6Code = iso6Code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Country{" +
                "iso6Code=" + iso6Code +
                ", name='" + name + '\'' +
                '}';
    }
}
