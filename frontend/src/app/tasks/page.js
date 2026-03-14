"use client";

import { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import api from "@/lib/api";
import { useAuth } from "@/context/AuthContext";
import Navbar from "@/components/Navbar";
import ProtectedRoute from "@/components/ProtectedRoute";
import TaskCard from "@/components/TaskCard";

export default function TasksPage() {
  
  const [tasks, setTasks] = useState(null); 


  const [statusFilter, setStatusFilter] = useState("");  
  const [priorityFilter, setPriorityFilter] = useState("");

  const [sortBy, setSortBy] = useState("createdAt"); 
  const [sortDir, setSortDir] = useState("desc");    


  const [page, setPage] = useState(0);   
  const [size] = useState(5);           

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const { user } = useAuth();
  const router = useRouter();

  
  const fetchTasks = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
     
      const params = {
        page,
        size,
        sortBy,
        sortDir,
      };

      if (statusFilter) params.status = statusFilter;
      if (priorityFilter) params.priority = priorityFilter;


      const response = await api.get("/api/tasks", { params });


      setTasks(response.data);
    } catch (err) {
      setError("Failed to load tasks. Please try again.");
    } finally {
      setLoading(false);
    }
  }, [page, size, statusFilter, priorityFilter, sortBy, sortDir]);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);


  const handleFilterChange = (setter) => (e) => {
    setter(e.target.value);
    setPage(0);
  };


  const handleDelete = async (taskId) => {
    if (!confirm("Are you sure you want to delete this task?")) return;

    try {
      await api.delete(`/api/tasks/${taskId}`);
      fetchTasks(); 
    } catch (err) {
      alert("Failed to delete task.");
    }
  };

  const handleComplete = async (taskId) => {
    try {
      await api.patch(`/api/tasks/${taskId}/complete`);
      fetchTasks(); 
    } catch (err) {
      alert("Failed to update task.");
    }
  };

  return (
    <ProtectedRoute>
      <Navbar />

      <div style={styles.container}>
        {/* Page Header */}
        <div style={styles.header}>
          <h2 style={styles.heading}>
            {user?.role === "ADMIN" ? "All Tasks (Admin View)" : "My Tasks"}
          </h2>
        </div>

        <div style={styles.controls}>
          <select
            value={statusFilter}
            onChange={handleFilterChange(setStatusFilter)}
            style={styles.select}
          >
            <option value="">All Statuses</option>
            <option value="TODO">TODO</option>
            <option value="IN_PROGRESS">IN PROGRESS</option>
            <option value="DONE">DONE</option>
          </select>

          <select
            value={priorityFilter}
            onChange={handleFilterChange(setPriorityFilter)}
            style={styles.select}
          >
            <option value="">All Priorities</option>
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
          </select>

          {/* Sort field */}
          <select
            value={sortBy}
            onChange={(e) => { setSortBy(e.target.value); setPage(0); }}
            style={styles.select}
          >
            <option value="createdAt">Sort by: Created</option>
            <option value="dueDate">Sort by: Due Date</option>
            <option value="priority">Sort by: Priority</option>
          </select>

          <select
            value={sortDir}
            onChange={(e) => { setSortDir(e.target.value); setPage(0); }}
            style={styles.select}
          >
            <option value="desc">Newest First</option>
            <option value="asc">Oldest First</option>
          </select>
        </div>

        {loading && <p style={styles.info}>Loading tasks...</p>}
        {error && <p style={styles.errorText}>{error}</p>}

        {/* Task list */}
        {!loading && tasks && (
          <>
            {tasks.content.length === 0 ? (
         
              <div style={styles.emptyState}>
                <p>No tasks found. Create your first task!</p>
              </div>
            ) : (
  
              <div>
                {tasks.content.map((task) => (
                  <TaskCard
                    key={task.id} 
                    task={task}
                    currentUser={user}
                    onDelete={handleDelete}
                    onComplete={handleComplete}
                    onEdit={(id) => router.push(`/tasks/${id}/edit`)}
                  />
                ))}
              </div>
            )}

           
            <div style={styles.pagination}>
              
              <button
                onClick={() => setPage(page - 1)}
                disabled={page === 0}
                style={{
                  ...styles.pageBtn,
                  opacity: page === 0 ? 0.4 : 1,
                }}
              >
                ← Previous
              </button>

              
              <span style={styles.pageInfo}>
                Page {tasks.number + 1} of {tasks.totalPages}
                {" "}({tasks.totalElements} total)
              </span>
         
              <button
                onClick={() => setPage(page + 1)}
                disabled={page >= tasks.totalPages - 1}
                style={{
                  ...styles.pageBtn,
                  opacity: page >= tasks.totalPages - 1 ? 0.4 : 1,
                }}
              >
                Next →
              </button>
            </div>
          </>
        )}
      </div>
    </ProtectedRoute>
  );
}

const styles = {
  container: {
    maxWidth: "900px",
    margin: "0 auto",
    padding: "24px",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "16px",
  },
  heading: {
    color: "#2c3e50",
    margin: 0,
    fontSize: "22px",
  },
  controls: {
    display: "flex",
    gap: "10px",
    marginBottom: "24px",
    flexWrap: "wrap",
  },
  select: {
    padding: "8px 12px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    fontSize: "14px",
    backgroundColor: "white",
    cursor: "pointer",
  },
  info: {
    textAlign: "center",
    color: "#7f8c8d",
  },
  errorText: {
    color: "#e74c3c",
    textAlign: "center",
  },
  emptyState: {
    textAlign: "center",
    padding: "60px",
    color: "#7f8c8d",
    backgroundColor: "white",
    borderRadius: "8px",
    border: "2px dashed #ddd",
  },
  pagination: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    gap: "20px",
    marginTop: "24px",
  },
  pageBtn: {
    padding: "8px 16px",
    backgroundColor: "#2c3e50",
    color: "white",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
  pageInfo: {
    color: "#7f8c8d",
    fontSize: "14px",
  },
};
