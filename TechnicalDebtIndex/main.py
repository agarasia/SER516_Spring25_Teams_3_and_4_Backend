from fastapi import FastAPI
from routers.tdi_router import router as technical_debt_router

app = FastAPI(
    title="Technical Debt Index Service",
    description="Calculates the Technical Debt Index (TDI).",
    version="1.0.0"
)

# Include the router
app.include_router(technical_debt_router, prefix="/api/technical-debt", tags=["Technical Debt"])

@app.get("/")
def read_root():
    return {"message": "Technical Debt Index Service is running"}
