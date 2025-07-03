# Stage 1: Build the app
FROM openjdk:18-jdk-slim AS builder

WORKDIR /app
COPY . .

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 2: Run the app
FROM openjdk:18-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
