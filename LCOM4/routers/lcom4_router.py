from fastapi import APIRouter, HTTPException, Form, Body, Query
from typing import Optional

from services.lcom4_calculator import calculate_lcom4
from services.project_parser import parse_java_files_in_dir
from services.github_service import fetch_project_from_github, cleanup_dir
from services.mongo_service import to_doc_id, store_lcom4_data_in_mongo, fetch_lcom4_data_from_mongo, get_benchmark_from_mongo, store_benchmark_in_mongo
from datetime import datetime
import time

router = APIRouter()

@router.post("/calculate")
async def calculate_lcom4_endpoint(
    gitHubLink: Optional[str] = Form(
        None, description="GitHub URL if sourceType='git'. Ignored otherwise."
    )
):
    """
    Single endpoint to compute LCOM4 from either a GitHub URL.
    
    Request:
      - multipart/form-data
      - Fields:
        gitHubLink: The GitHub URL if sourceType="git"
    
    Response:
      - JSON object with LCOM4 results for each Java class.
    """
    temp_dir = None

    # Fetch or Unzip the project
    try:
        if not gitHubLink:
            raise HTTPException(status_code=400, detail="Please provide a GitHub URL in gitHubLink.")
            
        # Clone the GitHub repo to a temp directory
        temp_dir = fetch_project_from_github(gitHubLink)

        # Parse .java files and compute LCOM4
        java_classes = parse_java_files_in_dir(temp_dir)
        results = []
        for cls in java_classes:
            lcom4_value = calculate_lcom4(cls)
            results.append({"class_name": cls.name, "score": lcom4_value})


        history_data = fetch_lcom4_data_from_mongo(gitHubLink)
        current_timestamp = datetime.utcfromtimestamp(time.time()).isoformat() + "Z"

        current_data = {
            "timestamp": current_timestamp,
            "data": results,
            "gitUniqueId": to_doc_id(gitHubLink)   
        }

        store_lcom4_data_in_mongo(gitHubLink, results)


        return { "lcom4_history": history_data if history_data else [],
                 "current_lcom4": current_data }

    except Exception as e:
        # Wrap any exceptions in an HTTP 500
        raise HTTPException(status_code=500, detail=str(e))

    finally:
        # Cleanup temp files and directories
        if temp_dir:
            cleanup_dir(temp_dir)

@router.post("/benchmark")
def store_labels_for_project(
    sourceValue: str = Body(..., example="https://github.com/owner/repo"),
    benchmark: float = Body(..., example=1)
):
    """
    Stores custom benchmark -> user entered bench mark in mongo for a given repo URL.
    """
    try:
        store_benchmark_in_mongo(sourceValue, benchmark)
        return {"message": "benchmark stored successfully."}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/benchmark")
def store_labels_for_project(
    sourceValue: str = Query(..., example="https://github.com/owner/repo")
):
    """
    get benchmark -> benchmark from mongo for a given repo URL.
    """
    try:
        benchmark = get_benchmark_from_mongo(sourceValue)
        return {"lcom4_benchmark": benchmark}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))