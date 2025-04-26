import os
import subprocess
from urllib.parse import urlparse
import re
from pathlib import Path
import fcntl
import git

SHARED_BASE_DIR = "/shared/repos"

def fetch_repo(repo_url):
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
