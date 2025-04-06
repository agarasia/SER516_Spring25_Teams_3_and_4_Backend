from fastapi import APIRouter, HTTPException, Form, Body, Query
from typing import Optional

from services.taiga_service import taiga_login, taiga_get_projects, taiga_get_user_stories

router = APIRouter()
@router.post("/login")
async def login(
    username: str = Body(..., example= "test-user"),
    password: str = Body(..., example= "test-password")
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
    userid: str = Query(..., example= "test-user-id"),
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
    
@router.get("/userstories")
async def get_project_info(
    project_id: str = Query(..., example= "test-project-id"),
    token: str = Body(...),
):
    """
    Get project information from the Taiga API.

    Args:
        project_id (str): The ID of the project.
        token (str): The authentication token.

    Returns:
        dict: A dictionary containing project information.
    """
    try:
        response = taiga_get_user_stories(token, project_id)
        return response
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))