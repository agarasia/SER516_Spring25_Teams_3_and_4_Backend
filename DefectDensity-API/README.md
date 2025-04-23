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
   Once the application is running, you can access the API using the following URL:
   ```
   http://localhost:8083/api/defects/repo?url=[Use a git hub repo url] [GET]
   ```
   - **Variable Name:** `url`
   - **Expected Input Value:** A Github repository URL



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
http://localhost:8083
```

## testing curl command 
a sample test Command
```
curl -X GET "http://localhost:8083/api/defects/repo?url=https://github.com/FreeTubeApp/FreeTube/Issues" -H "Accept: application/json"
```
