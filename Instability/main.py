from fastapi import FastAPI
from routers import instability

app = FastAPI(title="Instability Metric API")

# Register router
app.include_router(instability.router)

@app.get("/")
def read_root():
    return {"message": "Welcome to the Instability Metric API"}
