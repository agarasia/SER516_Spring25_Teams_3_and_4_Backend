
# Use OpenJDK 21 as base image
FROM openjdk:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the JAR dynamically from the Maven build step
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} DefectDensityAPI.jar

# Expose port 8083 (or the port your Spring Boot app uses)
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "DefectDensityAPI.jar"]
