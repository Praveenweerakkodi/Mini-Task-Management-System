package com.taskmanager.enums;

// Enum for task status - represents the current state of a task
// A task must always be in one of these three states
public enum TaskStatus {
    TODO,        // Task has been created but not started yet
    IN_PROGRESS, // Task is currently being worked on
    DONE         // Task has been completed
}
