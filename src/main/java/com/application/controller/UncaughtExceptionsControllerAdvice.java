package com.application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.application.exception.ServiceValidationException;

@ControllerAdvice
public class UncaughtExceptionsControllerAdvice {
    @ExceptionHandler({ServiceValidationException.class})
    public ResponseEntity<List<ServiceValidationException.FieldErrorDto>> handleBindingErrors(ServiceValidationException ex) {
        return ResponseEntity.status(400).body(ex.getErrors());
    }
}
