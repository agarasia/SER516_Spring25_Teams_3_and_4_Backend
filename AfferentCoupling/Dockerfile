# syntax=docker/dockerfile:1

################  Build  ################
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1) Copy the AfferentCoupling pom and go-offline
COPY AfferentCoupling/pom.xml .
RUN mvn -q dependency:go-offline

# 2) Copy your application code
COPY AfferentCoupling/src ./src

# 3) Copy the utilities (sits alongside AfferentCoupling/)
COPY utilities ./src/main/java/utilities

RUN mvn -q package -DskipTests

################  Run  ##################
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]
