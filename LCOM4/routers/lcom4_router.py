from datetime import datetime
# ,clone_repo

from fastapi import APIRouter, HTTPException, Request
from typing import Optional

from utilities.fetch_repo import fetch_repo 
from services.lcom4_calculator import calculate_lcom4
from services.project_parser import parse_java_files_in_dir

router = APIRouter()

@router.post("/lcom4")
async def calculate_lcom4_endpoint(request: Request):
    """
    Endpoint to compute LCOM4 from a GitHub URL.

    Requirements:
      - GitHub repo must already be cloned into /shared/repos.
      - Request body must contain: {"repo_url": "<github_repo_link>"}.

    Response:
      - JSON object with LCOM4 results for each Java class.
    """
    try:
        # Get JSON from request
        data = await request.json()
        gitHubLink = data.get("repo_url")

        if not gitHubLink:
            raise HTTPException(status_code=400, detail="Please provide a GitHub URL in 'repo_url'.")

        # headsh1, dir_path = clone_repo(gitHubLink)  
        # /un comment this line and clone_repo for testing
        # Fetch repo from shared volume
        fetch_result = fetch_repo(gitHubLink)

        if isinstance(fetch_result, dict) and "error" in fetch_result:
            raise HTTPException(status_code=400, detail=fetch_result["error"])

        head_sha, repo_dir = fetch_result

        # Parse Java classes and calculate LCOM4
        java_classes = parse_java_files_in_dir(repo_dir)
        results = [{"class_name": cls.name, "score": calculate_lcom4(cls)} for cls in java_classes]

        # Build API response
        response = {
            "timestamp": datetime.utcnow().isoformat() + "Z",
            "commit_sha": head_sha,
            "data": results
        }

        return response

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
