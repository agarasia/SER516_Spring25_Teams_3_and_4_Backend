from fastapi import FastAPI
from routers.lcomhs_router import router as lcomhs_router

app = FastAPI(
    title="LCOMHS Microservice",
    description="Computes LCOMHS metric for Java projects",
    version="0.1.0"
)

# Include the LCOMHS router
app.include_router(lcomhs_router, prefix="/api/lcomhs", tags=["LCOMHS"])

# Optionally, you could add root endpoint or health check
@app.get("/")
def read_root():
    return {"message": "LCOMHS Microservice is running."}