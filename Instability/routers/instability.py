from fastapi import APIRouter, Request, Query
from services.instability_service import process_instability
# from services.mongo_service import (
#     fetch_instability_data_from_mongo,
#     store_instability_threshold,
#     get_instability_threshold
# )

router = APIRouter(prefix="/instability", tags=["metrics"])

# POST /instability — calculate and store
@router.post("/calculate")
async def calculate_instability(request: Request):
    payload = await request.json()
    return process_instability(payload)

# # GET /instability/history — fetch stored results
# @router.get("/history")
# def get_instability_history():
#     return fetch_instability_data_from_mongo()

# # POST /instability/threshold — set global threshold
# @router.post("/threshold")
# def post_threshold(threshold: float = Query(...)):
#     store_instability_threshold(threshold)
#     return {"message": "Threshold saved", "threshold": threshold}

# # GET /instability/threshold — get global threshold
# @router.get("/threshold")
# def get_threshold():
#     threshold = get_instability_threshold()
#     return {"threshold": threshold}
