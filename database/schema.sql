-- =========================================
-- Database Schema for Mini Task Management System
-- Run this in MySQL to create the database and tables
-- OR let Hibernate auto-create them (spring.jpa.hibernate.ddl-auto=update)
-- =========================================

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS taskdb;

-- Step 2: Use the database
USE taskdb;

-- =========================================
-- Table: users
-- Stores all registered user accounts
-- =========================================
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,  -- Auto-increments: 1, 2, 3...
    name       VARCHAR(255) NOT NULL,              -- User's display name
    email      VARCHAR(255) NOT NULL UNIQUE,       -- Must be unique (login identifier)
    password   VARCHAR(255) NOT NULL,              -- BCrypt hashed password (not plain text!)
    role       ENUM('ADMIN', 'USER') NOT NULL      -- Role for access control
);

-- =========================================
-- Table: tasks
-- Stores all tasks, linked to a user via user_id (foreign key)
-- =========================================
CREATE TABLE IF NOT EXISTS tasks (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,                              -- TEXT allows longer descriptions
    status      ENUM('TODO', 'IN_PROGRESS', 'DONE') NOT NULL DEFAULT 'TODO',
    priority    ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM',
    due_date    DATE,                              -- Optional deadline
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,   -- Set automatically on INSERT
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Auto-updated on UPDATE
    user_id     BIGINT NOT NULL,                  -- Foreign key → links task to a user

    -- Foreign key constraint: user_id must match an id in the users table
    -- ON DELETE CASCADE: if user is deleted, their tasks are also deleted
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================================
-- Indexes for better query performance
-- =========================================
-- When we filter tasks by user_id, this index speeds up the query
CREATE INDEX idx_tasks_user_id ON tasks(user_id);

-- When we filter by status, this index speeds up the query
CREATE INDEX idx_tasks_status ON tasks(status);

-- When we filter by priority, this index speeds up the query
CREATE INDEX idx_tasks_priority ON tasks(priority);

-- =========================================
-- Sample data for testing (optional)
-- =========================================
-- Insert a test admin user (password: "admin123" hashed with BCrypt)
-- INSERT INTO users (name, email, password, role)
-- VALUES ('Admin User', 'admin@test.com', '$2a$10$YourBCryptHashHere', 'ADMIN');

-- Insert a test regular user (password: "user123" hashed with BCrypt)
-- INSERT INTO users (name, email, password, role)
-- VALUES ('Regular User', 'user@test.com', '$2a$10$YourBCryptHashHere', 'USER');
