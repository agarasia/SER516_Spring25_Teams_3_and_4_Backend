from typing import List, Dict
from datetime import datetime

def calculate_tdi(stories: List[Dict]) -> Dict:
    """
    Calculates the Technical Debt Index (TDI).
    TDI = (Technical Debt Story Points / Total Story Points) * 100
    """
    total_story_points = sum(story.get("story_points", 0) for story in stories)
    tech_debt_points = sum(
        story.get("story_points", 0)
        for story in stories
        if story.get("type") == "technical_debt"
    )

    tdi = 0.0
    if total_story_points > 0:
        tdi = (tech_debt_points / total_story_points) * 100

    return {
        "technical_debt_index": round(tdi, 2),
        "total_story_points": total_story_points,
        "technical_debt_story_points": tech_debt_points,
        "technical_debt_items": [s["id"] for s in stories if s["type"] == "technical_debt"],
        "calculated_at": datetime.now().isoformat()
    }
