from fastapi import APIRouter, HTTPException, Body
from typing import List, Optional
from pydantic import BaseModel, Field
from services.tdi_service import calculate_tdi

router = APIRouter()

# Input schema
class Story(BaseModel):
    id: str
    title: Optional[str] = None
    story_points: int = Field(..., ge=0)
    type: str = Field(..., description="feature, defect, technical_debt, etc.")

class StoriesRequest(BaseModel):
    stories: List[Story]

@router.post("/calculate")
def calculate_technical_debt_index(request: StoriesRequest = Body(...)):
    """
    Calculate the Technical Debt Index (TDI) from a list of user stories.
    """
    try:
        stories = [story.dict() for story in request.stories]
        result = calculate_tdi(stories)
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error calculating TDI: {str(e)}")
