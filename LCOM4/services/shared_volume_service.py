import os
import subprocess
from urllib.parse import urlparse
import re
from pathlib import Path
import fcntl
import git

SHARED_BASE_DIR = "/shared/repos"

def fetch_project_from_shared_volume(repo_url):
    if not repo_url:
        raise ValueError("No repository URL provided. Please enter a valid GitHub repository URL.")

    parsed = urlparse(repo_url)
    if parsed.netloc.lower() != "github.com" or parsed.path.count("/") < 2:
        raise ValueError("Invalid GitHub repository URL. Ensure it follows the format 'https://github.com/owner/repo'.")

    repo_path = parsed.path.strip("/")
    if repo_path.endswith(".git"):
        repo_path = repo_path[:-4]
    if not re.match(r"^[a-zA-Z0-9_.-]+/[a-zA-Z0-9_.-]+$", repo_path):
        raise ValueError("Malformed repository URL. Ensure the URL points to a valid GitHub repository.")

    owner, repo = repo_path.split("/")
    repo_dir = Path(SHARED_BASE_DIR) / owner / repo
    
    if not repo_dir.exists():
        return {"error": "Clone the repo first."}
    
    try:
        repo = git.Repo(repo_dir)
        head_sha = repo.head.commit.hexsha
        return head_sha, str(repo_dir)
    except Exception as e:
        return {"error": f"Error accessing repository: {str(e)}"}


# import tempfile
# import shutil
# import subprocess

# def fetch_project_from_shared_volume(github_url: str) -> str:
#     """
#     Fetch the project from shared volume and return the repo
#     """
#     temp_dir = tempfile.mkdtemp(prefix="repo_")

#     try:
#         subprocess.run(["git", "clone", github_url, temp_dir], check=True)
#     except subprocess.CalledProcessError as e:
#         #  if clone fails
#         cleanup_dir(temp_dir)
#         raise RuntimeError(f"Failed to clone {github_url}: {e}")

#     return temp_dir

# def cleanup_dir(path: str):
#     """
#     Removes the temporary project directory to free up space.
#     """
#     shutil.rmtree(path, ignore_errors=True)