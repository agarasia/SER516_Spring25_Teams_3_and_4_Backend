from fastapi import APIRouter, HTTPException, Request
from datetime import datetime

from utilities.fetch_repo import fetch_repo
from services.lcomhs_calculator import calculate_lcomhs
from services.project_parser import parse_java_files_in_dir

router = APIRouter()

@router.post("/")
async def calculate_lcomhs_endpoint(request: Request):
    """
    Endpoint to compute LCOMHS from a GitHub URL.

    Requirements:
      - GitHub repo must already be cloned into /shared/repos.
      - Request body must contain: {"repo_url": "<github_repo_link>"}.

    Response:
      - JSON object with LCOMHS results for each Java class.
    """
    try:
        # Get JSON from request
        data = await request.json()
        gitHubLink = data.get("repo_url")

        if not gitHubLink:
            raise HTTPException(status_code=400, detail="Please provide a GitHub URL in 'repo_url'.")

        # Fetch repo from shared volume
        fetch_result = fetch_repo(gitHubLink)

        if isinstance(fetch_result, dict) and "error" in fetch_result:
            raise HTTPException(status_code=400, detail=fetch_result["error"])

        head_sha, repo_dir = fetch_result

        # Parse Java classes and calculate LCOMHS
        java_classes = parse_java_files_in_dir(repo_dir)
        results = [{"class_name": cls.name, "score": calculate_lcomhs(cls)} for cls in java_classes]

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
