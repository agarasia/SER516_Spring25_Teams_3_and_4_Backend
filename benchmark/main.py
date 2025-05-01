from fastapi import FastAPI
from fastapi import HTTPException, Body, Query
from typing import Optional, Dict
from mongo_service import store_benchmark_in_mongo, get_benchmark_from_mongo

app = FastAPI(
    title="Benchmark Microservice",
    description="Gets and Stores Benchmark metric for the projects",
    version="0.1.0"
)


@app.get("/")
def read_root():
    return {"message": "Benchmark Microservice is running."}

@app.post("/benchmark")
def store_benchmark_for_project(
    gitHubLink: str = Body(..., example="like to github"),
    benchmarks: Dict[str, float] = Body(..., description="object of key value pair for benchmarks")
):
    """
    Stores custom benchmark -> user entered bench mark in mongo for a given repo URL.
    """
    try:
        store_benchmark_in_mongo(gitHubLink, benchmarks)
        return {"message": "benchmark stored successfully."}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/benchmark")
def get_benchmark_for_project(
    gitHubLink: str = Query(..., example="https://github.com/owner/repo")
):
    """
    get benchmark -> benchmark from mongo for a given repo URL.
    """
    try:
        benchmark = get_benchmark_from_mongo(gitHubLink)
        return benchmark
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
