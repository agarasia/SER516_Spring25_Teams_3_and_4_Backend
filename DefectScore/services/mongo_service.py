import os
import re
import pymongo
from datetime import datetime
import time

# MongoDB Connection
MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017/")
DB_NAME = "defect_score_db"
COLLECTION_NAME = "metric_settings"

client = pymongo.MongoClient(MONGO_URI)
db = client[DB_NAME]
collection = db[COLLECTION_NAME]

def to_doc_id(repo_url: str) -> str:
    """
    Convert 'https://github.com/owner/repo' -> 'owner_repo'
    or some sanitized doc ID for MongoDB.
    """
    repo_url = repo_url.replace("https://github.com/", "").replace(".git", "")
    doc_id = re.sub(r"[^a-zA-Z0-9_]+", "_", repo_url).lower()
    return doc_id

def store_label_mapping_in_mongo(repo_url: str, label_map: list):
    """
    Stores (or updates) the labelSeverityMap for a given repo URL.
    """
    doc_id = to_doc_id(repo_url)
    data = {
        "_id": doc_id,
        "repoUrl": repo_url,
        "labelSeverityMap": label_map
    }
    collection.update_one({"_id": doc_id}, {"$set": data}, upsert=True)

def fetch_label_mapping_from_mongo(repo_url: str) -> list:
    """
    Gets the labelSeverityMap for a given repoUrl.
    Returns a dict of label->severity, or a default mapping if not found.
    """
    doc_id = to_doc_id(repo_url)
    document = collection.find_one({"_id": doc_id})
    default_label_severity_map = [
        {"key": "bug", "value": 2},
        {"key": "minor", "value": 2},
        {"key": "major", "value": 4},
        {"key": "critical", "value": 5},
        {"key": "high", "value": 5},
        {"key": "low", "value": 1}
    ]
    return document.get("labelSeverityMap", default_label_severity_map) if document else default_label_severity_map

def store_def_score_data_in_mongo(repo_url: str, label_map: dict):
    """
    Stores (or updates) the defect score data for a given repo URL.
    """
    doc_id = to_doc_id(repo_url)
    data = {
        "timestamp": datetime.utcnow().isoformat() + "Z",
        "data": label_map,
        "gitUniqueId": doc_id
    }
    db["defect_score_data"].insert_one(data)

def fetch_def_score_data_from_mongo(repo_url: str) -> list:
    """
    Gets the defect score data for a given repoUrl.
    Returns an array of data.
    """
    doc_id = to_doc_id(repo_url)
    query = db["defect_score_data"].find({"gitUniqueId": doc_id}, {"_id": 0})
    return list(query)

def store_benchmark_in_mongo(repo_url: str, benchmark: float):
    """
    Sets the defect score benchmark for a given repoUrl.
    Returns success if update is successful.
    """
    doc_id = to_doc_id(repo_url)
    collection.update_one({"_id": doc_id}, {"$set": {"defect_score_benchmark": benchmark}}, upsert=True)

def get_benchmark_from_mongo(repo_url: str):
    """
    Gets the defect score benchmark for a given repoUrl.
    Returns the benchmark value if it is present, otherwise returns 0.
    """
    doc_id = to_doc_id(repo_url)
    document = collection.find_one({"_id": doc_id})
    return document.get("defect_score_benchmark", 0) if document else 0
