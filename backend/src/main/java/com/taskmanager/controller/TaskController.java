package com.taskmanager.controller;



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

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            // @AuthenticationPrincipal: Spring injects the currently logged-in user
            @AuthenticationPrincipal User currentUser,

            // @RequestParam(required = false): query parameter is optional
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        // Build the sort direction
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Pageable combines pagination + sorting into one object
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<TaskResponse> tasks = taskService.getTasks(currentUser, status, priority, pageable);
        return ResponseEntity.ok(tasks);
    }

    // GET /api/tasks/{id}
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
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> markComplete(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskResponse task = taskService.markTaskComplete(id, currentUser);
        return ResponseEntity.ok(task);
    }
}
