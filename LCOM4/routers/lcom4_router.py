from services.shared_volume_service import fetch_repo
# ,clone_repo

from fastapi import APIRouter, HTTPException, Form
from typing import Optional

from services.lcom4_calculator import calculate_lcom4
from services.project_parser import parse_java_files_in_dir
from services.github_service import fetch_project_from_github, cleanup_dir
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

        # headsh1, dir_path = clone_repo(gitHubLink)  /un comment this line and clone_repo for testing

        # fetch the GitHub repo from shared volume
        headsh, temp_dir = fetch_repo(gitHubLink)

        # Parse .java files and compute LCOM4
        java_classes = parse_java_files_in_dir(temp_dir)
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

    finally:
        # Cleanup temp files and directories
        if temp_dir:
            cleanup_dir(temp_dir)

