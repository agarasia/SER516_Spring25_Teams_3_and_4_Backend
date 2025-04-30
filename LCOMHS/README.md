# LOCMHS API
To run LCOMHS API using Docker:

### 1. go to LCOMHS directory 
cd LCOMHS
### 2. run the below commands

####  To build the docker container
```bash
docker build -t locmhs .
```

#### To run the docker container
```bash
docker run -p 8000:8000 --name locmhs-api-container locmhs
```

#### TO stop the container
```bash
docker stop locmhs-api-container
```

#### To remove the container
```bash 
docker rm locmhs-api-container 
```

### testing using curl commands 
curl -X POST "http://localhost:8000/api/lcomhs/calculate" \
     -H "Content-Type: application/json" \
     -d '{"repo_url": "https://github.com/HouariZegai/Calculator"}'