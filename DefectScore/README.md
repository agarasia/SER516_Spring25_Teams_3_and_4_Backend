# Defects Score API

## Step 1 : navigate to DefectScore directory
cd DefectScore

## Step 2
### 1. Build the Docker container
docker build -t defect-score-service .

### 2. Run the Docker container
docker run --name defect-score-service -p 8000:8000 defect-score-service

### 3. Stop the container
docker stop defect-score-service 

### 4. to remove the container
docker rm defect-score-service 

### 5. test using curl commands 
curl -X POST "http://localhost:8000/api/defect_score/calculate" \
     -F "sourceValue=https://github.com/HouariZegai/Calculator"
