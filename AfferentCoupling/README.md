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
   - **Expected Input Value:** A GitHub repo url that contains java files. If you are testing with privategithub repo, then use both repo url and token as params.


## Testing the API using Postman


## 🧪 How to Test the Afferent API using Postman

Follow these steps to send a `POST` request to the API endpoint using [Postman](https://www.postman.com/):

### 🔧 Steps

1. **Open Postman**.

2. **Set the HTTP method to `POST`** using the dropdown on the left of the URL bar.

3. **Enter the API Endpoint**:
   ```
   http://localhost:8001/afferent
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
  docker build -f AfferentCoupling/Dockerfile -t afferent-api .
 ```
 
 ### Running the Docker Container
 ```bash
 docker run -p 8081:8081 --name afferent-api-container afferent-api
 ```

### Stopping the Container
```bash
docker stop afferent-api-container
```
### Remove the Container
```bash
docker rm afferent-api-container
```

## testing curl command 
curl -X POST http://localhost:8001/afferent \
     -H "Content-Type: application/json" \
     -d '{"repo_url":{Github_URL}"}'


