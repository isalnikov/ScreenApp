package com.digitalsignage.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String resourceName, String identifier) {
        super(
            "RESOURCE_NOT_FOUND",
            String.format("%s not found with identifier: %s", resourceName, identifier),
            HttpStatus.NOT_FOUND,
            resourceName, identifier
        );
    }

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
