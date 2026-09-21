# UpRit Backend

UpRit is a gamified student achievement & team-matching platform. This repository contains the Spring Boot backend service.

## Tech Stack
- **Java**: 24
- **Framework**: Spring Boot 3.4.3
- **Database**: MySQL 8.x
- **Security**: Spring Security + JWT (JSON Web Tokens)
- **Build Tool**: Maven

## Prerequisites
Before you begin, ensure you have the following installed on your machine:
- [Java Development Kit (JDK) 24](https://jdk.java.net/24/)
- [MySQL Server](https://dev.mysql.com/downloads/mysql/) (version 8.0+)
- (Optional) Maven (You can use the included Maven wrapper `mvnw`)

## Database Setup
1. Open your MySQL client (or CLI) and create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS uprit_db;
   ```
2. The application uses the following default database credentials:
   - **Username**: `root`
   - **Password**: `Ugesh@2006`

*Note: You can override these credentials using environment variables (see the Configuration section below).*

## Configuration

The main configuration file is located at `src/main/resources/application.properties`.

### Environment Variables
You can customize the database connection by setting the following environment variables before starting the application:
- `DB_USERNAME`: Your MySQL username (default is `root`)
- `DB_PASSWORD`: Your MySQL password (default is `Ugesh@2006`)

### Important Properties
In `application.properties`, you will find:
- **Server Port**: `server.port=8080` (Change this if port 8080 is already in use).
- **JWT Configuration**: 
  - `app.jwt.secret`: The secret key used for signing JWTs.
  - `app.jwt.expiration-ms`: The token expiration time in milliseconds (default is `86400000` for 24 hours).
- **Hibernate / JPA**: 
  - `spring.jpa.hibernate.ddl-auto=update`: Automatically updates the database schema to match the entity classes.

## Running the Application

You can run the application directly using the Maven wrapper included in the project.

### Using Maven Wrapper (Windows)
Open your terminal/command prompt in the root of the project directory and run:
```cmd
.\mvnw.cmd spring-boot:run
```

### Using Maven Wrapper (Mac/Linux)
Open your terminal in the root of the project directory and run:
```bash
./mvnw spring-boot:run
```

### Building an Executable JAR
If you prefer to build the JAR file and run it manually:
1. Build the project:
   ```cmd
   .\mvnw.cmd clean package -DskipTests
   ```
2. Run the generated JAR file:
   ```cmd
   java -jar target/uprit-0.0.1-SNAPSHOT.jar
   ```

## Troubleshooting
- **Port already in use**: If you get a "Web server failed to start. Port 8080 was already in use" error, open `src/main/resources/application.properties` and change `server.port` to another available port (e.g., `8081`).
- **MySQL Connection Error**: Ensure your MySQL server is running and the database `uprit_db` is created. Check your username and password in `application.properties` or via environment variables.

## Project Structure
- `src/main/java/com/tutorial/uprit`: Contains the main application source code (Controllers, Services, Repositories, Entities, Security Config).
- `src/main/resources`: Contains application configuration files.
