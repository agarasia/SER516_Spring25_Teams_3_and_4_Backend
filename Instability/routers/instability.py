from fastapi import APIRouter, Request
from services.instability_service import process_instability

router = APIRouter(prefix="/instability", tags=["metrics"])

@router.post("/")
async def calculate_instability(request: Request):
    payload = await request.json()
    return process_instability(payload)
