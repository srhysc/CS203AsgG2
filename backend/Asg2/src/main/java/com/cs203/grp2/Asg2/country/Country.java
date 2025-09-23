package com.cs203.grp2.Asg2.country;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Country {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @Min(value = 1, message = "ISO6code must be positive")
    private int ISO6code;

    public Country() {
    }

    public Country(String name, int ISO6code) {
        this.name = name;
        this.ISO6code = ISO6code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getISO6code() {
        return ISO6code;
    }

    public void setISO6code(int ISO6code) {
        this.ISO6code = ISO6code;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", ISO6code=" + ISO6code +
                '}';
    }
}
