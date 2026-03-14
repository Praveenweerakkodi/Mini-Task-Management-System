package com.taskmanager.enums;

// Enum for user roles in the system
// Enums are like a fixed list of allowed values
// In this case, a user can only be either ADMIN or USER
public enum Role {
    ADMIN,  // Admin can see and manage all tasks in the system
    USER    // Regular user can only manage their own tasks
}
