"use client";


import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import api from "@/lib/api";
import Navbar from "@/components/Navbar";
import ProtectedRoute from "@/components/ProtectedRoute";

export default function EditTaskPage() {
  const params = useParams();
  const taskId = params?.id;

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [status, setStatus] = useState("TODO");
  const [priority, setPriority] = useState("MEDIUM");
  const [dueDate, setDueDate] = useState("");

  // UI states
  const [loading, setLoading] = useState(true);  
  const [saving, setSaving] = useState(false);    
  const [error, setError] = useState("");

  const router = useRouter();

  // Load the existing task data when the page first loads
  useEffect(() => {
    if (!taskId) return;

    const fetchTask = async () => {
      try {
       
        const response = await api.get(`/api/tasks/${taskId}`);
        const task = response.data;
       
        setTitle(task.title);
        setDescription(task.description || "");
        setStatus(task.status);
        setPriority(task.priority);
        setDueDate(task.dueDate ? task.dueDate.split("T")[0] : "");
      } catch (err) {
        setError("Failed to load task.");
      } finally {
        setLoading(false);
      }
    };

    fetchTask();
  }, [taskId]); 

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");

    try {
      const taskData = {
        title,
        description,
        status,
        priority,
        dueDate: dueDate || null,
      };

      
      await api.put(`/api/tasks/${taskId}`, taskData);

      
      router.push("/tasks");
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        JSON.stringify(err.response?.data?.fieldErrors) ||
        "Failed to update task.";
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  
  if (loading) {
    return (
      <ProtectedRoute>
        <Navbar />
        <div style={{ textAlign: "center", paddingTop: "60px" }}>Loading task...</div>
      </ProtectedRoute>
    );
  }

  return (
    <ProtectedRoute>
      <Navbar />

      <div style={styles.container}>
        <div style={styles.card}>
          <h2 style={styles.title}>Edit Task</h2>

          {error && <div style={styles.errorBox}>{error}</div>}

          <form onSubmit={handleSubmit}>
            <div style={styles.fieldGroup}>
              <label style={styles.label}>Title *</label>
              <input
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
                style={styles.input}
              />
            </div>

            <div style={styles.fieldGroup}>
              <label style={styles.label}>Description</label>
              <textarea
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                rows={3}
                style={{ ...styles.input, resize: "vertical" }}
              />
            </div>

            <div style={styles.row}>
              <div style={{ flex: 1 }}>
                <label style={styles.label}>Status *</label>
                <select value={status} onChange={(e) => setStatus(e.target.value)} style={styles.input}>
                  <option value="TODO">TODO</option>
                  <option value="IN_PROGRESS">IN PROGRESS</option>
                  <option value="DONE">DONE</option>
                </select>
              </div>
              <div style={{ flex: 1 }}>
                <label style={styles.label}>Priority *</label>
                <select value={priority} onChange={(e) => setPriority(e.target.value)} style={styles.input}>
                  <option value="LOW">LOW</option>
                  <option value="MEDIUM">MEDIUM</option>
                  <option value="HIGH">HIGH</option>
                </select>
              </div>
            </div>

            <div style={styles.fieldGroup}>
              <label style={styles.label}>Due Date (optional)</label>
              <input
                type="date"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
                style={styles.input}
              />
            </div>

            <div style={styles.buttonGroup}>
              <button
                type="button"
                onClick={() => router.push("/tasks")}
                style={styles.cancelBtn}
              >
                Cancel
              </button>
              <button type="submit" disabled={saving} style={styles.submitBtn}>
                {saving ? "Saving..." : "Save Changes"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </ProtectedRoute>
  );
}

const styles = {
  container: { maxWidth: "600px", margin: "0 auto", padding: "24px" },
  card: {
    backgroundColor: "white",
    padding: "32px",
    borderRadius: "12px",
    boxShadow: "0 2px 8px rgba(0,0,0,0.08)",
  },
  title: { color: "#2c3e50", marginTop: 0, marginBottom: "24px", fontSize: "20px" },
  errorBox: {
    backgroundColor: "#fdecea",
    color: "#c0392b",
    padding: "10px 14px",
    borderRadius: "6px",
    marginBottom: "16px",
    fontSize: "14px",
    border: "1px solid #e74c3c",
  },
  fieldGroup: { marginBottom: "16px" },
  row: { display: "flex", gap: "16px", marginBottom: "16px" },
  label: { display: "block", marginBottom: "6px", fontWeight: "500", color: "#2c3e50", fontSize: "14px" },
  input: {
    width: "100%",
    padding: "10px 12px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    fontSize: "14px",
    boxSizing: "border-box",
    fontFamily: "inherit",
  },
  buttonGroup: { display: "flex", gap: "12px", justifyContent: "flex-end", marginTop: "24px" },
  cancelBtn: {
    padding: "10px 20px",
    border: "1px solid #ddd",
    backgroundColor: "white",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
  submitBtn: {
    padding: "10px 20px",
    backgroundColor: "#3498db",
    color: "white",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
};
