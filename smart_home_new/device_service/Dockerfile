# Use a modern, lightweight Java 21 base image
# Eclipse Temurin is a good choice for general use
FROM eclipse-temurin:21-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
# Make sure to replace 'target/your-application.jar' with the correct path to your JAR
COPY device-service-0.0.1.jar app.jar

# Expose the port your application runs on (e.g., 8080 for Spring Boot)
EXPOSE 8080

# Command to run the application
# Using the array format of ENTRYPOINT is recommended
ENTRYPOINT ["java", "-jar", "app.jar"]