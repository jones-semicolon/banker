# Stage 1: Build the Spring Boot app
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw

# Build the application (skip tests to speed up)
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the Spring Boot app
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]