package com.taskmanager.exception;

// Custom exception class for when a resource (user or task) is not found
// We throw this exception in our services when something doesn't exist in the database
// The GlobalExceptionHandler then catches it and returns a 404 HTTP response

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus tells Spring: "if this exception is not caught, return HTTP 404"
// But we handle it in GlobalExceptionHandler, so this is a backup
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Constructor: creates the exception with a descriptive message
    // Example: throw new ResourceNotFoundException("Task not found with id: 5")
    public ResourceNotFoundException(String message) {
        super(message); // Pass message to parent RuntimeException
    }
}
