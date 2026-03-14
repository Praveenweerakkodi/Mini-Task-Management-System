package com.taskmanager.repository;


import com.taskmanager.entity.Task;
import com.taskmanager.enums.Priority;
import com.taskmanager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {


    Page<Task> findByUserId(Long userId, Pageable pageable);

    // Filter by status only (for a specific user)
    Page<Task> findByUserIdAndStatus(Long userId, TaskStatus status, Pageable pageable);

    // Filter by priority only (for a specific user)
    Page<Task> findByUserIdAndPriority(Long userId, Priority priority, Pageable pageable);

    // Filter by both status AND priority (for a specific user)
    Page<Task> findByUserIdAndStatusAndPriority(Long userId, TaskStatus status, Priority priority, Pageable pageable);

    // ---- For ADMIN: see ALL tasks ----
    // Filter all tasks by status only
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    // Filter all tasks by priority only
    Page<Task> findByPriority(Priority priority, Pageable pageable);

    // Filter all tasks by status AND priority
    Page<Task> findByStatusAndPriority(TaskStatus status, Priority priority, Pageable pageable);

    // Custom JPQL query to filter with optional parameters
    @Query("SELECT t FROM Task t WHERE " +
           "(:userId IS NULL OR t.user.id = :userId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority)")
    Page<Task> findTasksWithFilters(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status,
            @Param("priority") Priority priority,
            Pageable pageable
    );
}
