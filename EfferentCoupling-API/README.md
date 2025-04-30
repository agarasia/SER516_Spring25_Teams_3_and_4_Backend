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


## 🧪 How to Test the Efferent API using Postman

Follow these steps to send a `POST` request to the API endpoint using [Postman](https://www.postman.com/):

### 🔧 Steps

1. **Open Postman**.

2. **Set the HTTP method to `POST`** using the dropdown on the left of the URL bar.

3. **Enter the API Endpoint**:
   ```
   http://localhost:8002/efferent
   ```

4. **Set the Request Body**:
   - Navigate to the **Body** tab.
   - Select **raw**.
   - From the dropdown next to “Text”, choose **JSON**.
   - Paste the following JSON into the editor:
     ```json
     {
       "repo_url": "{Github_URL}"
     }
     ```

5. **(Optional) Set Headers**:
   - Go to the **Headers** tab.
   - Ensure the following header is present (usually auto-added):
     ```
     Key: Content-Type
     Value: application/json
     ```

6. **Click "Send"** to submit the request.

7. **Check the Response**:
   - A JSON response will return with the calculated metrics or an error message.

## Building and Running with Docker

### Prerequisites
Ensure that the `.jar` file exists before building the Docker container. Use the following command to create the `.jar` file:
```bash
mvn clean package
```

### Building the Docker Container
```bash
docker build -f EfferentCoupling-API/Dockerfile -t efferent-coupling-api .
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
curl -X POST http://localhost:8002/efferent \
     -H "Content-Type: application/json" \
     -d '{"repo_url":{Github_URL}"}'


