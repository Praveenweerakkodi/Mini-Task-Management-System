# Mini Task Management System

A full-stack web application built with **Next.js** (frontend) and **Spring Boot** (backend) using **MySQL** as the database. Supports user registration, login with JWT authentication, role-based access control (ADMIN/USER), and full task management.

---

## Project Structure

```
/
├── backend/     ← Spring Boot backend (Java)
│   ├── src/main/java/com/taskmanager/
│   │   ├── config/              ← Security configuration
│   │   ├── controller/          ← REST API endpoints
│   │   ├── dto/                 ← Request/Response data objects
│   │   ├── entity/              ← Database models (User, Task)
│   │   ├── enums/               ← Role, TaskStatus, Priority
│   │   ├── exception/           ← Global error handling
│   │   ├── repository/          ← Database queries
│   │   ├── security/            ← JWT logic and filter
│   │   └── service/             ← Business logic
│   └── src/main/resources/
│       └── application.properties
│
├── frontend/    ← Next.js frontend (JavaScript)
│   └── src/
│       ├── app/                 ← Pages (login, register, tasks)
│       ├── components/          ← Reusable UI components
│       ├── context/             ← Auth state management
│       └── lib/                 ← Axios API configuration
│
├── database/
│   └── schema.sql               ← MySQL database schema
└── docs/
    └── api-docs.md              ← API documentation
```

---

## Prerequisites

Make sure you have these installed before running the app:

- **Java 17+** → [Download here](https://adoptium.net/)
- **Maven 3.6+** → Usually bundled with Java or your IDE
- **Node.js 18+** → [Download here](https://nodejs.org/)
- **MySQL 8+** → [Download here](https://dev.mysql.com/downloads/)

---

## Database Setup

1. Open MySQL and run the schema file:
   ```sql
   source C:/path/to/database/schema.sql;
   ```
   Or manually create the database:
   ```sql
   CREATE DATABASE taskdb;
   ```

2. Hibernate will automatically create the tables when the backend first starts (because `ddl-auto=update` is set).

---

## Backend Setup (Spring Boot)

### 1. Set Environment Variables

Before running, set these environment variables on your system or IDE:

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | MySQL JDBC connection URL | `jdbc:mysql://localhost:3306/taskdb` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | `yourpassword` |
| `JWT_SECRET` | Secret key for JWT signing (min 32 chars) | `mySecretKey1234567890abcdefghijklm` |
| `JWT_EXPIRATION` | Token lifetime in milliseconds | `86400000` (24 hours) |
| `FRONTEND_URL` | Frontend URL for CORS | `http://localhost:3000` |

#### How to set environment variables (Windows):
```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/taskdb"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "yourpassword"
$env:JWT_SECRET = "mySecretKey1234567890abcdefghijklm"
$env:JWT_EXPIRATION = "86400000"
$env:FRONTEND_URL = "http://localhost:3000"
```

#### OR in IntelliJ IDEA:
Go to **Run > Edit Configurations > Environment Variables** and add them there.

### 2. Run the Backend
```bash
cd backend
mvn spring-boot:run
```

The backend will start on **http://localhost:8080**

---

## Frontend Setup (Next.js)

### 1. Configure Environment Variable
The `.env.local` file already contains the default:
```
NEXT_PUBLIC_API_URL=http://localhost:8080
```
Change this if your backend runs on a different port.

### 2. Install Dependencies
```bash
cd frontend
npm install
```

### 3. Run the Frontend
```bash
npm run dev
```

The frontend will start on **http://localhost:3000**

---

## Using the Application

1. Open **http://localhost:3000** in your browser
2. Click **Register here** to create a new account
3. Choose role: `USER` (manage your own tasks) or `ADMIN` (see all tasks)
4. After registering, you're automatically logged in
5. Use **+ New Task** to create tasks
6. Filter tasks by Status or Priority using the dropdowns
7. Use **Edit**, **Mark Done**, and **Delete** buttons on each task

---

## Key Environment Variable Names (for secure submission)

Do NOT commit real values. Share only these variable names:

```
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION
FRONTEND_URL
NEXT_PUBLIC_API_URL
```

---

## API Documentation

See [`docs/api-docs.md`](docs/api-docs.md) for full API reference including:
- All endpoint URLs
- Request/response examples
- Error codes

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Next.js 14, React, Axios |
| Backend | Spring Boot 3, Spring Security, Spring Data JPA |
| Auth | JWT (JSON Web Token) with JJWT library |
| Database | MySQL 8 |
| Password Hashing | BCrypt |
| ORM | Hibernate (via JPA) |

---

## Files NOT in the Repository

These are excluded via `.gitignore`:
- `task-management-backend/target/` (build output)
- `task-management-frontend/.next/` (Next.js build)
- `task-management-frontend/node_modules/` (npm packages)
- `.env.local` (environment variables)
