package com.cs203.grp2.Asg2.petroleum;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)  // Returns 404 status when thrown
public class PetroleumNotFoundException extends RuntimeException {

    public PetroleumNotFoundException(String message) {
        super(message);
    }
}
