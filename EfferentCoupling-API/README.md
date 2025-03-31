# Efferent Coupling API

This guide provides instructions on how to run the Efferent Coupling API application both locally and using Docker.

## Running the Application Locally

1. **Navigate to the Project Directory:**
   ```bash
   cd EfferentCoupling-API
   ```

2. **Run the Application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the API:**
   Once the application is running, you can access the API using the following URL:
   ```
   http://localhost:8082/api/efferent-coupling/upload [POST]
   ```
   - **Variable Name:** `file`
   - **Expected Input Value:** A zip file containing Java code.

## Testing the API using Postman

1. Open Postman

2. Select **POST** request

3. Enter the API Endpoint:
    ```bash
    http://localhost:8082/api/efferent-coupling/upload
    ```

4. Go to the **"Body"** tab in Postman,Select **"form-data"**

5. Add a new key with:
    - **Key**: `file`
    - **Type**: `File`
    - **Value**: Upload the `test.zip` file.

6. Ensure the Content-Type is set to `multipart/form-data`

7. Send the Request and verify response

## Building and Running with Docker

### Prerequisites
Ensure that the `.jar` file exists before building the Docker container. Use the following command to create the `.jar` file:
```bash
mvn clean package
```

### Building the Docker Container
```bash
docker build --build-arg JAR_FILE=target/efferent-coupling-api-0.0.1-SNAPSHOT.jar -t efferent-coupling-api .
```

### Running the Docker Container
```bash
docker run -d -p 8082:8082 --name efferent-coupling-api efferent-coupling-api
```

### Stopping the Container
```bash
docker stop efferent-coupling-api
```

## testing curl command 
curl -v -X POST "http://localhost:8082/api/efferent-coupling/upload" -F "file=@\"[locationToFile]""
example my personal - /Users/twisted_fate/Desktop/E-commerce-project-springBoot-master2 (1).zip\

