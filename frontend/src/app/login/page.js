"use client";

import { useState } from "react";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";

export default function LoginPage() {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const { login } = useAuth(); 
  const router = useRouter();

  const handleSubmit = async (e) => {
    e.preventDefault(); 

    setLoading(true); 
    setError("");     

    try {

      await login(email, password);


      router.push("/tasks");
    } catch (err) {

      const msg = err.response?.data?.message || "Login failed. Please try again.";
      setError(msg);
    } finally {
      setLoading(false); 
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        {/* Header */}
        <h1 style={styles.title}>📋 Task Manager</h1>
        <h2 style={styles.subtitle}>Login to your account</h2>

        {/* Error Message */}
        {error && <div style={styles.errorBox}>{error}</div>}

        {/* Login Form */}
        <form onSubmit={handleSubmit} suppressHydrationWarning>
          {/* Email field */}
          <div style={styles.fieldGroup}>
            <label style={styles.label}>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)} 
              placeholder="Enter your email"
              required 
              style={styles.input}
              suppressHydrationWarning
            />
          </div>

          {/* Password field */}
          <div style={styles.fieldGroup}>
            <label style={styles.label}>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              required
              style={styles.input}
              suppressHydrationWarning
            />
          </div>

          {/* Submit button */}
          <button type="submit" disabled={loading} style={styles.button} suppressHydrationWarning>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        {/* Link to register page */}
        <p style={styles.switchText}>
          Don't have an account?{" "}
          <a href="/register" style={styles.link}>Register here</a>
        </p>
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#f0f2f5",
  },
  card: {
    backgroundColor: "white",
    padding: "40px",
    borderRadius: "12px",
    boxShadow: "0 2px 12px rgba(0,0,0,0.1)",
    width: "100%",
    maxWidth: "400px",
  },
  title: {
    textAlign: "center",
    marginBottom: "4px",
    color: "#2c3e50",
    fontSize: "24px",
  },
  subtitle: {
    textAlign: "center",
    color: "#7f8c8d",
    fontWeight: "400",
    fontSize: "16px",
    marginBottom: "24px",
    marginTop: "0",
  },
  errorBox: {
    backgroundColor: "#fdecea",
    color: "#c0392b",
    padding: "10px 14px",
    borderRadius: "6px",
    marginBottom: "16px",
    fontSize: "14px",
    border: "1px solid #e74c3c",
  },
  fieldGroup: {
    marginBottom: "16px",
  },
  label: {
    display: "block",
    marginBottom: "6px",
    fontWeight: "500",
    color: "#2c3e50",
    fontSize: "14px",
  },
  input: {
    width: "100%",
    padding: "10px 12px",
    border: "1px solid #ddd",
    borderRadius: "6px",
    fontSize: "14px",
    boxSizing: "border-box",
    outline: "none",
  },
  button: {
    width: "100%",
    padding: "12px",
    backgroundColor: "#2c3e50",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "16px",
    cursor: "pointer",
    marginTop: "8px",
  },
  switchText: {
    textAlign: "center",
    marginTop: "20px",
    fontSize: "14px",
    color: "#7f8c8d",
  },
  link: {
    color: "#3498db",
    textDecoration: "none",
    fontWeight: "500",
  },
};
