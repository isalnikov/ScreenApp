package com.digitalsignage.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends BusinessException {
    
    public AuthenticationException(String message) {
        super("AUTHENTICATION_ERROR", message, HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException() {
        super("AUTHENTICATION_ERROR", "Authentication required", HttpStatus.UNAUTHORIZED);
    }
}
