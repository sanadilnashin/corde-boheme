# Developer Guide: PostgreSQL with Docker & Spring Boot

This guide covers the fundamentals of Docker, how Docker runs PostgreSQL, and step-by-step instructions for configuring PostgreSQL with a Java/Spring Boot application.

---

## 1. What is Docker?

**Docker** is a containerization platform that allows developers to package an application along with all its dependencies (libraries, configuration files, system binaries) into a standardized, lightweight unit called a **container**.

### Why Use Docker?
* **Consistency:** Eliminates the *"it works on my machine"* problem by providing identical environments across development, testing, and production.
* **Isolation:** Containers run in isolated environments on the host system without interfering with other applications or host libraries.
* **Portability:** A container runs on any system supporting Docker (Windows, macOS, Linux).
* **Fast Startup:** Unlike heavy Virtual Machines (VMs), containers share the host operating system's kernel, making them lightweight and fast to start.

---

## 2. Core Concepts: Images vs. Containers

* **Docker Image:** A read-only blueprint/template containing application code, dependencies, and environment configurations. (Analogous to a Java `.class` file or OOP Class).
* **Docker Container:** A runnable, isolated instance of a Docker image. (Analogous to an instantiated Object in OOP).
* **Docker Registry:** A repository where Docker images are stored and shared (e.g., Docker Hub).
* **Docker Volume:** A persistent data storage mechanism managed by Docker to ensure database data isn't lost when a container stops or is recreated.

---

## 3. How Docker Works for PostgreSQL

When you run a PostgreSQL Docker image:
1. Docker pulls the official `postgres` image from Docker Hub.
2. It provisions an isolated Linux container running the PostgreSQL database engine process.
3. **Port Mapping (`-p host:container`):** Maps a port on your local machine (e.g., `5434`) to the default PostgreSQL port inside the container (`5432`).
4. **Environment Variables (`-e`):** Configures database credentials (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`) on startup.
5. **Volume Mounting (`-v`):** Mounts container storage (`/var/lib/postgresql/data`) to host storage to maintain data persistence across container restarts.

---

## 4. Setting Up PostgreSQL with Docker Compose

Using `docker-compose.yml` allows you to manage container startup configurations declarative and consistently across developer environments.

### `docker-compose.yml`

Create a `docker-compose.yml` file in your project root:

```yaml
version: '3.8'

services:
  product-db:
    image: postgres:17
    container_name: product-db
    restart: always
    environment:
      POSTGRES_DB: product_db
      POSTGRES_USER: product
      POSTGRES_PASSWORD: product
      TZ: Asia/Kolkata
      PGTZ: Asia/Kolkata
    ports:
      - "5434:5432"  # Maps Host Port 5434 -> Container Port 5432
    volumes:
      - product_db_data:/var/lib/postgresql/data
    networks:
      - ecommerce-net

volumes:
  product_db_data:

networks:
  ecommerce-net:
    driver: bridge
Docker Compose Commands
Action	Command
Start container in background	docker compose up -d
Recreate container after YAML updates	docker compose up -d --force-recreate
Stop container without deleting volumes	docker compose down
Stop container and delete volumes	docker compose down -v
View container logs	docker logs -f product-db
5. Application Configuration (Spring Boot)
Configure your application to connect to the Dockerized PostgreSQL instance.

src/main/resources/application.yml
YAML
spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/product_db?currentSchema=main,public
    username: product
    password: product
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        default_schema: main
Note on Ports:

Use port 5434 when running Spring Boot directly on your host OS (via IDE or mvn spring-boot:run).

Use port 5432 and service name product-db if Spring Boot itself is containerized inside the same Docker network (ecommerce-net).

6. Common Issues & Troubleshooting
Issue 1: Timezone Mismatch Handshake Failure
Symptom: FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"

Cause: Windows JVMs pass legacy timezone alias Asia/Calcutta which newer PostgreSQL versions reject.

Fix: Pass -Duser.timezone=Asia/Kolkata to your Maven or Java startup arguments:

PowerShell
# PowerShell
$env:JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Kolkata"
mvn spring-boot:run
Issue 2: Connecting via Database GUI (DBeaver / DataGrip)
Host: localhost

Port: 5434

Database: product_db

Username: product

Password: product