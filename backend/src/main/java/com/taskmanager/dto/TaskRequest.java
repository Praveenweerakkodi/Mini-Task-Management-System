package com.taskmanager.dto;


import com.taskmanager.enums.Priority;
import com.taskmanager.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    @NotNull(message = "Status is required (TODO, IN_PROGRESS, DONE)")
    private TaskStatus status;

    @NotNull(message = "Priority is required (LOW, MEDIUM, HIGH)")
    private Priority priority;

    private LocalDate dueDate;
}
