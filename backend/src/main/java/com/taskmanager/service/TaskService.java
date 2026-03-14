package com.taskmanager.service;

// TaskService: All the business logic for tasks
// Create, Read, Update, Delete tasks
// Filter by status/priority, paginate, sort
// Role-based logic: ADMIN sees all, USER sees own tasks

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.enums.Priority;
import com.taskmanager.enums.Role;
import com.taskmanager.enums.TaskStatus;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // ---- Convert Task entity to TaskResponse DTO ----
    // We never return raw entities to the frontend - use DTOs to control what's exposed
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .userId(task.getUser().getId())
                .userName(task.getUser().getName())
                .build();
    }

    // ---- Get Tasks (with filter, sort, pagination) ----
    // currentUser: the logged-in user (from JWT token)
    // status, priority: optional filters (null means "no filter")
    // pageable: contains page number, page size, and sort direction

    public Page<TaskResponse> getTasks(User currentUser, TaskStatus status, Priority priority, Pageable pageable) {

        // Determine userId filter:
        // - If ADMIN: userId = null (no filter → see all tasks)
        // - If USER: userId = currentUser.getId() (only see own tasks)
        Long userId = currentUser.getRole() == Role.ADMIN ? null : currentUser.getId();

        // Use the flexible JPQL query from TaskRepository
        // It handles all combinations: no filter, status only, priority only, both
        Page<Task> tasks = taskRepository.findTasksWithFilters(userId, status, priority, pageable);

        // Convert each Task entity to TaskResponse DTO
        return tasks.map(this::mapToResponse);
    }

    // ---- Get a single task by ID ----
    public TaskResponse getTaskById(Long taskId, User currentUser) {
        // Find the task, throw 404 if not found
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        // Security check: USER can only see their OWN tasks
        // ADMIN can see any task
        if (currentUser.getRole() != Role.ADMIN && !task.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
            // We throw 404 instead of 403 to avoid telling the user that the task exists
        }

        return mapToResponse(task);
    }

    // ---- Create a new task ----
    public TaskResponse createTask(TaskRequest request, User currentUser) {
        // Build the Task entity from the request DTO
        Task newTask = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .user(currentUser) // Assign the task to the currently logged-in user
                // createdAt and updatedAt are automatically set by @CreationTimestamp and @UpdateTimestamp
                .build();

        // Save to database
        Task savedTask = taskRepository.save(newTask);
        return mapToResponse(savedTask);
    }

    // ---- Update an existing task ----
    public TaskResponse updateTask(Long taskId, TaskRequest request, User currentUser) {
        // Find the existing task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        // Only the owner or an ADMIN can update the task
        if (currentUser.getRole() != Role.ADMIN && !task.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }

        // Update only the fields from the request
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        // updatedAt is automatically updated by @UpdateTimestamp when we save

        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    // ---- Delete a task ----
    public void deleteTask(Long taskId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        // Only the owner or ADMIN can delete
        if (currentUser.getRole() != Role.ADMIN && !task.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }

        taskRepository.delete(task);
    }

    // ---- Mark task as completed (DONE) ----
    // A convenience endpoint just to mark a task as DONE without updating everything
    public TaskResponse markTaskComplete(Long taskId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        if (currentUser.getRole() != Role.ADMIN && !task.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }

        // Just change the status to DONE
        task.setStatus(TaskStatus.DONE);
        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }
}
