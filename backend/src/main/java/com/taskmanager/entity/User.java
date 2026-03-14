package com.taskmanager.entity;

// This class represents the 'users' table in our database
// JPA (Java Persistence API) maps this class to a database table automatically

import com.taskmanager.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// @Entity tells JPA "this class is a database table"
@Entity
// @Table tells JPA the exact table name to use in the database
@Table(name = "users")
// Lombok annotations - these auto-generate boilerplate code:
@Data           // Generates getters, setters, equals, hashCode, toString
@Builder        // Lets us build objects like: User.builder().name("John").build()
@NoArgsConstructor   // Generates an empty constructor: new User()
@AllArgsConstructor  // Generates constructor with all fields
// UserDetails is a Spring Security interface - implementing it allows Spring Security
// to use our User class directly for authentication
public class User implements UserDetails {

    // @Id marks this as the primary key
    // @GeneratedValue means the database auto-generates this value (auto-increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false) means this field CANNOT be null in the database
    @Column(nullable = false)
    private String name;

    // @Column(unique = true) means no two users can have the same email
    @Column(unique = true, nullable = false)
    private String email;

    // This stores the hashed password (we never store plain text passwords!)
    @Column(nullable = false)
    private String password;

    // @Enumerated(EnumType.STRING) stores the enum as text ("ADMIN" or "USER")
    // instead of a number (0 or 1) - makes the DB more readable
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ---- UserDetails Interface Methods ----
    // Spring Security calls these methods to check permissions

    // getAuthorities() returns what roles/permissions the user has
    // Spring Security uses this to enforce role-based access
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We prefix the role with "ROLE_" because Spring Security expects this format
        // e.g., "ROLE_ADMIN" or "ROLE_USER"
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // Spring Security needs a username to identify users - we use email
    @Override
    public String getUsername() {
        return email;
    }

    // These are from UserDetails interface - returning true means the account is valid
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
