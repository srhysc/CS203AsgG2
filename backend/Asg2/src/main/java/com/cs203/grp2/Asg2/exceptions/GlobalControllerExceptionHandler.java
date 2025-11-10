package com.cs203.grp2.Asg2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
        CountryNotFoundException.class,
        LandedCostNotFoundException.class,
        PetroleumNotFoundException.class,
        RefineryNotFoundException.class,
        RouteOptimizationNotFoundException.class,
        ShippingFeesNotFoundException.class,
        TariffNotFoundException.class,
        UserNotFoundException.class,
        TradeAgreementNotFoundException.class,
        ConvertableNotFoundException.class
    })
    public @ResponseBody String handleNotFound(RuntimeException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserAuthorizationException.class) // <-- add this
    public @ResponseBody String handleForbidden(UserAuthorizationException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public @ResponseBody String handleBadRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public @ResponseBody String handleGeneral(Exception ex) {
        return "An unexpected error occurred: " + ex.getMessage();
    }
}