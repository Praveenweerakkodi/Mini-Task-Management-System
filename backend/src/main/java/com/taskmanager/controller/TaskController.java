package com.taskmanager.controller;

// TaskController: Handles all task-related endpoints
// All endpoints REQUIRE a valid JWT token (configured in SecurityConfig)
//
// Endpoints:
//   GET    /api/tasks           → Get all tasks (with optional filters, pagination, sorting)
//   GET    /api/tasks/{id}      → Get single task
//   POST   /api/tasks           → Create new task
//   PUT    /api/tasks/{id}      → Update task
//   DELETE /api/tasks/{id}      → Delete task
//   PATCH  /api/tasks/{id}/complete → Mark task as done

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.entity.User;
import com.taskmanager.enums.Priority;
import com.taskmanager.enums.TaskStatus;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // GET /api/tasks
    // Query parameters (all optional):
    //   ?status=TODO          → filter by status
    //   ?priority=HIGH        → filter by priority
    //   ?page=0               → page number (0-indexed, default 0)
    //   ?size=10              → items per page (default 10)
    //   ?sortBy=dueDate       → sort field (dueDate or priority)
    //   ?sortDir=asc          → sort direction (asc or desc)
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            // @AuthenticationPrincipal: Spring injects the currently logged-in user
            // This is set by JwtAuthFilter when it validates the JWT token
            @AuthenticationPrincipal User currentUser,

            // @RequestParam(required = false): query parameter is optional
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0") int page,        // Default: first page
            @RequestParam(defaultValue = "10") int size,       // Default: 10 items per page
            @RequestParam(defaultValue = "createdAt") String sortBy,  // Default: sort by creation date
            @RequestParam(defaultValue = "desc") String sortDir        // Default: newest first
    ) {
        // Build the sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Pageable combines pagination + sorting into one object
        // PageRequest.of(page, size, sort) creates this object
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<TaskResponse> tasks = taskService.getTasks(currentUser, status, priority, pageable);
        return ResponseEntity.ok(tasks);
    }

    // GET /api/tasks/{id}
    // @PathVariable: gets the {id} value from the URL
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskResponse task = taskService.getTaskById(id, currentUser);
        return ResponseEntity.ok(task);
    }

    // POST /api/tasks
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskResponse task = taskService.createTask(request, currentUser);
        // 201 CREATED: a new task was created
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    // PUT /api/tasks/{id}
    // @Valid triggers validation on TaskRequest fields
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskResponse task = taskService.updateTask(id, request, currentUser);
        return ResponseEntity.ok(task);
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        taskService.deleteTask(id, currentUser);
        // 204 NO CONTENT: operation was successful but nothing to return
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/tasks/{id}/complete
    // PATCH is used for partial updates (just changing the status to DONE)
    // vs PUT which replaces the entire resource
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> markComplete(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskResponse task = taskService.markTaskComplete(id, currentUser);
        return ResponseEntity.ok(task);
    }
}
