package com.taskmanager.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    // The JWT token the frontend will store and send in future requests
    private String token;


    private Long userId;
    private String name;
    private String email;
    private String role;
}
