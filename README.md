# Study Planner API

A backend REST API for managing study tasks (CRUD, filters/search, authentication later).

## Tech Stack
- Java 21
- Spring Boot
- Spring Web (REST)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Docker Compose
- Maven

---

## Quick Start (Local)

### 1) Start PostgreSQL with Docker Compose
From the project root:
```bash
docker compose up -d
``` 
Check that the container is running:
```bash
docker ps
```
Stop containers:
```bash
docker compose down
```
### 2) Run the application
Option A: IntelliJ IDEA -> Run StudyPlannerApiApplication\
Option B: Maven:
```bash
mvn spring-boot:run
```
The application will start at:
http://localhost:8080

### 3) Database
PostgreSQL is started from `docker-compose.yml`:
- DB: `study_planner`
- User: `planner`
- Password: `secret`

If you change DB name/user/password after the first run, re-create the volume:
```bash
docker compose down -v
docker compose up -d
```
### 4) API Endpoints
Tasks
- POST /api/tasks — create a task
- GET /api/tasks — list tasks
- GET /api/tasks/{id} — get task by id
- PUT /api/tasks/{id} — update task (full update)
- DELETE /api/tasks/{id} — delete task