package com.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceValidationException extends RuntimeException {
    private final BindingResult bindingResult;

    public ServiceValidationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public List<FieldErrorDto> getErrors() {
        return bindingResultsToListOfFieldErrors(bindingResult);
    }

    public static List<FieldErrorDto> bindingResultsToListOfFieldErrors(BindingResult bindingResult) {
        return bindingResult == null ? Collections.emptyList() : bindingResult
                .getFieldErrors()
                .stream().map(fe -> new FieldErrorDto(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class FieldErrorDto {
        final String field;
        final String error;
    }
}
