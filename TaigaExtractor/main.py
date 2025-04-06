from fastapi import FastAPI
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

@app.post("/login")
async def login(username: str, password: str):
    """
    Authenticate a user with the Taiga API.

    Args:
        username (str): The username/email of the user.
        password (str): The password of the user.

    Returns:
        dict: A dictionary containing the authentication token and user information.
    """
    try:
        response = taiga_login(username, password)
        return response
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))