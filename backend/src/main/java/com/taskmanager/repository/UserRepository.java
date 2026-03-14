package com.taskmanager.repository;

// Repository is the layer that talks to the database
// Spring Data JPA auto-implements common queries (findAll, save, delete, etc.)
// We only need to write custom queries here

import com.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// @Repository marks this as a Spring-managed component
// JpaRepository<User, Long> means: manage 'User' entities where the ID type is 'Long'
// Spring automatically provides: save(), findById(), findAll(), delete(), etc.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA creates the SQL automatically based on the method name:
    // "findByEmail" → SELECT * FROM users WHERE email = ?
    // Returns Optional<User> - this safely handles the case where no user is found
    Optional<User> findByEmail(String email);

    // Check if an email already exists in the database (used during registration)
    // "existsByEmail" → SELECT COUNT(*) FROM users WHERE email = ? > 0
    boolean existsByEmail(String email);
}
