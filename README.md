# Study Planner API

A backend REST API for managing study tasks.

Current features:
- Create, read, update, delete tasks
- Search tasks by title
- Validate request bodies
- Persist data in PostgreSQL
- Run locally with Docker Compose

## Tech Stack
- Java 21
- Spring Boot
- Spring Web (REST)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Docker Compose
- Maven

---

## Running the Project

### Option 1: Run everything with Docker Compose

```bash
docker compose up --build
```
The application will start at:
http://localhost:8080

Stop the containers:
```bash
docker compose down
```

### Option 2: Run only PostgreSQL with Docker, application locally

Start only the database:

```bash
docker compose up -d db
```

Then run the application locally:

```bash
./mvnw spring-boot:run
```

The application will connect to PostgreSQL at: localhost:5432

## API Endpoints

### Create task

```http
POST /api/tasks
Content-Type: application/json
```
Request:
```json
{
    "title": "Study Spring Boot validation",
    "description": "Learn @NotBlank and @FutureOrPresent",
    "tag": "Spring Boot",
    "dueDate": "2026-07-20"
}
```
Response:
```json
{
    "id": 1,
    "title": "Study Spring Boot validation",
    "description": "Learn @NotBlank and @FutureOrPresent",
    "tag": "Spring Boot",
    "dueDate": "2026-07-20",
    "status": "TO_DO"
}
```

### Update task

```http
PUT /api/tasks/{id}
Content-Type: application/json
```
Request:
```json
{
    "title": "Study Spring Data JPA",
    "description": "Practice repositories and JPQL queries",
    "tag": "JPA",
    "dueDate": "2026-07-25",
    "status": "IN_PROGRESS"
}
```
Response:
```json
{
    "id": 1,
    "title": "Study Spring Data JPA",
    "description": "Practice repositories and JPQL queries",
    "tag": "JPA",
    "dueDate": "2026-07-25",
    "status": "IN_PROGRESS"
}
```

### Other task endpoints

```http
GET /api/tasks
GET /api/tasks/{id}
DELETE /api/tasks/{id}
```

### Search tasks by title

```http
GET /api/tasks/search/{keyword}
```

## Error Responses

### Validation error

```json
{
  "timestamp": "2026-07-05T10:30:00",
  "status": "BAD_REQUEST",
  "message": "Validation failed",
  "path": "/api/tasks",
  "fieldErrors": {
    "error": "validation failed",
    "fields": {
      "title": "title must be not empty"
    }
  }
}
```

### Not found error

```json
{
  "timestamp": "2026-07-05T10:30:00", 
  "status": "NOT_FOUND", 
  "message": "Task with id 99 not found",
  "path": "/api/tasks/99",
  "fieldErrors": {
  "error": "task not found"
  }
}
```

## Validation Rules

Create task:
- `title` must not be blank
- `dueDate` must be today or in the future, if provided

Update task:
- `title` must not be blank
- `dueDate` must be today or in the future, if provided
- `status` must not be null

Allowed status values:
- `TO_DO`
- `IN_PROGRESS`
- `DONE`
