# -------------------------------
# Build stage
# -------------------------------
FROM eclipse-temurin:24-jdk AS build
WORKDIR /app

# Copy Maven wrapper and make it executable
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Copy pom.xml first (for dependency caching)
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build the application (skip tests for faster builds)
RUN ./mvnw package -DskipTests

# -------------------------------
# Runtime stage
# -------------------------------
FROM eclipse-temurin:24-jre
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]