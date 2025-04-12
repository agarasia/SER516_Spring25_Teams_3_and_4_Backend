import os
import re
import pymongo
import time
from datetime import datetime

# MongoDB Connection
MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017/")
DB_NAME = "lcom4_db"
COLLECTION_NAME = "lcom4_data"

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

def store_lcom4_data_in_mongo(repo_url: str, label_map: dict):
    """
    Stores (or updates) the lcom4_data for a given repo URL.
    """
    doc_id = to_doc_id(repo_url)
    data = {
        "timestamp": datetime.utcnow().isoformat() + "Z",
        "data": label_map,
        "gitUniqueId": doc_id,
    }
    db["lcom4_data"].insert_one(data)

def fetch_lcom4_data_from_mongo(repo_url: str) -> list:
    """
    Gets the lcom4_data for a given repoUrl.
    Returns an array of data without the MongoDB _id field.
    """
    doc_id = to_doc_id(repo_url)
    query = db["lcom4_data"].find({"gitUniqueId": doc_id}, {"_id": 0})
    return list(query)

def store_benchmark_in_mongo(repo_url: str, benchmark: float):
    """
    Sets the lcom4 benchmark for a given repoUrl.
    Returns success if update is successful.
    """
    doc_id = to_doc_id(repo_url)
    db["metric_settings"].update_one({"_id": doc_id}, {"$set": {"lcom4_benchmark": benchmark}}, upsert=True)

def get_benchmark_from_mongo(repo_url: str):
    """
    Gets the lcom4 benchmark for a given repoUrl.
    Returns the benchmark value if it is present, otherwise returns 0.
    """
    doc_id = to_doc_id(repo_url)
    document = db["metric_settings"].find_one({"_id": doc_id}, {"_id": 0, "lcom4_benchmark": 1})
    return document.get("lcom4_benchmark", 0) if document else 0
