package com.taskmanager.exception;

// GlobalExceptionHandler: One place to handle ALL exceptions across the entire app
// Instead of writing try-catch blocks in every controller,
// we define error handling here and Spring automatically uses it
//
// This is also called "centralized exception handling" - a clean architecture practice

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
// It means: "this class intercepts exceptions from all controllers and returns JSON responses"
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- Helper method to create a consistent error response map ----
    // All error responses will have the same structure: status, message, timestamp
    private Map<String, Object> buildErrorBody(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());     // e.g., 404
        body.put("error", status.getReasonPhrase()); // e.g., "Not Found"
        body.put("message", message);           // e.g., "Task not found with id: 5"
        return body;
    }

    // ---- Handle Resource Not Found (404) ----
    // Called when code throws: throw new ResourceNotFoundException("...")
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // HTTP 404
                .body(buildErrorBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // ---- Handle Validation Errors (400) ----
    // Called when @Valid fails in a controller (e.g., missing required field)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Collect all field errors into a map: {fieldName: errorMessage}
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = buildErrorBody(HttpStatus.BAD_REQUEST, "Validation failed");
        body.put("fieldErrors", fieldErrors); // Add specific field errors

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ---- Handle Bad Credentials (401) ----
    // Called when login fails (wrong email or password)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // HTTP 401
                .body(buildErrorBody(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    }

    // ---- Handle Illegal Arguments (400) ----
    // Called when something like "invalid role value" is passed
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // HTTP 400
                .body(buildErrorBody(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    // ---- Handle Any Other Unexpected Errors (500) ----
    // Catches everything else - safety net
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500
                .body(buildErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"));
    }
}
