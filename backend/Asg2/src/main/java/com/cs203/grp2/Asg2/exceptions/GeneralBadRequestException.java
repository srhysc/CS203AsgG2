package com.cs203.grp2.Asg2.exceptions;

public class GeneralBadRequestException extends RuntimeException {
    public GeneralBadRequestException(String message) {
        super(message);
    }
}