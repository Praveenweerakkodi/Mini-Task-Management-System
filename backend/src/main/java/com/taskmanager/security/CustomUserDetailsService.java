package com.taskmanager.security;

// CustomUserDetailsService: Spring Security calls this to load user info from our database
// When a request comes in with a JWT token, Spring needs to find the matching user
// in the database to verify their identity. This service does that.

import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// @RequiredArgsConstructor: Lombok generates a constructor for all 'final' fields
// This is used for Dependency Injection (Spring automatically provides UserRepository)
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // Spring will inject an instance of UserRepository here
    private final UserRepository userRepository;

    // Spring Security calls this method when it needs to find a user
    // It passes the "username" (which for us is the email address)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Look up the user by email
        // If not found, throw UsernameNotFoundException (Spring Security handles this)
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
