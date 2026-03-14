"use client";

import { createContext, useContext, useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import api from "../lib/api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const router = useRouter();

  useEffect(() => {
    const savedUser = localStorage.getItem("user");
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }

    setLoading(false);
  }, []);

  //  Login function

  const login = async (email, password) => {
    const response = await api.post("/api/auth/login", { email, password });
    const data = response.data;

    localStorage.setItem("token", data.token);

    const userData = {
      id: data.userId,
      name: data.name,
      email: data.email,
      role: data.role,
    };
    localStorage.setItem("user", JSON.stringify(userData));
    setUser(userData);

    return data;
  };

  //  Register function
  const register = async (name, email, password, role = "USER") => {
    const response = await api.post("/api/auth/register", {
      name,
      email,
      password,
      role,
    });
    const data = response.data;

    localStorage.setItem("token", data.token);
    const userData = {
      id: data.userId,
      name: data.name,
      email: data.email,
      role: data.role,
    };
    localStorage.setItem("user", JSON.stringify(userData));
    setUser(userData);

    return data;
  };

  //  Logout function
  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    setUser(null);

    router.push("/login");
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return context;
}
