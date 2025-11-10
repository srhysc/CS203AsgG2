package com.cs203.grp2.Asg2.exceptions;

public class ConvertableNotFoundException extends RuntimeException {
    public ConvertableNotFoundException(String hscode) {
        super("Convertable with HS code " + hscode + " not found.");
    }
}