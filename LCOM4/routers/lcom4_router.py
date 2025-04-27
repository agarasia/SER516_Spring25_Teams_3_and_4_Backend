from fastapi import APIRouter, HTTPException, Form, Body, Query 
from typing import Optional

from services.lcom4_calculator import calculate_lcom4
from services.project_parser import parse_java_files_in_dir
from services.shared_volume_service import fetch_project_from_shared_volume
from flask import jsonify
from datetime import datetime
import time
import git

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

    # Get the project from Shared Volune
    try:
        if not gitHubLink:
            raise HTTPException(status_code=400, detail="Please provide a GitHub URL in gitHubLink.")
            
        # Clone the GitHub repo to a temp directory
        head_sha, repo_path = fetch_project_from_shared_volume(gitHubLink)

        repo_dir = git.Repo(repo_path) 
        
        if isinstance(repo_dir, dict) and "error" in repo_dir:
            return jsonify({"error": repo_dir["error"]}), 200

        # Parse .java files and compute LCOM4
        java_classes = parse_java_files_in_dir(repo_dir)
        results = []
        for cls in java_classes:
            lcom4_value = calculate_lcom4(cls)
            results.append({"class_name": cls.name, "score": lcom4_value})

        current_timestamp = datetime.utcfromtimestamp(time.time()).isoformat() + "Z"

        response = {
            "timestamp": current_timestamp,
            "data": results
        }

        return response

    except Exception as e:
        # Wrap any exceptions in an HTTP 500
        raise HTTPException(status_code=500, detail=str(e))
    
