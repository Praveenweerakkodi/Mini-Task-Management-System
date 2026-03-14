package com.taskmanager.dto;

// AuthResponse: The data we send BACK to the user after login or register
// Contains the JWT token and basic user information

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    // The JWT token the frontend will store and send in future requests
    private String token;

    // Basic user info so the frontend can show the user's name and role
    private Long userId;
    private String name;
    private String email;
    private String role;  // "ADMIN" or "USER"
}
