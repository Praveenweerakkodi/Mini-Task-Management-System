"use client";


export default function TaskCard({ task, currentUser, onDelete, onComplete, onEdit }) {

  const formatDate = (dateStr) => {
    if (!dateStr) return "No due date";
    return new Date(dateStr).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  const statusColors = {
    TODO: { bg: "#eaf4fb", text: "#2980b9", border: "#aed6f1" },
    IN_PROGRESS: { bg: "#fef9e7", text: "#d68910", border: "#f8c471" },
    DONE: { bg: "#eafaf1", text: "#27ae60", border: "#a9dfbf" },
  };

  const priorityColors = {
    LOW: { bg: "#f0f0f0", text: "#7f8c8d" },
    MEDIUM: { bg: "#fef9e7", text: "#d68910" },
    HIGH: { bg: "#fdecea", text: "#c0392b" },
  };

  const statusStyle = statusColors[task.status] || statusColors.TODO;
  const priorityStyle = priorityColors[task.priority] || priorityColors.LOW;

  const isOwnerOrAdmin =
    currentUser?.role === "ADMIN" || currentUser?.id === task.userId;

  return (
    <div style={styles.card}>
      {/* Top row: */}
      <div style={styles.cardHeader}>
        <h3 style={styles.taskTitle}>{task.title}</h3>

        <div style={styles.badgeGroup}>
          {/* Status badge */}
          <span
            style={{
              ...styles.badge,
              backgroundColor: statusStyle.bg,
              color: statusStyle.text,
              border: `1px solid ${statusStyle.border}`,
            }}
          >
            {task.status.replace("_", " ")} 
          </span>

          {/* Priority badge */}
          <span
            style={{
              ...styles.badge,
              backgroundColor: priorityStyle.bg,
              color: priorityStyle.text,
            }}
          >
            ⚡ {task.priority}
          </span>
        </div>
      </div>

      {/* Task description */}
      {task.description && (
        <p style={styles.description}>{task.description}</p>
      )}

      {/* Task metadata */}
      <div style={styles.meta}>
        <span>📅 Due: {formatDate(task.dueDate)}</span>

        {/* Admin sees who owns this task */}
        {currentUser?.role === "ADMIN" && (
          <span>👤 {task.userName}</span>
        )}

        <span style={styles.dateText}>
          Created: {new Date(task.createdAt).toLocaleDateString()}
        </span>
      </div>

      {/* Action buttons */}
      {isOwnerOrAdmin && (
        <div style={styles.actions}>
          {/* Edit button */}
          <button
            onClick={() => onEdit(task.id)}
            style={styles.editBtn}
          >
            Edit
          </button>

          {/* Mark Complete button */}
          {task.status !== "DONE" && (
            <button
              onClick={() => onComplete(task.id)}
              style={styles.completeBtn}
            >
              ✓ Mark Done
            </button>
          )}

          {/* Delete button */}
          <button
            onClick={() => onDelete(task.id)}
            style={styles.deleteBtn}
          >
            Delete
          </button>
        </div>
      )}
    </div>
  );
}

const styles = {
  card: {
    backgroundColor: "white",
    border: "1px solid #e0e0e0",
    borderRadius: "8px",
    padding: "20px",
    marginBottom: "12px",
    boxShadow: "0 1px 3px rgba(0,0,0,0.05)",
  },
  cardHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
    marginBottom: "8px",
  },
  taskTitle: {
    margin: 0,
    fontSize: "16px",
    fontWeight: "600",
    color: "#2c3e50",
  },
  badgeGroup: {
    display: "flex",
    gap: "8px",
    flexWrap: "wrap",
  },
  badge: {
    padding: "3px 10px",
    borderRadius: "12px",
    fontSize: "12px",
    fontWeight: "500",
    whiteSpace: "nowrap",
  },
  description: {
    color: "#7f8c8d",
    fontSize: "14px",
    margin: "8px 0",
    lineHeight: "1.5",
  },
  meta: {
    display: "flex",
    gap: "20px",
    fontSize: "13px",
    color: "#95a5a6",
    marginTop: "10px",
    flexWrap: "wrap",
  },
  dateText: {
    marginLeft: "auto",
  },
  actions: {
    display: "flex",
    gap: "8px",
    marginTop: "14px",
    paddingTop: "14px",
    borderTop: "1px solid #f0f0f0",
  },
  editBtn: {
    padding: "6px 14px",
    border: "1px solid #3498db",
    color: "#3498db",
    backgroundColor: "transparent",
    borderRadius: "5px",
    cursor: "pointer",
    fontSize: "13px",
  },
  completeBtn: {
    padding: "6px 14px",
    border: "none",
    color: "white",
    backgroundColor: "#27ae60",
    borderRadius: "5px",
    cursor: "pointer",
    fontSize: "13px",
  },
  deleteBtn: {
    padding: "6px 14px",
    border: "none",
    color: "white",
    backgroundColor: "#e74c3c",
    borderRadius: "5px",
    cursor: "pointer",
    fontSize: "13px",
    marginLeft: "auto",
  },
};
