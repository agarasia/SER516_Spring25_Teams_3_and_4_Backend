import requests

def taiga_login(username: str, password: str):
    """
    Authenticate a user with the Taiga API.

    Args:
        username (str): The username/email of the user.
        password (str): The password of the user.

    Returns:
        dict: A dictionary containing the authentication token and user information.
    """
    url = "https://api.taiga.io/api/v1/auth"
    payload = {
        "username": username,
        "type": "normal",
        "password": password
    }
    headers = {
        'Content-Type': 'application/json'
    }

    response = requests.post(url, json=payload, headers=headers)

    if response.status_code == 200:
        return response.json()
    else:
        response.raise_for_status()

def taiga_get_projects(token: str, userid: str):
    """
    Get user information from the Taiga API.

    Args:
        token (str): The authentication token.
        userid (str): The ID of the user.

    Returns:
        dict: A dictionary containing user information.
    """
    url = f"https://api.taiga.io/api/v1/projects?member={userid}"
    headers = {
        'Content-Type': 'application/json'
    }

    response = requests.get(url, headers=headers)

    if response.status_code == 200:
        # Extract Project ID and Name
        projects = response.json()
        project_info = []
        for project in projects:
            project_info.append({
                "id": project['id'],
                "name": project['name']
            })
        return {
            "projects": project_info
        }


    else:
        response.raise_for_status()
