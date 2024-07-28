package org.mounanga.userservice.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class FieldValidationException extends RuntimeException {

    private final List<FieldError> fieldErrors;

    public FieldValidationException(String message, List<FieldError> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }
}
