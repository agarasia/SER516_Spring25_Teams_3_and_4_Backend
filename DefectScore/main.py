import os
from fastapi import FastAPI
from routers.defect_score_router import router as defect_score_router

# to load environment variables from .env
try:
    from dotenv import load_dotenv
    load_dotenv()
except ImportError:
    pass

app = FastAPI(
    title="Defect Score Microservice",
    description="Computes a defect score using GitHub Issues",
    version="0.1.0"
)

# Include the router
app.include_router(defect_score_router, prefix="/api/defect_score", tags=["DefectScore"])

@app.get("/")
def read_root():
    return {"message": "Defect Score microservice is running."}