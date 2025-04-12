import os
import re
import pymongo

# MongoDB Connection
MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017/")
DB_NAME = "benchmark_db"
COLLECTION_NAME = "benchmark_data"

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

def store_benchmark_in_mongo(repo_url: str, benchmarks: dict):
    """
    Stores (or updates) the benchmark data for a given repo URL.
    """
    doc_id = to_doc_id(repo_url)
    
    db[COLLECTION_NAME].update_one({"_id": doc_id}, {"$set": benchmarks}, upsert=True)

def get_benchmark_from_mongo(repo_url: str) -> dict:
    """
    Gets the benchmark data for a given repoUrl.
    Returns the benchmark data if it is present, otherwise returns an empty dict.
    """
    doc_id = to_doc_id(repo_url)
    document = db[COLLECTION_NAME].find_one({"_id": doc_id}, {"_id": 0})
    
    if document:
        return document
    else:
        return {}
