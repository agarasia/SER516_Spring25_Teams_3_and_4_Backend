# LCOM4 API

This guide provides instructions on how to run the LCOM4 API application using Docker.


## Building and Running with Docker

### Building the Docker Container
 ```bash
  docker build -t lcom4 .
 ```
 
 ### Running the Docker Container
 ```bash
 docker run -p 8001:8001 --name lcom4-api-container lcom4
 ```

### Stopping the Container
```bash
docker stop lcom4-api-container
```
### Remove the Container
```bash
docker rm lcom4-api-container
```

## testing curl command 
curl -X POST "http://localhost:8000/lcomh4" \
     -H "Content-Type: application/json" \
     -d '{"repo_url": "{Github_URL}"}'


