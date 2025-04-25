# Efferent Coupling API

This guide provides instructions on how to run the Efferent Coupling API application both locally and using Docker.

<!-- ---

## 🛠️ MongoDB Setup Using Docker

Before starting the API service, you need a running MongoDB instance. If you don’t have MongoDB installed locally, the easiest way is to run it in a Docker container.

### 🧱 Start MongoDB Container

```bash
docker run -d \
  --name mongo-container \
  -p 27017:27017 \
  -e MONGO_INITDB_DATABASE=efferent_coupling_db \
  mongo
```

This will:
- Pull the official MongoDB image (if not already pulled)
- Run MongoDB on port `27017`
- Name the container `mongo-container`
- Set the default database to `efferent_coupling_db`

> By default, the Spring Boot app connects to `mongodb://localhost:27017/efferent_coupling_db`. No extra config needed unless overridden. -->

---

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
   http://localhost:8082/api/efferent-coupling/analyze [POST]
   ```
   - **Variable Name:** `url`
   - **Expected Input Value:** GitHub repository URL.

## Testing the API using Postman

1. Open Postman

2. Select **POST** request

3. Enter the API Endpoint:
    ```bash
    http://localhost:8082/api/efferent-coupling/analyze
    ```

4. Go to the **"Body"** tab in Postman,Select **"form-data"**

5. Add a new key with:
    - **Key**: `url`
    - **Type**: `Text`
    - **Value**: GitHub repository URL containing Java Code.

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
docker run -d -p 8082:8082 --name efferent-coupling-api \
  efferent-coupling-api
```
<!-- 
> The `--link` connects your app container with the MongoDB container using an internal hostname (`mongo-container`).
> The database name is set as `efferent_coupling_db`. -->


### Stopping the Container
```bash
docker stop efferent-coupling-api
```

## testing curl command 
curl -X POST http://localhost:8082/api/efferent-coupling/analyze \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "url=https://github.com/shashirajraja/shopping-cart"


