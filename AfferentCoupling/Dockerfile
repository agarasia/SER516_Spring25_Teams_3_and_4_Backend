# syntax=docker/dockerfile:1

################  Build  ################
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first (to cache dependencies)
COPY AfferentCoupling/pom.xml .
RUN mvn -q dependency:go-offline

# Copy the AfferentCoupling source
COPY AfferentCoupling/src ./src

# Create the correct directory structure and copy utilities
RUN mkdir -p src/main/java/utilities
COPY utilities/ src/main/java/utilities/

# Now src/main/java/utilities/RepoFetcher.java exists correctly!

RUN mvn -q package -DskipTests         
# → target/*.jar

################  Run  ##################
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8083          
ENTRYPOINT ["java","-jar","/app/app.jar"]