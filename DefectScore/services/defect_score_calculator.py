import requests
import math
from services.mongo_service import fetch_label_mapping_from_mongo

def compute_defect_score_from_github(repo_url: str, token: str = None) -> dict:
    """
    Fetch issues from a GitHub repository and calculate a defect score.

    Then compute:
      - total_defects
      - weighted_average_severity
      - min_severity
      - max_severity
      - standard_deviation_of_severity (optional or remove if not needed)

    :param repo_url: e.g. 'https://github.com/owner/repo'
    :param token: A GitHub personal access token if needed (private repo or higher rate limits).
    :return: A dict with defect stats.
    """
    # Parse out owner/repo from the URL
    #    e.g. "https://github.com/owner/repo" => owner="owner", repo="repo"
    if not repo_url.startswith("https://github.com/"):
        raise ValueError("Invalid GitHub URL. Must start with 'https://github.com/'")

    path_parts = repo_url.replace("https://github.com/", "").replace(".git", "").split("/")
    if len(path_parts) < 2:
        raise ValueError("Invalid GitHub URL. Expected 'https://github.com/owner/repo'")
    owner, repo = path_parts[0], path_parts[1]

    # Get labelMapping from Firebase
    label_severity_data = fetch_label_mapping_from_mongo(repo_url)
    # Build the GitHub Issues API endpoint
    issues_api_url = f"https://api.github.com/repos/{owner}/{repo}/issues"

    # We can retrieve open & closed issues to get the full picture
    params = {
        "state": "all",
        "per_page": 100
    }

    headers = {
        "Accept": "application/vnd.github+json"
    }
    if token:
        headers["Authorization"] = f"Bearer {token}"

    response = requests.get(issues_api_url, params=params, headers=headers)
    if response.status_code != 200:
        raise ValueError(
            f"GitHub API request failed ({response.status_code}).\nResponse: {response.text}"
        )
    issues = response.json()
    if not isinstance(issues, list):
        raise ValueError("Unexpected response format from GitHub Issues API (not a list).")

    # We will gather a list of severities from any issues that appear to be "defects"
    defect_severities = []
    
    for issue in issues:
        labels = issue.get("labels", [])
        label_names = [lbl["name"].lower() for lbl in labels if isinstance(lbl, dict)]

        # For each label, see if it maps to a known severity
        # Pick the MAX severity found among labels on this issue or skip if none matches
        issue_severity = 0
        label_severity_map = {}
        for data in label_severity_data:
            label_severity_map[data["key"]] = float(data["value"])
        for lname in label_names:
            if lname in label_severity_map:
                issue_severity = max(issue_severity, label_severity_map[lname])

        if issue_severity > 0:
            defect_severities.append(issue_severity)
    
    if not defect_severities:
        return {
            "total_defects": 0,
            "weighted_average_severity": 0,
            "min_severity": 0,
            "max_severity": 0,
            "std_dev_severity": 0,
            "details": "No issues matched defect labels. Defect Score = 0."
        }

    # Calculate stats
    total_defects = len(defect_severities)
    sum_severity = sum(defect_severities)
    max_sev = max(defect_severities)
    min_sev = min(defect_severities)

    # Weighted average
    weighted_avg = sum_severity / total_defects

    # Standard deviation
    mean = weighted_avg
    variance = sum((s - mean) ** 2 for s in defect_severities) / total_defects
    std_dev = math.sqrt(variance)

    return {
        "total_defects": total_defects,
        "weighted_average_severity": round(weighted_avg, 2),
        "min_severity": min_sev,
        "max_severity": max_sev,
        "std_dev_severity": round(std_dev, 2),
        "details": "Defect Score derived from GitHub Issues label mapping."
    }