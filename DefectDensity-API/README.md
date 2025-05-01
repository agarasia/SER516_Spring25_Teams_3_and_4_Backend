# DefectDensity-API

This guide provides instructions on how to run the DefectDensity-API application both locally and using Docker.

## Running the Application Locally

1. **Navigate to the Project Directory:**
   ```bash
   cd DefectDensity-API
   ```

2. **Run the Application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the API:**
   

## 🧪 How to Test the Defect Density API using Postman

Follow these steps to send a `POST` request to the API endpoint using [Postman](https://www.postman.com/):

### 🔧 Stepss

1. **Open Postman**.

2. **Set the HTTP method to `POST`** using the dropdown on the left of the URL bar.

3. **Enter the API Endpoint**:
   ```
   http://localhost:8003/defectdensity
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



### Building the Docker Container
```bash
docker build --no-cache -t defectdensity .
```

### Running the Docker Container
```bash
docker run --name defectdensity_container -p 8083:8083 -d defectdensity
```

### Starting the Container
```bash
docker start defectdensity_container
```

### Stopping the Container
```bash
docker stop defectdensity_container
```

### Using Custom Names for Docker Image and Container

#### Building the Docker Container with a Custom Name
```bash
docker build -t [DockerImageName]
```

#### Running the Docker Container with a Custom Name
```bash
docker run --name [DockerContainerName] -p 8083:8083 -d [DockerImageName]
```

#### Starting the Custom Named Container
```bash
docker start [DockerContainerName]
```

#### Stopping the Custom Named Container
```bash
docker stop [DockerContainerName]
```

## Accessing the Application
Once the container is running, you can access the application by pinging the local host:
```
http://localhost:8083/defectdensity
```

## testing curl command 
a sample test Command
```
   curl -X POST http://localhost:8003/defectdensity \
     -H "Content-Type: application/json" \
     -d '{"repo_url":{Github_URL}"}'
```
