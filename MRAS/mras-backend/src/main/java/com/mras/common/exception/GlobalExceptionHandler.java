package com.mras.common.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;


import com.mras.common.dto.ErrorResponse;
import com.mras.common.dto.ErrorResponse.FieldViolation;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bean validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<FieldViolation> violations = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(err -> {
                    String field = (err instanceof FieldError fe) ? fe.getField() : err.getObjectName();
                    return new FieldViolation(field, err.getDefaultMessage());
                })
                .collect(Collectors.toList());

        ErrorResponse body = new ErrorResponse("Validation failed", req.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(), violations);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Your app-level "bad request" errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), req.getRequestURI(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), req.getRequestURI(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Auth failures (if we start using AuthenticationManager later)
    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleBadCreds(RuntimeException ex, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse("Invalid credentials", req.getRequestURI(), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<com.mras.common.dto.ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            jakarta.servlet.http.HttpServletRequest req
    ) {
        var body = new com.mras.common.dto.ErrorResponse(
                "Access Denied",
                req.getRequestURI(),
                org.springframework.http.HttpStatus.FORBIDDEN.value()
        );
        return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<com.mras.common.dto.ErrorResponse> handleAuthException(
            AuthenticationException ex,
            jakarta.servlet.http.HttpServletRequest req
    ) {
        var body = new com.mras.common.dto.ErrorResponse(
                "Unauthorized",
                req.getRequestURI(),
                org.springframework.http.HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body(body);
    }

    
    // Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        // Don't leak internals in response
        ErrorResponse body = new ErrorResponse("Internal server error", req.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        // But log it for yourself
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
