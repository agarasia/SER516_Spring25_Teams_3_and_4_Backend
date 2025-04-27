# syntax=docker/dockerfile:1
################  Build  ################
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q package -DskipTests         
# → target/*.jar

################  Run  ##################
FROM eclipse-temurin:17.0.15_6-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8083          
# adjust per service
ENTRYPOINT ["java","-jar","/app/app.jar"]
