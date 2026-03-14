// app/layout.js

import { AuthProvider } from "@/context/AuthContext";

// set <title> and <meta description> tags
export const metadata = {
  title: "Task Manager",
  description: "Mini Task Management System",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <head>
        <link
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
          rel="stylesheet"
        />
      </head>
      <body style={{ fontFamily: "'Inter', sans-serif", margin: 0, backgroundColor: "#f5f5f5" }}>

        <AuthProvider>
          {children} 
        </AuthProvider>
      </body>
    </html>
  );
}
