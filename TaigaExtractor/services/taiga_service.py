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

def taiga_get_projects(userid: str):
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
        'Content-Type': 'application/json',
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

def taiga_get_user_stories(token: str, project_id: str, include_activity: bool = True):
    """
    Get user stories with optional activity data for a specific project.

    Args:
        token (str): Authentication token
        project_id (str): Target project ID
        include_activity (bool): Whether to fetch activity timeline

    Returns:
        list: User stories with selected fields
    """
    url = f"https://api.taiga.io/api/v1/userstories?project={project_id}"
    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'Bearer {token}'  # Critical authentication header
    }

    response = requests.get(url, headers=headers)
    response.raise_for_status()
    
    user_stories = []
    for story in response.json():
        story_data = {
            "id": story['id'],
            "name": story['subject'],
            "status": story.get('status_extra_name', ''),
            "points": story.get('total_points', None)
        }

        if include_activity:
            timeline_url = f"https://api.taiga.io/api/v1/history/userstory/{story['id']}"
            timeline_response = requests.get(timeline_url, headers=headers)
            if timeline_response.status_code == 200:
                story_data['activity'] = [entry['comment'] for entry in timeline_response.json()]

        user_stories.append(story_data)

    return user_stories
