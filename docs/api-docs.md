# API Documentation

## Base URL
All endpoints are relative to: `http://localhost:8080`

---

## Authentication Endpoints (Public - no token required)

### POST /api/auth/register
Register a new user.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123",
  "role": "USER"
}
```
- `role` is optional. Defaults to `USER`. Valid values: `ADMIN`, `USER`

**Success Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2025-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email is already registered: john@example.com"
}
```

---

### POST /api/auth/login
Login and receive a JWT token.

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "timestamp": "2025-01-01T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password"
}
```

---

## Task Endpoints (Protected - requires JWT token)

### How to send the JWT token
Add `Authorization` header to every request:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

### GET /api/tasks
Get a paginated, filtered list of tasks.
- `USER` role: returns only their own tasks
- `ADMIN` role: returns all tasks in the system

**Query Parameters (all optional):**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `status` | string | - | Filter: `TODO`, `IN_PROGRESS`, `DONE` |
| `priority` | string | - | Filter: `LOW`, `MEDIUM`, `HIGH` |
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 10 | Items per page |
| `sortBy` | string | `createdAt` | Sort field: `createdAt`, `dueDate`, `priority` |
| `sortDir` | string | `desc` | Sort direction: `asc`, `desc` |

**Example Request:**
```
GET /api/tasks?status=TODO&priority=HIGH&page=0&size=5&sortBy=dueDate&sortDir=asc
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Fix login bug",
      "description": "Users can't log in on mobile",
      "status": "TODO",
      "priority": "HIGH",
      "dueDate": "2025-03-15",
      "createdAt": "2025-01-01T10:00:00",
      "updatedAt": "2025-01-01T10:00:00",
      "userId": 1,
      "userName": "John Doe"
    }
  ],
  "totalElements": 14,
  "totalPages": 3,
  "number": 0,
  "size": 5,
  "first": true,
  "last": false
}
```

---

### GET /api/tasks/{id}
Get a single task by ID.
- USER can only access their own tasks.
- ADMIN can access any task.

**Success Response (200 OK):** Same as task object in the list above.

**Error Response (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 99"
}
```

---

### POST /api/tasks
Create a new task. Task is assigned to the logged-in user.

**Request Body:**
```json
{
  "title": "Build login page",
  "description": "Create the login UI with form validation",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2025-03-20"
}
```
- `description` and `dueDate` are optional.
- `status` valid values: `TODO`, `IN_PROGRESS`, `DONE`
- `priority` valid values: `LOW`, `MEDIUM`, `HIGH`

**Success Response (201 Created):** Returns the created task object.

---

### PUT /api/tasks/{id}
Update an existing task (full update).
- USER can only update their own tasks.
- ADMIN can update any task.

**Request Body:** Same as POST /api/tasks.

**Success Response (200 OK):** Returns the updated task object.

---

### DELETE /api/tasks/{id}
Delete a task.
- USER can only delete their own tasks.
- ADMIN can delete any task.

**Success Response (204 No Content):** Empty response body.

---

### PATCH /api/tasks/{id}/complete
Mark a task as DONE (status = DONE).

**Success Response (200 OK):** Returns the updated task object with `status: "DONE"`.

---

## Error Response Format (consistent across all endpoints)
```json
{
  "timestamp": "2025-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": {
    "title": "Task title is required",
    "status": "Status is required"
  }
}
```

## HTTP Status Codes Used
- `200 OK` - Success
- `201 Created` - Resource created
- `204 No Content` - Deleted successfully
- `400 Bad Request` - Validation error or bad input
- `401 Unauthorized` - Missing or invalid JWT token
- `403 Forbidden` - Token valid but insufficient permissions
- `404 Not Found` - Resource doesn't exist
- `500 Internal Server Error` - Unexpected server error
