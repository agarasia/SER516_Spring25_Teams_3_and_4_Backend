import os
from fastapi import APIRouter, HTTPException, Form, Body, Query
from typing import Optional, List
from services.defect_score_calculator import compute_defect_score_from_github
from services.mongo_service import to_doc_id,store_label_mapping_in_mongo,\
    fetch_label_mapping_from_mongo, \
    store_def_score_data_in_mongo, \
    fetch_def_score_data_from_mongo, \
    store_benchmark_in_mongo, \
    get_benchmark_from_mongo
from datetime import datetime
import time

router = APIRouter()

@router.post("/calculate")
async def calculate_defect_score(
    sourceValue: Optional[str] = Form(None, description="GitHub repo URL"),
    token: Optional[str] = Form(None, description="GitHub token if needed for private repos")
):
    """
    Endpoint to compute a 'defect score' by retrieving issues from a GitHub repo
    """

    try:
        if not sourceValue:
            raise HTTPException(
                status_code=400, 
                detail="Please provide a GitHub repo URL in sourceValue"
            )

        # If no token provided in the request, try environment variable
        if not token:
            token = os.getenv("GITHUB_TOKEN", None)

        # Compute the defect score from the GH Issues
        result = compute_defect_score_from_github(sourceValue, token)

        history_data = fetch_def_score_data_from_mongo(sourceValue)
        current_timestamp = datetime.utcfromtimestamp(time.time()).isoformat() + "Z"

        current_data = {
            "timestamp": current_timestamp,
            "data": result,
            "gitUniqueId": to_doc_id(sourceValue)   
        }

        store_def_score_data_in_mongo(sourceValue, result)

        return { "defect_score_history": history_data if history_data else [],
                 "current_defect_score": current_data }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    

@router.post("/labelsMapping")
def store_labels_for_project(
    sourceValue: str = Body(..., example="https://github.com/owner/repo"),
    labelSeverityMap: List = Body(..., example=[{"key": "bug", "value": 2}, {"key": "critical", "value": 5}])
):
    """
    Stores custom label -> severity mapping in mongo for a given repo URL.
    """
    try:
        store_label_mapping_in_mongo(sourceValue, labelSeverityMap)
        return {"message": "Label severity map stored successfully."}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/labelsMapping")
def fetch_labels_for_project(
    sourceValue: str = Query(..., example="https://github.com/owner/repo")
):
    """
    Get custom label -> severity mapping from mongo for a given repo URL.
    """
    try:
        label_mapping = fetch_label_mapping_from_mongo(sourceValue)
        if not label_mapping:
            return [
                {
                    "key": "bug",
                    "value": 2
                },
                {
                    "key": "minor",
                    "value": 2
                },
                {
                    "key": "major",
                    "value": 4
                },
                {
                    "key": "critical",
                    "value": 5
                },
                {
                    "key": "high",
                    "value": 5
                },
                {
                    "key": "low",
                    "value": 1
                }
            ]
        return label_mapping
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    
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
        return {"defect_score_benchmark": benchmark}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))