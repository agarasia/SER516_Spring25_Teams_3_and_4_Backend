from fastapi import FastAPI
from routers.lcom4_router import router as lcom4_router

app = FastAPI(
    title="LCOM4 Microservice",
    description="Computes LCOM4 metric for Java projects",
    version="0.1.0"
)

# Include the LCOM4 router
app.include_router(lcom4_router, prefix="/api/lcom4", tags=["LCOM4"])

# Optionally, you could add root endpoint or health check
@app.get("/")
def read_root():
    return {"message": "LCOM4 Microservice is running."}
