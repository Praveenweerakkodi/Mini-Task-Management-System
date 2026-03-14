package com.taskmanager.dto;

// TaskRequest: Data the client sends when creating or updating a task

import com.taskmanager.enums.Priority;
import com.taskmanager.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    // Title is required
    @NotBlank(message = "Task title is required")
    private String title;

    // Description is optional - no validation needed
    private String description;

    // Status must be provided - can't have a task with no state
    @NotNull(message = "Status is required (TODO, IN_PROGRESS, DONE)")
    private TaskStatus status;

    // Priority must be provided
    @NotNull(message = "Priority is required (LOW, MEDIUM, HIGH)")
    private Priority priority;

    // Due date is optional - user may not know the deadline yet
    private LocalDate dueDate;
}
