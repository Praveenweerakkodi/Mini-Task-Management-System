package com.taskmanager.dto;

// DTO stands for Data Transfer Object
// These classes define the shape of data coming IN from the client (request body)
// and going OUT to the client (response body)
// We use DTOs instead of sending entity classes directly - better security and control

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// RegisterRequest: The data a user sends when they sign up
@Data // Lombok: generates getters, setters, etc.
public class RegisterRequest {

    // @NotBlank means the field cannot be null or empty string
    @NotBlank(message = "Name is required")
    private String name;

    // @Email checks that the value looks like a valid email address
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    // @Size ensures password is at least 6 characters
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // Role is optional - defaults to USER if not provided (handled in service)
    // "ADMIN" or "USER"
    private String role;
}
