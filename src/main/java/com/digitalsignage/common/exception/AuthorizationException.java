package com.digitalsignage.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authorization fails (insufficient permissions).
 */
public class AuthorizationException extends BusinessException {
    
    public AuthorizationException(String message) {
        super("AUTHORIZATION_ERROR", message, HttpStatus.FORBIDDEN);
    }

    public AuthorizationException() {
        super("AUTHORIZATION_ERROR", "Insufficient permissions", HttpStatus.FORBIDDEN);
    }
}
