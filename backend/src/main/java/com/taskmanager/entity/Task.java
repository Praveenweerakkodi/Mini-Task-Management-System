package com.taskmanager.entity;

// This class represents the 'tasks' table in our database
// Each Task belongs to one User (many tasks can belong to one user)

import com.taskmanager.enums.Priority;
import com.taskmanager.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Title of the task - cannot be empty
    @Column(nullable = false)
    private String title;

    // Description is optional (can be null), stored as TEXT for longer content
    @Column(columnDefinition = "TEXT")
    private String description;

    // Task status stored as string (TODO, IN_PROGRESS, DONE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    // Task priority stored as string (LOW, MEDIUM, HIGH)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    // The deadline for this task - optional
    private LocalDate dueDate;

    // @CreationTimestamp automatically sets this to the current time when task is created
    // We never manually set this - Hibernate handles it
    @CreationTimestamp
    @Column(updatable = false) // Once created, this should never change
    private LocalDateTime createdAt;

    // @UpdateTimestamp automatically updates this every time the task is saved/modified
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ---- Relationship to User ----
    // @ManyToOne means many Tasks belong to One User
    // @JoinColumn tells JPA to create a foreign key column called 'user_id' in the tasks table
    // This links each task to its owner (user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
