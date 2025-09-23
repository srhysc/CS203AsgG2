package com.cs203.grp2.Asg2.vat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VATNotFoundException extends RuntimeException {

    public VATNotFoundException(String message) {
        super(message);
    }
}
