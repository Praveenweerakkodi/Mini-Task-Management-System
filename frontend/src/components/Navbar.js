"use client";

import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";

export default function Navbar() {

  const { user, logout } = useAuth();
  const router = useRouter();

  return (
    <nav style={styles.navbar}>
      <div
        style={styles.logo}
        onClick={() => router.push("/tasks")}
      >
        📋 Task Manager
      </div>

      {/* Right side */}
      <div style={styles.rightSection}>
        {user && (
          <>
            {/* Show user name and role badge */}
            <span style={styles.userInfo}>
              Hello, <strong>{user.name}</strong>
            </span>

            {/* Show role badge */}
            <span
              style={{
                ...styles.roleBadge,
                backgroundColor: user.role === "ADMIN" ? "#e74c3c" : "#3498db",
              }}
            >
              {user.role}
            </span>

            <button
              style={styles.createBtn}
              onClick={() => router.push("/tasks/create")}
            >
              + New Task
            </button>

            {/* Logout button */}
            <button style={styles.logoutBtn} onClick={logout}>
              Logout
            </button>
          </>
        )}
      </div>
    </nav>
  );
}


const styles = {
  navbar: {
    backgroundColor: "#2c3e50",
    color: "white",
    padding: "12px 24px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    boxShadow: "0 2px 4px rgba(0,0,0,0.2)",
  },
  logo: {
    fontSize: "20px",
    fontWeight: "bold",
    cursor: "pointer",
  },
  rightSection: {
    display: "flex",
    alignItems: "center",
    gap: "16px",
  },
  userInfo: {
    fontSize: "14px",
  },
  roleBadge: {
    padding: "3px 10px",
    borderRadius: "12px",
    fontSize: "12px",
    fontWeight: "bold",
    color: "white",
  },
  createBtn: {
    backgroundColor: "#27ae60",
    color: "white",
    border: "none",
    padding: "8px 14px",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
  logoutBtn: {
    backgroundColor: "transparent",
    color: "white",
    border: "1px solid white",
    padding: "8px 14px",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
};
