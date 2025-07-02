# Use official OpenJDK 18 image for building
FROM openjdk:18-jdk AS builder

WORKDIR /app
COPY . .

# Grant execution rights and build the app using Maven wrapper
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Use official OpenJDK 18 image for running the app
FROM openjdk:18-jdk

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
