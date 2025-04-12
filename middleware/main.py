from fastapi import FastAPI, HTTPException, Body, Query
import httpx
import os
from fastapi.middleware.cors import CORSMiddleware
from typing import Dict, List
import asyncio
import time

app = FastAPI(title="API Gateway (Middleware)")

# We'll read service URLs from ENV or default
LCOM4_URL = os.environ.get("LCOM4_SERVICE_URL", "http://lcom4:8000")
LCOMHS_URL = os.environ.get("LCOMHS_URL", "http://lcomhs:8000")
DEFECT_SCORE_URL = os.environ.get("DEFECT_SCORE_URL", "http://defectscore:8000")
BENCHMARK_URL = os.environ.get("BENCHMARK_SERVICE_URL", "http://benchmark:8000")
INSTABILITY_URL = os.environ.get("INSTABILITY_SERVICE_URL", "http://instability:8000")
AFFERENT_URL = os.environ.get("AFFERENT_SERVICE_URL", "http://afferent-api:8081")
EFFERENT_URL = os.environ.get("EFFERENT_SERVICE_URL", "http://efferent-api:8082")
DEFECTDENSITY_URL = os.environ.get("DEFECTDENSITY_SERVICE_URL", "http://defectdensity-api:8083")

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=['*'],  # You can specify specific origins instead of allowing all with "*"
    allow_credentials=True,
    allow_methods=['*'],
    allow_headers=['*'],
)

# Mock Endpoint
@app.get("/")
def read_root():
    return {"message": "API Gateway up and running"}


@app.post("/gateway/calculate")
async def gateway_calculate(
    gitHubLink: str = Body(..., example="https://github.com/owner/repo"),
    metrics: str = Body(..., example="LCOM4,LCOMHS,DefectScore,AfferentCoupling,EfferentCoupling,DefectDensityAnalysis")
):
    """
    Receives a GitHub repository link and a comma-separated list of metrics to be calculated.
    It then calls the corresponding microservices concurrently and returns a combined result.
    """
    selected_metrics = [m.strip() for m in metrics.lower().split(",")]
    results = {}

    # Create a single AsyncClient to use for all service calls.
    async with httpx.AsyncClient() as client:
        tasks = []

        if "lcom4" in selected_metrics:
            tasks.append(call_lcom4(client, gitHubLink))
        else:
            results["LCOM4"] = None

        if "defectscore" in selected_metrics:
            tasks.append(call_defect_score(client, gitHubLink))
        else:
            results["DefectScore"] = None

        if "lcomhs" in selected_metrics:
            tasks.append(call_lcomhs(client, gitHubLink))
        else:
            results["LCOMHS"] = None
        
        if "afferentcoupling" in selected_metrics:
            tasks.append(call_afferent(client, gitHubLink))
        else:
            results["AfferentCoupling"] = None

        if "efferentcoupling" in selected_metrics:
            tasks.append(call_efferent(client, gitHubLink))
        else:
            results["EfferentCoupling"] = None

        if "defectdensityanalysis" in selected_metrics:
            tasks.append(call_defectdensity(client, gitHubLink))
        else:
            results["DefectDensityAnalysis"] = None
        

        # Run all tasks concurrently.
        responses = await asyncio.gather(*tasks, return_exceptions=True)

    # Process each response from the tasks.
    for resp in responses:
        if isinstance(resp, Exception):
            # If any call fails, raise an exception.
            raise HTTPException(status_code=500, detail=str(resp))
        else:
            results.update(resp)
    # instability is to be called only if afferent and efferent coupling are called and it takes the results from them as input
    async with httpx.AsyncClient() as client:
        if "afferentcoupling" in selected_metrics and "efferentcoupling" in selected_metrics and "instability" in selected_metrics:
            data = {
                "afferent": results["AfferentCoupling"],
                "efferent": results["EfferentCoupling"]
            }
            # Call the instability service
            instability_response = await call_instability(client, data)
            results["Instability"] = instability_response["Instability"]
        else:
            results["Instability"] = None
            
    return results

@app.get("/gateway/benchmark")
async def gateway_benchmark(
    gitHubLink: str = Query(..., example="github link")
):
    async with httpx.AsyncClient() as client:
        response = await client.get(f"{BENCHMARK_URL}/benchmark?gitHubLink={gitHubLink}", timeout=None)
    if response.status_code != 200:
        raise HTTPException(status_code=500, detail=f"benchmark get call failed: {response.status_code}, {response.text}")
    results = response.json()
    return results

@app.post("/gateway/benchmark")
async def gateway_benchmark_post(
    gitHubLink: str = Body(..., example="like to github"),
    benchmarks: Dict[str, float] = Body(..., description="object of key value pair for benchmarks")
):  
    request = {
        "gitHubLink": gitHubLink,
        "benchmarks": benchmarks
    }
    async with httpx.AsyncClient() as client:
        response = await client.post(f"{BENCHMARK_URL}/benchmark", json=request, timeout=None)
    if response.status_code != 200:
        raise Exception("Failed to post benchmark data")
    return response.json()

# Helper functions to call each microservice

async def call_lcom4(client: httpx.AsyncClient, gitHubLink: str) -> dict:
    formdata = {
        "gitHubLink": gitHubLink
    }
    response = await client.post(f"{LCOM4_URL}/api/lcom4/calculate", data=formdata, timeout=None)
    if response.status_code != 200:
        raise Exception(f"LCOM4 call failed: {response.status_code}, {response.text}")
    return {"LCOM4": response.json()}

async def call_instability(client: httpx.AsyncClient, data: dict) -> dict:
    response = await client.post(f"{INSTABILITY_URL}/instability/calculate", json=data, timeout=None)
    if response.status_code != 200:
        raise Exception(f"Instability call failed: {response.status_code}, {response.text}")
    return {"Instability": response.json()}


async def call_defect_score(client: httpx.AsyncClient, gitHubLink: str) -> dict:
    formdata = {
        "sourceValue": gitHubLink
    }
    response = await client.post(f"{DEFECT_SCORE_URL}/api/defect_score/calculate", data=formdata, timeout=None)
    if response.status_code != 200:
        raise Exception(f"DefectScore call failed: {response.status_code}, {response.text}")
    return {"DefectScore": response.json()}

async def call_lcomhs(client: httpx.AsyncClient, gitHubLink: str) -> dict:
    formdata = {
        "gitHubLink": gitHubLink
    }
    response = await client.post(f"{LCOMHS_URL}/api/lcomhs/calculate", data=formdata, timeout=None)
    if response.status_code != 200:
        raise Exception(f"LCOMHS call failed: {response.status_code}, {response.text}")
    return {"LCOMHS": response.json()}

async def call_defectdensity(client: httpx.AsyncClient, gitHubLink: str) -> dict:
    response = await client.get(f"{DEFECTDENSITY_URL}/api/defects/repo?url={gitHubLink}", timeout=None)
    
    if response.status_code != 200:
        raise Exception(f"DefectDensity call failed: {response.status_code}, {response.text}")
    return {"DefectDensityAnalysis": response.json()}

async def call_afferent(client: httpx.AsyncClient, gitHubLink: str) -> dict:
    response = await client.post(f"{AFFERENT_URL}/api/coupling/github?repoUrl={gitHubLink}", timeout=None)
    if response.status_code != 200:
        raise Exception(f"AfferentCoupling call failed: {response.status_code}, {response.text}")
    data = response.json()
    return {"AfferentCoupling": data}

async def call_efferent(client: httpx.AsyncClient, gitHubLink: str) -> dict:
    response = await client.post(f"{EFFERENT_URL}/api/efferent-coupling/analyze?url={gitHubLink}", timeout=None)
    if response.status_code != 200:
        raise Exception(f"EfferentCoupling call failed: {response.status_code}, {response.text}")
    data = response.json()
    return {"EfferentCoupling": data}

@app.post("/gateway/defectscore/labelmapping")
async def gateway_get_defectscore_labelmapping(
    gitHubLink: str = Body(..., example="https://github.com/owner/repo"),
    labelSeverityMap: List = Body(..., example=[{"key": "bug", "value": 2}, {"key": "critical", "value": 5}])
    ):
    """
    Post the label mapping data to database using Defect Score service
    """

    results = {}

    async with httpx.AsyncClient() as client:
        label_data = {
            "sourceValue": gitHubLink,
            "labelSeverityMap": labelSeverityMap

        }
        labelmapping_response = await client.post(f"{DEFECT_SCORE_URL}/api/defect_score/labelsMapping", json=label_data, timeout=None)

    if labelmapping_response.status_code != 200:
        raise HTTPException(
            status_code=500,
            detail=f"defectscore post label mapping call failed: {labelmapping_response.status_code}, {labelmapping_response.text}"
        )
    
    results = labelmapping_response.json()
    return results

@app.get("/gateway/defectscore/labelmapping")
async def gateway_get_defectscore_labelmapping(
    gitHubLink: str = Query(..., example="URL For the GitHub Repository")
    ):
    """
    Get the label mapping data from database from Defect Score service
    """

    results = {}

    async with httpx.AsyncClient() as client:
        param_data = {
            "sourceValue": gitHubLink
        }
        labelmapping_response = await client.get(f"{DEFECT_SCORE_URL}/api/defect_score/labelsMapping", params=param_data, timeout=None)

    if labelmapping_response.status_code != 200:
        raise HTTPException(
            status_code=500,
            detail=f"defectscore get label mapping call failed: {labelmapping_response.status_code}, {labelmapping_response.text}"
        )
    
    results = labelmapping_response.json()
    return results