package com.taskmanager.repository;


import com.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// @Repository marks this as a Spring-managed component
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA creates the SQL automatically based on the method name:
    Optional<User> findByEmail(String email);

    // Check if an email already exists in the database (used during registration)
    boolean existsByEmail(String email);
}
