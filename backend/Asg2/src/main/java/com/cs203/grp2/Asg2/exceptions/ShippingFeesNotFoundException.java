package com.cs203.grp2.Asg2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShippingFeesNotFoundException extends RuntimeException {
    public ShippingFeesNotFoundException(String message) {
        super(message);
    }
}
