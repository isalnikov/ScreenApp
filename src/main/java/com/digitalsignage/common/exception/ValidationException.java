package com.digitalsignage.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a validation constraint is violated.
 */
public class ValidationException extends BusinessException {
    
    public ValidationException(String field, String message) {
        super(
            "VALIDATION_ERROR",
            String.format("Validation failed for field '%s': %s", field, message),
            HttpStatus.BAD_REQUEST,
            field, message
        );
    }

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST);
    }
}
