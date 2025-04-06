from fastapi import FastAPI, HTTPException
from routers import taiga_router

app = FastAPI(
    title="Taiga API",
    description="API for Taiga project management tool",
    version="1.0.0",
)

app.include_router(taiga_router.router, prefix="/api/taiga", tags=["Taiga"])

@app.get("/")
async def root():
    return {"message": "Welcome to the Taiga API!"}