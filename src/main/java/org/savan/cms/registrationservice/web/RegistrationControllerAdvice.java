package org.savan.cms.registrationservice.web;

import org.savan.cms.registrationservice.domain.expection.KeycloakEmailException;
import org.savan.cms.registrationservice.domain.expection.KeycloakRegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Error Handler.
 * <p>
 * author: savan1508 on 10.3.24.
 */
@RestControllerAdvice
public class RegistrationControllerAdvice {
    @ExceptionHandler(KeycloakEmailException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    String keycloakEmailHandler(KeycloakEmailException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(KeycloakRegistrationException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    String keycloakRegistrationHandler(KeycloakRegistrationException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
