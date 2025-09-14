# Todo Application API Documentation

## Overview
This document provides examples and instructions for using the Todo Application API endpoints.

## Base URL
```
http://localhost:8080
```

## Authentication
Currently, the API endpoints are open and don't require authentication.

## API Endpoints

### 1. Get All Todos
```bash
GET /api/todos
```
Example Response:
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the Todo application",
    "completed": false,
    "dueDate": "2025-09-21T15:00:00Z",
    "createdAt": "2025-09-14T10:30:00Z",
    "updatedAt": "2025-09-14T10:30:00Z"
  }
]
```

### 2. Get Todo by ID
```bash
GET /api/todos/{id}
```
Example:
```bash
GET /api/todos/1
```

### 3. Create Todo
```bash
POST /api/todos
Content-Type: application/json

{
  "title": "New task",
  "description": "Description of the new task",
  "dueDate": "2025-09-21T15:00:00Z"
}
```

### 4. Update Todo
```bash
PUT /api/todos/{id}
Content-Type: application/json

{
  "title": "Updated task",
  "description": "Updated description",
  "completed": true,
  "dueDate": "2025-09-21T15:00:00Z"
}
```

### 5. Delete Todo
```bash
DELETE /api/todos/{id}
```

### 6. Toggle Todo Status
```bash
PATCH /api/todos/{id}/toggle
```

### 7. Get Todos by Status
```bash
GET /api/todos/status?completed=true
```

### 8. Search Todos
```bash
GET /api/todos/search?title=project
```

## Response Status Codes
- 200: Success
- 201: Created
- 204: No Content (successful deletion)
- 400: Bad Request
- 404: Not Found
- 500: Internal Server Error

## Interactive Documentation
Access the interactive Swagger UI documentation at:
```
http://localhost:8080/swagger-ui.html
```

Access the OpenAPI specification at:
```
http://localhost:8080/api-docs
```