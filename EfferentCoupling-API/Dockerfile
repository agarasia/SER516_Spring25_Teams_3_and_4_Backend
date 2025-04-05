# Use a base image with JDK
FROM openjdk:21-jdk

# Setting working directory
WORKDIR /app

# Copying the JAR dynamically from the Maven build step
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} efferent-coupling-api.jar

# Exposing the port
EXPOSE 8082

# Running the application
ENTRYPOINT ["java", "-jar", "efferent-coupling-api.jar"]
