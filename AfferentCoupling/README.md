# Afferent Coupling API

This guide provides instructions on how to run the Afferent Coupling API application both locally and using Docker.

## Running the Application Locally

1. **Navigate to the Project Directory:**
   ```bash
   cd AfferentCoupling
   ```

2. **Run the Application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the API:**
   Once the application is running, you can access the API using the following URL:
   ```
   http://localhost:8081/api/coupling/github?repoUrl=[GITHUB_URL]
   ```
   - **Expected Input Value:** A GitHub repo url that contains java files.


## Testing the API using Postman

1. Open Postman

2. Select **POST** request

3. Enter the API Endpoint:
    ```bash
    http://localhost:8081/api/coupling/github?repoUrl={GITHUB_URL}
    ```
4. Send the Request and verify response


## Building and Running with Docker

### Prerequisites
Ensure that the `.jar` file exists before building the Docker container. Use the following command to create the `.jar` file:
```bash
mvn clean package
```

### Building the Docker Container
```bash
 docker build -t afferent-api .
```

### Running the Docker Container
```bash
docker run -p 8081:8081 --name afferent-api-container afferent-api
```

### Stopping the Container
```bash
docker stop afferent-api-container
```

## testing curl command 
curl -X POST "http://localhost:8081/api/coupling/github?repoUrl={Github_URL}"


