package com.application.controllers;

import com.application.ServiceValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UncaughtExceptionsControllerAdvice {
    @ExceptionHandler({ServiceValidationException.class})
    public ResponseEntity handleBindingErrors(ServiceValidationException ex) {
        return ResponseEntity.status(400).body(ex.getErrors());
    }
}
