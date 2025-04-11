import os
import pymongo
from datetime import datetime

# MongoDB Connection
MONGO_URI = os.getenv("MONGO_URI", "mongodb://host.docker.internal:27017")
DB_NAME = "instability_db"

client = pymongo.MongoClient(MONGO_URI)
db = client[DB_NAME]


def store_instability_data_in_mongo(instability_result: dict):
    """
    Stores instability metric data. Assumes only one global context.
    """
    record = {
        "timestamp": datetime.utcnow().isoformat() + "Z",
        "instability_result": instability_result
    }
    db["instability_data"].insert_one(record)


def fetch_instability_data_from_mongo() -> list:
    """
    Returns all stored instability data (global, not repo-specific).
    """
    query = db["instability_data"].find({}, {"_id": 0})
    return list(query)


def store_instability_threshold(threshold: float):
    """
    Stores a global instability threshold.
    """
    db["metric_settings"].update_one(
        {"_id": "global"},
        {"$set": {"instability_threshold": threshold}},
        upsert=True
    )


def get_instability_threshold():
    """
    Gets the global instability threshold.
    """
    doc = db["metric_settings"].find_one(
        {"_id": "global"},
        {"_id": 0, "instability_threshold": 1}
    )
    return doc.get("instability_threshold", 0) if doc else 0
