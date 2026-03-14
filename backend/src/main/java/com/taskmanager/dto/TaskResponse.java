package com.taskmanager.dto;

// TaskResponse: The data we send BACK to the client for each task
// We don't send the full Task entity because it includes the User object (security concern)
// We only send what the frontend needs

import com.taskmanager.enums.Priority;
import com.taskmanager.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Include who owns this task (useful for admin view)
    private Long userId;
    private String userName;
}
