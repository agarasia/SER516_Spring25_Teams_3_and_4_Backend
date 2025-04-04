FROM eclipse-temurin:21-jdk-jammy
RUN apt-get update && apt-get install -y git
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} afferent-coupling-api.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "afferent-coupling-api.jar"]
