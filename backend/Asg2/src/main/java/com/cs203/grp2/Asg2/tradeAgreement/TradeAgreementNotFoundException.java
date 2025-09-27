package com.cs203.grp2.Asg2.tradeAgreement;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradeAgreementNotFoundException extends RuntimeException {
    public TradeAgreementNotFoundException(String message) {
        super(message);
    }
}
