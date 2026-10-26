# WorkSphere — Employee & Project Management API

WorkSphere is a RESTful backend application for managing employees, departments, and projects within an organization. It is built with Java and Spring Boot and provides JWT-based authentication, role-based authorization, employee approval workflows, and email-assisted password recovery.

The backend is containerized with Docker and deployed to an Azure Linux VM using Docker Compose, PostgreSQL, and Nginx. GitHub Actions automates the build, test, container image publishing, and deployment workflow.

## Features

- **Authentication:** JWT access tokens and refresh-token workflow, including login, token renewal, and logout.
- **Role-based access control:** Support for `ADMIN`, `HR`, `MANAGER`, and `EMPLOYEE` roles.
- **Employee approval:** Newly registered accounts remain `PENDING` until approved by an authorized administrator or HR user.
- **Employee management:** Employee profiles, employee listings, approval workflows, and team-related endpoints.
- **Department management:** Department creation, updates, manager assignment, and department-related queries.
- **Project management:** Project creation, updates, status filtering, manager assignment, and team-member management.
- **Password recovery:** Email-assisted password recovery using one-time passwords (OTPs).
- **Data persistence:** PostgreSQL with Spring Data JPA.
- **Containerization:** Docker and Docker Compose.
- **Reverse proxy:** Nginx routes incoming HTTP requests to the backend API.
- **Automated delivery:** GitHub Actions builds and tests the application, publishes images to GitHub Container Registry (GHCR), and deploys releases to Azure.
- **Health monitoring endpoint:** Spring Boot Actuator health endpoint for checking application status.

## Architecture

```text
Android Client (Jetpack Compose)
              |
              | HTTP/HTTPS + JSON
              v
          Nginx
              |
              v
       Spring Boot REST API
       ├── Spring Security + JWT
       ├── Controllers
       ├── Services
       ├── Repositories (Spring Data JPA)
       └── PostgreSQL Database
```

**Deployment pipeline:**

```text
Code Push to main
        |
        v
GitHub Actions
        |
        v
Build & Test with Maven
        |
        v
Build Docker Image
        |
        v
Publish Image to GHCR
        |
        v
Secure SSH Connection to Azure VM
        |
        v
Deploy Backend with Docker Compose
        |
        v
Health Check & Rollback on Failure
```

## Technology Stack

| Component | Technology |
|---|---|
| Language | Java 17 |
| Backend framework | Spring Boot |
| Web layer | Spring MVC |
| Security | Spring Security, JWT |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Build tool | Maven |
| Containerization | Docker, Docker Compose |
| Reverse proxy | Nginx |
| CI/CD | GitHub Actions |
| Container registry | GitHub Container Registry (GHCR) |
| Cloud deployment | Microsoft Azure Linux VM |
| Client integration | Android / Jetpack Compose |

> Verify the exact Spring Boot version against `pom.xml` before publishing this README.

## Project Structure

```text
worksphere/
├── .github/
│   └── workflows/
│       └── ci.yaml
├── scripts/
│   └── deploy-azure.sh
├── src/
│   ├── main/
│   │   ├── java/com/ali/worksphere/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   └── resources/
│   └── test/
├── compose.yaml
├── Dockerfile
├── .env.example
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

*This structure illustrates the main files and packages; the actual repository may contain additional files.*

## Requirements

### Local development

- JDK 17
- PostgreSQL 16, or a compatible PostgreSQL version
- Maven Wrapper included in the repository

### Docker-based setup

- Docker Engine
- Docker Compose v2

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/ali-softeng/worksphere.git
cd worksphere
```

### 2. Configure PostgreSQL

Create a PostgreSQL database named `worksphere` and prepare a database user with appropriate permissions.

### 3. Configure environment variables

Set the environment variables required by the selected Spring profile.

For PowerShell:

```powershell
$env:DEV_DB_USERNAME = "your-postgres-username"
$env:DEV_DB_PASSWORD = "your-postgres-password"
$env:JWT_SECRET = "your-long-random-secret"
$env:MAIL_USERNAME = "your-email@example.com"
$env:MAIL_PASSWORD = "your-email-app-password"
```

Replace all example values with your own configuration. Never commit real passwords, JWT signing secrets, or email credentials.

### 4. Run the application locally

```powershell
.\mvnw.cmd -Dspring-boot.run.profiles=dev spring-boot:run
```

On Linux or macOS, use:

```bash
./mvnw -Dspring-boot.run.profiles=dev spring-boot:run
```

If the application starts successfully, the API should be available at:

```text
http://localhost:8080
```

The exact profile and environment variable requirements depend on the application's configuration files.

## Run with Docker Compose

1. Copy the example environment file:

   ```bash
   cp .env.example .env
   ```

   On PowerShell:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Edit `.env` and supply the required database credentials and a strong JWT secret.

3. Create the external database volume if it does not already exist:

   ```bash
   docker volume create worksphere-db-data
   ```

4. Start the services:

   ```bash
   docker compose up --build -d
   ```

5. Check the health endpoint:

   ```text
   http://localhost/actuator/health
   ```

Nginx listens on port `80` in the containerized setup and proxies requests to the backend.

To stop the services without deleting the persistent database volume:

```bash
docker compose down
```

**Important:** Do not use `docker compose down -v` on an environment containing data you need to preserve.

## Configuration

The following environment variables are documented for the application:

| Variable | Purpose |
|---|---|
| `DEV_DB_USERNAME` | Database username for local development |
| `DEV_DB_PASSWORD` | Database password for local development |
| `DB_USERNAME` | Database username for the Docker environment |
| `DB_PASSWORD` | Database password for the Docker environment |
| `JWT_SECRET` | Secret used to sign JWTs |
| `MAIL_USERNAME` | SMTP account used for email |
| `MAIL_PASSWORD` | SMTP credential or app password |

Provide the values through your shell, IDE run configuration, or the environment file expected by Docker Compose. A `.env` file is not automatically loaded by every Spring Boot local-run configuration.

## API Overview

The following endpoints are documented in the project README. Verify their exact request formats and authorization rules against the current source code before relying on them.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Register an employee account |
| `POST` | `/auth/login` | Authenticate and obtain tokens |
| `POST` | `/auth/refresh` | Renew an access token |
| `POST` | `/auth/logout` | Revoke a refresh token |
| `POST` | `/auth/forgot-password` | Start password recovery |
| `POST` | `/auth/verify-otp` | Verify a password-recovery OTP |
| `POST` | `/auth/reset-password` | Reset a password |

New registrations remain pending until approved. The documented password policy requires at least eight characters, including an uppercase letter, a lowercase letter, a digit, and one of `@#$!`.

### Employees

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/employees` | Create an employee |
| `GET` | `/employees` | List employees |
| `GET` | `/employees/{id}` | Get an employee by ID |
| `PUT` | `/employees/{id}` | Update an employee |
| `DELETE` | `/employees/{id}` | Delete an employee |
| `GET` | `/employees/pending` | List employees awaiting approval |
| `PUT` | `/employees/{id}/approve` | Approve an employee and assign a role or department |
| `GET` | `/employees/me` | Get the authenticated employee's profile |
| `GET` | `/employees/my-team` | Get the authenticated manager's team |

### Departments

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/departments` | Create a department |
| `GET` | `/departments` | List departments |
| `GET` | `/departments/{id}` | Get department details |
| `PUT` | `/departments/{id}` | Update a department |
| `DELETE` | `/departments/{id}` | Delete a department |
| `POST` | `/departments/{id}/manager` | Assign a department manager |
| `PUT` | `/departments/{id}/manager` | Change a department manager |
| `DELETE` | `/departments/{id}/manager` | Remove a department manager |
| `GET` | `/departments/me` | Get the authenticated employee's department |

### Projects

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/projects` | Create a project |
| `GET` | `/projects` | List projects |
| `GET` | `/projects/{id}` | Get a project by ID |
| `PUT` | `/projects/{id}` | Update a project |
| `DELETE` | `/projects/{id}` | Delete a project |
| `GET` | `/projects/status/{status}` | Filter projects by status |
| `POST` | `/projects/{id}/manager` | Assign a project manager |
| `PUT` | `/projects/{id}/manager` | Change a project manager |
| `DELETE` | `/projects/{id}/manager` | Remove a project manager |
| `POST` | `/projects/{id}/employees` | Add an employee to a project |
| `DELETE` | `/projects/{id}/employees/{employeeId}` | Remove an employee from a project |
| `GET` | `/projects/me` | List the authenticated employee's projects |
| `GET` | `/projects/my-department` | List department projects for the authenticated manager |

### Health

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/actuator/health` | Check application health |

## Authentication and Authorization

Protected endpoints use the JWT access token returned by the login endpoint.

Include the token in the request header:

```http
Authorization: Bearer <access-token>
```

Role-based access rules are configured in the Spring Security configuration. Public endpoints, authenticated endpoints, and role-restricted endpoints should be verified against `SecurityConfig.java`.

## Example Requests

### Register an employee

```http
POST /auth/register
Content-Type: application/json
```

```json
{
  "name": "Ayesha Khan",
  "email": "ayesha@example.com",
  "password": "StrongPass1!"
}
```

### Log in

The account must first be approved if the application enforces the pending-approval workflow.

```http
POST /auth/login
Content-Type: application/json
```

```json
{
  "email": "ayesha@example.com",
  "password": "StrongPass1!"
}
```

Use the returned access token to call protected endpoints.

## CI/CD and Azure Deployment

The GitHub Actions workflow is configured to run on pushes to `main`.

The pipeline:

1. Starts a PostgreSQL service for CI.
2. Builds and tests the Spring Boot application using Maven.
3. Builds and publishes a Docker image to GitHub Container Registry.
4. Verifies SSH connectivity to the Azure VM.
5. Copies the deployment script to the VM.
6. Deploys the selected backend image using Docker Compose.
7. Checks backend health and attempts rollback if the new backend fails its health check.

Deployment depends on GitHub Actions secrets for SSH authentication and VM connection details. Credentials must be configured in repository secrets and must not be stored in the source code.

The database uses a persistent Docker volume. Deployment operations should preserve this volume.

## Running Tests

Run the test suite using the Maven Wrapper.

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

On Linux or macOS:

```bash
./mvnw test
```

The CI workflow also runs:

```bash
mvn clean package
```

## Security Notes

- Do not commit `.env` files containing real secrets.
- Keep JWT signing secrets strong and private.
- Store deployment credentials in GitHub Actions secrets.
- Restrict access to protected endpoints through authentication and role-based authorization.
- Keep production database credentials separate from development credentials.
- Verify HTTPS, firewall rules, backups, and monitoring before treating the deployment as production-hardened.

## Screenshots and Demo

Screenshots can be added here.
