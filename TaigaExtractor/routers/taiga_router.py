from fastapi import APIRouter, HTTPException, Form, Body, Query
from typing import Optional

from services.taiga_service import taiga_login, taiga_get_projects

router = APIRouter()
@router.post("/login")
async def login(
    username: str = Form(...),
    password: str = Form(...)
):
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
    
@router.get("/projects")
async def get_user_info(
    userid: str = Query(...)
):
    """
    Get user information from the Taiga API.

    Args:
        userid (str): The ID of the user.

    Returns:
        dict: A dictionary containing user information.
    """
    try:
        response = taiga_get_projects(userid)
        return response
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))