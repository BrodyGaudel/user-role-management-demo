package org.mounanga.userservice.exception;

import java.util.List;

public record FieldErrorResponse(Integer code,
                                 String message,
                                 String description,
                                 List<FieldError> fieldErrors) {
}
